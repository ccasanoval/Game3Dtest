package com.cesoft.cesdoom.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.signals.Signal
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.assets.Sounds
import com.cesoft.cesdoom.components.*
import com.cesoft.cesdoom.entities.Player
import com.cesoft.cesdoom.events.*
import com.cesoft.cesdoom.managers.EnemyActions
import com.cesoft.cesdoom.managers.EnemyFactory
import com.cesoft.cesdoom.managers.MazeFactory
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class EnemySystem(
		eventSignal: Signal<EnemyEvent>,
		private val gameEventSignal: Signal<GameEvent>,
		private val bulletEventSignal: Signal<BulletEvent>,
		private val renderEventSignal: Signal<RenderEvent>,
		assets: Assets
) : EntitySystem(), EntityListener {

	companion object {
		private val tag: String = EnemySystem::class.java.simpleName
		private const val ATTACK_RADIO = EnemyComponent.RADIO + PlayerComponent.RADIO + 4
	}

	private var player: Player? = null
	private val enemyFactory = EnemyFactory(assets)
	private val enemyQueue = EnemyQueue()
	init {
		Log.e(tag, "INI ---------------------------------------------------------")
		eventSignal.add(enemyQueue)
	}

	/// Implements EntityListener
	//______________________________________________________________________________________________
	override fun entityRemoved(entity: Entity) = Unit
	override fun entityAdded(entity: Entity) {
		player = entity as Player
	}

	/// Extends EntitySystem
	//______________________________________________________________________________________________
	override fun addedToEngine(e: Engine) {
		enemyFactory.enemies = e.getEntitiesFor(Family.all(EnemyComponent::class.java, StatusComponent::class.java).get()) as ImmutableArray<Entity>
		e.addEntityListener(Family.one(PlayerComponent::class.java).get(), this)
	}

	//______________________________________________________________________________________________
	override fun update(delta: Float) {
		processEvents()
		updateEnemies(delta)
		enemyFactory.spawnIfNeeded(engine)
	}

	//______________________________________________________________________________________________
	private fun processEvents() {
		for(event in enemyQueue.events) {
			when(event.type) {
				EnemyEvent.Type.HURT -> {
					hurt(event.enemy)
				}
				//else -> Unit
			}
		}
	}
	private fun updateEnemies(delta: Float) {
		enemyFactory.enemies.let { enemies ->
			for(entity in enemies) {
				val posPlayer = player!!.getPosition().cpy()
				updateEnemy(entity, delta, posPlayer)
			}
		}
	}


	//______________________________________________________________________________________________
	private fun updateEnemy(entity: Entity, delta: Float, posPlayer: Vector3) {

		updateDeath(entity, delta)
		if(isDeadOver(entity))return
		///
		updateMovement(entity, posPlayer, delta)
		///
		AnimationComponent.get(entity).update(delta)
	}

	//______________________________________________________________________________________________
	private fun updateDeath(entity: Entity, delta: Float) {
		val status = StatusComponent.get(entity)
		updateStatusSys(entity, delta)
		if(status.isDead()) {
			if(deathProgress(entity) == 0f)
				Sounds.play(Sounds.SoundType.ENEMY_DIE)

			val model = ModelComponent.get(entity)
			if(model.blendingAttribute != null)
				model.blendingAttribute!!.opacity = 1 - deathProgress(entity)
		}
	}
	//______________________________________________________________________________________________
	private var lastAttack = 0L
	private fun updateMovement(entity: Entity, playerPosition: Vector3, delta: Float) {

		val force: Float
		val enemy = EnemyComponent.get(entity)
		val model = ModelComponent.get(entity)

		model.instance.transform.getTranslation(enemy.posTemp)
		enemy.currentPos2D = Vector2(enemy.posTemp.x, enemy.posTemp.z)
		val distPlayer = enemy.posTemp.dst(playerPosition)

		val statusMov = statusMov(entity, distPlayer)
		when(statusMov) {
			StatusMov.QUIET -> {
				force = 0f
			}
			StatusMov.ATTACK -> {
				setAttacking(entity)
				val now = System.currentTimeMillis()
				if(now > lastAttack + EnemyComponent.DELAY_ATTACK) {
					lastAttack = now
					gameEventSignal.dispatch(GameEvent(GameEvent.Type.PLAYER_HURT, EnemyComponent.BITE_PAIN))
					Sounds.play(Sounds.SoundType.ENEMY_ATTACK)
				}
				force = 0f
			}
			StatusMov.RUN -> {
				force = if (CesDoom.isMobile) 1600f else 2200f
				setRunning(entity)
			}
			StatusMov.WALK -> {
				force = if (CesDoom.isMobile) 800f else 900f
				setWalking(entity)
			}
		}

		/// Si hay movimiento, calcula el camino
		//if( ! status.isAttacking() && force != 0f)
		if (statusMov == StatusMov.WALK || statusMov == StatusMov.RUN) {
			calcPath(entity, playerPosition)
		}

		/// Mueve
		val isWalking = distPlayer > 2*ATTACK_RADIO && (statusMov == StatusMov.WALK || statusMov == StatusMov.RUN)
		val isQuiet = statusMov == StatusMov.QUIET
		moveEnemy(entity, playerPosition, isWalking, isQuiet, force, delta)
	}
	//______________________________________________________________________________________________
	private fun moveEnemy(entity: Entity, playerPosition: Vector3, isWalking: Boolean, isQuiet: Boolean, force: Float, delta: Float) {

		val enemy = EnemyComponent.get(entity)
		val rigidBody = BulletComponent.get(entity).rigidBody

		val model = ModelComponent.get(entity)
		model.instance.transform.getTranslation(enemy.posTemp)
		var dX = playerPosition.x - enemy.posTemp.x
		var dZ = playerPosition.z - enemy.posTemp.z

		/// Set velocity
		val dir = enemy.nextStep3D.add(enemy.posTemp.scl(-1f)).nor().scl(force * delta)
		dir.y = rigidBody.linearVelocity.y
		rigidBody.linearVelocity = dir

		/// Calc orientation
		val transf = Matrix4()
		rigidBody.getWorldTransform(transf)
		transf.getTranslation(enemy.posTemp)
		if(isWalking) {
			dX = rigidBody.linearVelocity.x
			dZ = rigidBody.linearVelocity.z
		}
		if( ! isQuiet) {
			val theta = Math.atan2(dX.toDouble(), dZ.toDouble())
			if(theta * enemy.orientation < 0) {
				enemy.orientation = theta
			}
			else {
				val weight = 5.0//TODO: referenciar a delta
				enemy.orientation = (weight * enemy.orientation + theta) / (weight + 1)
			}
		}
		val rot = Quaternion().setFromAxis(0f, 1f, 0f, Math.toDegrees(enemy.orientation).toFloat())

		/// Set position and rotation
		model.instance.transform.set(enemy.posTemp, rot)
	}

	private fun calcPath(entity: Entity, playerPosition: Vector3) {

		val enemy = EnemyComponent.get(entity)
		val player2D = Vector2(playerPosition.x, playerPosition.z)
		val map = MazeFactory.mapFactory.map

		//TODO: Si player cambia mucho la posicion, obliga a recalcular
		enemy.stepCounter++ //Obliga a recalcular pase lo que pase cada x ciclos
		if (enemy.stepCounter % 20 == 0 || enemy.pathIndex == 0 || enemy.pathIndex >= enemy.path!!.size) {
			//timePathfinding = System.currentTimeMillis()
			enemy.path = map.findPath(enemy.currentPos2D, player2D)
			enemy.path?.let { path ->
				if (path.size > 1) {
					enemy.pathIndex = 2
					enemy.stepCalc2D = path[1]
					enemy.nextStep3D = Vector3(enemy.stepCalc2D.x, enemy.posTemp.y, enemy.stepCalc2D.y)
				} else
					enemy.nextStep3D = Vector3(player2D.x, enemy.posTemp.y, player2D.y)
			}
		} else {
			val next = enemy.path!![enemy.pathIndex]

			if (enemy.currentPos2D.dst(next) < 5) {
				enemy.pathIndex++
			}
			if (enemy.pathIndex >= enemy.path!!.size) {
				enemy.pathIndex = 0
			} else {
				enemy.stepCalc2D = next
				enemy.nextStep3D = Vector3(enemy.stepCalc2D.x, enemy.posTemp.y, enemy.stepCalc2D.y)
			}
		}
		//Log.e(tag, "$id PATH---------******************::: $stepCalc2D")
		//Log.e(tag, "$id ENEMY--------- $currentPos2D")
		//Log.e(tag, "$id PLAYER--------- $player2D")
	}

	private enum class StatusMov { QUIET, ATTACK, RUN, WALK }

	private fun statusMov(entity: Entity, distPlayer: Float): StatusMov {
		val enemy = EnemyComponent.get(entity)
		val status = StatusComponent.get(entity)
		/// No está en condiciones de atacar: herido, muerto o sobre el suelo
		return if (status.isAching() || status.isDead() || enemy.posTemp.y > EnemyComponent.RADIO + 2)
			StatusMov.QUIET
		/// Esta al lado, atacale (Las colisiones no valen, porque aqui ignoro el estado)
		else if(distPlayer < ATTACK_RADIO)
			StatusMov.ATTACK
		/// Esta cerca, corre a por el
		else if (distPlayer < 180f)
			StatusMov.RUN
		/// Esta lejos, camina buscando
		else
			StatusMov.WALK
	}



	private fun updateStatusSys(entity: Entity, delta: Float) {
		updateStatus(entity, delta)
		val status = StatusComponent.get(entity)
		if(status.isDead()) {
			when {
				deathProgress(entity) == 0f -> {
					gameEventSignal.dispatch(GameEvent.EnemyDead)
				}
				deathProgress(entity) >= 1f -> {
					status.alive = false
					engine.removeEntity(entity)
					gameEventSignal.dispatch(GameEvent(GameEvent.Type.ENEMY_DEAD, EnemyComponent.KILL_REWARD, entity))
					//entity.remove(StatusComponent::class.java)
					//TODO: val reward = EnemyComponent.get(entity).reward by type of enemy
				}
			}
		}
	}

	private fun updateStatus(entity: Entity, delta: Float) {
		val status = StatusComponent.get(entity)
		if( ! status.isDead() && status.health < 0) {
			removeCollider(entity)
			playDying(entity)
			setDeadState(entity)
		} else if (status.isDead()) {
			status.deadStateTime += delta
		} else if (status.isAching()) {
			status.achingStateTime += delta
			if (isAchingOver(entity)) {
				status.achingStateTime = 0f
				status.estado = EnemyComponent.ACTION.REINCARNATING
				playReincarnating(entity)
			}
		}
	}

	private fun removeCollider(entity: Entity) {
		bulletEventSignal.dispatch(BulletEvent(BulletEvent.Type.REMOVE, entity))
	}


	//______________________________________________________________________________________________
	private fun playReincarnating(entity: Entity) {
		EnemyActions.setAnimation(entity, EnemyComponent.ACTION.REINCARNATING)
	}
	//______________________________________________________________________________________________
	private fun playRunning(entity: Entity) {
		EnemyActions.setAnimation(entity, EnemyComponent.ACTION.RUNNING)
	}
	//______________________________________________________________________________________________
	private fun playWalking(entity: Entity) {
		EnemyActions.setAnimation(entity, EnemyComponent.ACTION.WALKING)
	}
	//______________________________________________________________________________________________
	private fun playAttack(entity: Entity) {
		EnemyActions.setAnimation(entity, EnemyComponent.ACTION.ATTACKING)
	}
	//______________________________________________________________________________________________
	private fun playAching(entity: Entity) {
		EnemyActions.setAnimation(entity, EnemyComponent.ACTION.ACHING)
	}
	//______________________________________________________________________________________________
	private fun playDying(entity: Entity) {
		EnemyActions.setAnimation(entity, EnemyComponent.ACTION.DYING)

		/// Particle Effects
		val enemy = EnemyComponent.get(entity)
		val model = ModelComponent.get(entity)
		val effect = enemy.particleEffect!!
		val emitter = effect.controllers.first().emitter as RegularEmitter
		emitter.emissionMode = RegularEmitter.EmissionMode.EnabledUntilCycleEnd
		effect.setTransform(model.instance.transform)
		effect.scale(5f, 8f, 5f)
		effect.init()
		effect.start()
		renderEventSignal.dispatch(RenderEvent(RenderEvent.Type.ADD_PARTICLE_FX, effect))
		//game.render.addParticleEffect(effect)//TODO: remove this reference... Event!
	}


	////////////////////////////////////////////////////////////////////////////////////////////////

	private fun setDeadState(entity: Entity) {
		val status = StatusComponent.get(entity)
		status.estado = EnemyComponent.ACTION.DYING
		status.deadStateTime = 0f
	}

	private fun isDeadOver(entity: Entity) = deathProgress(entity) > 1
	private fun deathProgress(entity: Entity): Float {
		val status = StatusComponent.get(entity)
		val enemy = EnemyComponent.get(entity)
		return status.deadStateTime / EnemyActions.getActionDuration(EnemyComponent.ACTION.DYING, enemy.type)
	}


	private fun isAchingOver(entity: Entity): Boolean {
		val status = StatusComponent.get(entity)
		val enemy = EnemyComponent.get(entity)
		return status.achingStateTime > EnemyActions.getActionDuration(EnemyComponent.ACTION.ACHING, enemy.type)
	}

	private fun setAttacking(entity: Entity) {
		val status = StatusComponent.get(entity)
		if (status.isDead() || status.isAching()) return
		if (!status.isAttacking()) {
			status.setAttackingState()
			playAttack(entity)
		}
	}

	private fun hurt(entity: Entity, pain: Float = 50f) {
		val status = StatusComponent.get(entity)
		if (status.isDead()) return
		if (!status.isAching()) {
			status.health -= pain
			status.achingStateTime = 0f
			status.setAchingState()
			playAching(entity)
			Sounds.play(Sounds.SoundType.ENEMY_HURT)
		} else {
			status.health -= pain/4f
		}
	}

	private fun setRunning(entity: Entity) {
		val status = StatusComponent.get(entity)
		if(status.isDead() || status.isAching()) return
		if(!status.isRunning()) {
			status.setRunningState()
			playRunning(entity)
		}
	}

	private fun setWalking(entity: Entity) {
		val status = StatusComponent.get(entity)
		if(status.isDead() || status.isAching()) return
		if( ! status.isWalking()) {
			status.setWalkingState()
			playWalking(entity)
		}
	}

}