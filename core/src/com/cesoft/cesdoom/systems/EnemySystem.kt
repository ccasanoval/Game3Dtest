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
import com.cesoft.cesdoom.managers.*


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
		private fun attackRadio(enemy: EnemyComponent) = PlayerComponent.RADIO + 4 + enemy.radio
	}

	private var player: Player? = null
	private val enemyFactory = EnemyFactory(assets)
	private val enemyQueue = EnemyQueue()
	init {
		//Log.e(tag, "INI ---------------------------------------------------------")
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
				updateEnemy(entity, delta)
			}
		}
	}


	//______________________________________________________________________________________________
	private fun updateEnemy(entity: Entity, delta: Float) {

		updateDeath(entity, delta)
		if(isDeadOver(entity))return
		///
		updateMovement(entity, delta)
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
	private fun updateMovement(entity: Entity, delta: Float) {

		val playerPosition = player!!.getPosition().cpy()

		val force: Float
		val enemy = EnemyComponent.get(entity)
		val model = ModelComponent.get(entity)

		model.instance.transform.getTranslation(enemy.position)
		enemy.currentPos2D = Vector2(enemy.position.x, enemy.position.z)
		val distPlayer = enemy.position.dst(playerPosition)

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
				force = forceRun(enemy)
				setRunning(entity)
			}
			StatusMov.WALK -> {
				force = forceWalk(enemy)
				setWalking(entity)
			}
		}

		/// Si hay movimiento, calcula el camino
		//if( ! status.isAttacking() && force != 0f)
		if (statusMov == StatusMov.WALK || statusMov == StatusMov.RUN) {
			calcPath(entity, playerPosition)
		}

		/// Mueve
		val isWalking = distPlayer > 2*attackRadio(enemy) && (statusMov == StatusMov.WALK || statusMov == StatusMov.RUN)
		val isQuiet = statusMov == StatusMov.QUIET
		moveEnemy(entity, playerPosition, isWalking, isQuiet, force, delta)
	}
	private fun forceRun(enemy: EnemyComponent): Float {
		return if(CesDoom.isMobile) {
			if(enemy.type == EnemyComponent.TYPE.MONSTER0)
				1600f
			else
				1000f
		}
		else {
			2200f
		}
	}
	private fun forceWalk(enemy: EnemyComponent) = if(CesDoom.isMobile) 800f else 900f
	//______________________________________________________________________________________________
	private fun moveEnemy(entity: Entity, playerPosition: Vector3, isWalking: Boolean, isQuiet: Boolean, force: Float, delta: Float) {

		val enemy = EnemyComponent.get(entity)
		val rigidBody = BulletComponent.get(entity).rigidBody

		val model = ModelComponent.get(entity)
		model.instance.transform.getTranslation(enemy.position)

		/// Set velocity
		val dir = enemy.nextStep3D.add(enemy.position.scl(-1f)).nor().scl(force * delta)
		dir.y = rigidBody.linearVelocity.y
		rigidBody.linearVelocity = dir

		/// Calc orientation
		val transf = Matrix4()
		rigidBody.getWorldTransform(transf)
		transf.getTranslation(enemy.position)

		val dX: Float
		val dZ: Float
		if(isWalking) {
			dX = rigidBody.linearVelocity.x
			dZ = rigidBody.linearVelocity.z
		}
		else {
			dX = playerPosition.x - enemy.position.x
			dZ = playerPosition.z - enemy.position.z
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
		model.instance.transform.set(enemy.position, rot)
	}

	//----------------------------------------------------------------------------------------------
	//TODO: Hacer que enemigo busque en ambas plantas...
	private fun calcPath(entity: Entity, playerPosition: Vector3) {

		val enemy = EnemyComponent.get(entity)
		var recalcular = false

		val levelEnemy = if(enemy.position.y > 2 * WallFactory.HIGH) 1 else 0
		val levelPlayer = if(playerPosition.y > 2 * WallFactory.HIGH) 1 else 0

		//Log.e(tag, "LEVELS ----------------------------------------------- $levelEnemy  <>  $levelPlayer      id=${enemy.id}  player2D=${enemy.player2D} ")

		//Distintas plantas: Buscar en mapa de accessos
		if(levelEnemy != levelPlayer && !enemy.isAccessLevelPath) {
			enemy.isAccessLevelPath = true
			val map = MazeFactory.mapFactory.map[levelEnemy]
			val access = map.getNearerLevelAccess(enemy.currentPos2D)
			enemy.player2D.set(access)
            enemy.pathIndex = 0
			recalcular = true
            //Log.e(tag, "Distintas plantas ----------------------------------------------- id=${enemy.id}  /  access=${enemy.player2D}  / access=$access")
		}
		else if(levelEnemy == levelPlayer && enemy.isAccessLevelPath){
			enemy.isAccessLevelPath = false
			enemy.player2D.set(Vector2(playerPosition.x, playerPosition.z))
            enemy.pathIndex = 0
            //Log.e(tag, "Restaurar misma planta ----------------------------------------------- id=${enemy.id}  /  access=${enemy.player2D} ")
		}
		//Misma planta: Buscar en mapa de obstaculos correspondiente
		val player2D = if(enemy.isAccessLevelPath) MazeFactory.mapFactory.map[levelEnemy].getNearerLevelAccess(enemy.currentPos2D) //enemy.player2D
						else Vector2(playerPosition.x, playerPosition.z)
		//if(enemy.isAccessLevelPath)Log.e(tag, "--------------------------- enemy.isAccessLevelPath !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! $player2D ")
		if(player2D.dst2(enemy.player2D) > WallFactory.LONG) {//TODO:otra medida max?
			//Log.e(tag, "--------------------------- RECALCULAR POR DISTANCIA  id=${enemy.id}    / $player2D ")
			recalcular = true
		}
		if(enemy.stepCounter++ > 30) {
			//Log.e(tag, "--------------------------- RECALCULAR LIMITE DE PASOS  id=${enemy.id}    / $player2D ")
			enemy.stepCounter = 0
			recalcular = true
		}

		//else if(player2D.dst2(enemy.player2D) != 0f) Log.e(tag, "---------------------------${player2D.dst2(enemy.player2D)}")
		if(enemy.pathIndex == 0 || enemy.pathIndex >= enemy.path!!.size) {
			//Si distancia < x ve a por ella -> rampa
			//TODO: cuando los enemigos pasan por debajo de rampa y player sube rampa, enemigo se bloquea debajo de rampa:
			//TODO   se cree que ya ha llegado a destino, deberia dar la vuelta y entrar por inicio de rampa!!!
			//Log.e(tag, "--------------------------- RECALCULAR POR FALTA DE PATH  id=${enemy.id}  / $player2D  /  path size=${enemy.path?.size}  ")
			recalcular = true
		}
		//
		if(recalcular) {
			enemy.player2D.set(player2D)
			recalcPath(enemy, levelEnemy)
		}
		else {
			usePath(enemy)
		}
	}

	private fun usePath(enemy: EnemyComponent) {
		val next = enemy.path!![enemy.pathIndex]
		//Log.e(tag, "usePath----------------------------------------------- id=${enemy.id}  path index=${enemy.pathIndex}  /  $next")

		if(enemy.currentPos2D.dst(next) < 5) {
			enemy.pathIndex++
		}
		if(enemy.pathIndex >= enemy.path!!.size) {
			enemy.pathIndex = 0
		}
		else {
			enemy.stepCalc2D = next
			enemy.nextStep3D = Vector3(enemy.stepCalc2D.x, enemy.position.y, enemy.stepCalc2D.y)
		}
	}
	private fun recalcPath(enemy: EnemyComponent, levelEnemy: Int) {
		val map = MazeFactory.mapFactory.map[levelEnemy]
		enemy.path = map.findPath(enemy.currentPos2D, enemy.player2D)
		//Log.e(tag, "recalcPath----------------------------------------------- id=${enemy.id}  path size=${enemy.path?.size}")
		enemy.path?.let { path ->
			if (path.size > 1) {
				enemy.pathIndex = 2
				enemy.stepCalc2D = path[1]
				enemy.nextStep3D = Vector3(enemy.stepCalc2D.x, enemy.position.y, enemy.stepCalc2D.y)
			} else
				enemy.nextStep3D = Vector3(enemy.player2D.x, enemy.position.y, enemy.player2D.y)
		}
	}


	//----------------------------------------------------------------------------------------------
	private enum class StatusMov { QUIET, ATTACK, RUN, WALK }

	private fun statusMov(entity: Entity, distPlayer: Float): StatusMov {
		val enemy = EnemyComponent.get(entity)
		val status = StatusComponent.get(entity)

		/// No está en condiciones de atacar: herido, muerto o sobre el suelo	//TODO: sobre suelo: Raycast para ver distancia a suelo.... EnemyComponent.RADIO + 2)
		return if(status.isAching() || status.isDead() || enemy.position.y > 2*WallFactory.HIGH + 2*RampFactory.THICK + enemy.radio) {
			StatusMov.QUIET
		}
		/// Esta al lado, atacale (Las colisiones no valen, porque aqui ignoro el estado)
		else if(distPlayer < attackRadio(enemy))
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
					gameEventSignal.dispatch(GameEvent(GameEvent.Type.ENEMY_DEAD, EnemyComponent.get(entity).reward, entity))
					engine.removeEntity(entity)
					//entity.remove(StatusComponent::class.java)
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
				status.estado = EnemyComponent.ACTION.IDLE//REINCARNATING
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