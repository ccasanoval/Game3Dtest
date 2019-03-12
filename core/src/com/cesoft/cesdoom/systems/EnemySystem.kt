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
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.assets.Sounds
import com.cesoft.cesdoom.components.*
import com.cesoft.cesdoom.entities.Player
import com.cesoft.cesdoom.events.*
import com.cesoft.cesdoom.managers.*
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
				when(EnemyComponent.get(entity).type) {
					EnemyComponent.TYPE.MONSTER0 -> Sounds.play(Sounds.SoundType.ENEMY_DIE)
					EnemyComponent.TYPE.MONSTER1 -> Sounds.play(Sounds.SoundType.ENEMY1_DIE)
				}

			val model = ModelComponent.get(entity)
			if(model.blendingAttribute != null)
				model.blendingAttribute!!.opacity = 1 - deathProgress(entity)
		}
	}
	//______________________________________________________________________________________________
	private var lastAttack = 0L
	private fun updateMovement(entity: Entity, delta: Float) {

		val playerPosition = player!!.getPosition().cpy()

		val enemy = EnemyComponent.get(entity)
		val model = ModelComponent.get(entity)

		model.instance.transform.getTranslation(enemy.position)
		enemy.currentPos2D = Vector2(enemy.position.x, enemy.position.z)
		val distPlayer = enemy.position.dst(playerPosition)

		calcStatusMov(entity, distPlayer)
		val force: Float
		when(enemy.statusMov) {
			EnemyComponent.StatusMov.QUIET, EnemyComponent.StatusMov.FALL -> {
				force = 0f
			}
			EnemyComponent.StatusMov.ATTACK -> {
				setAttacking(entity)
				val now = System.currentTimeMillis()
				if(now > lastAttack + EnemyComponent.DELAY_ATTACK) {
					lastAttack = now
					gameEventSignal.dispatch(GameEvent(GameEvent.Type.PLAYER_HURT, EnemyComponent.BITE_PAIN))
					when(enemy.type) {
						EnemyComponent.TYPE.MONSTER0 -> Sounds.play(Sounds.SoundType.ENEMY_ATTACK)
						EnemyComponent.TYPE.MONSTER1 -> Sounds.play(Sounds.SoundType.ENEMY1)
					}
				}
				force = 0f
			}
			EnemyComponent.StatusMov.RUN -> {
				force = runForce(enemy)
				setRunning(entity)
			}
			EnemyComponent.StatusMov.WALK -> {
				force = walkForce(enemy)
				setWalking(entity)
			}
		}

		/// Si hay movimiento, calcula el camino
		//if( ! status.isAttacking() && force != 0f)
		if (enemy.statusMov == EnemyComponent.StatusMov.WALK || enemy.statusMov == EnemyComponent.StatusMov.RUN) {
			calcPath(entity, playerPosition)
		}

		/// Mueve
		val isMoving = distPlayer > 2*attackRadio(enemy)
				&& (enemy.statusMov == EnemyComponent.StatusMov.WALK || enemy.statusMov == EnemyComponent.StatusMov.RUN)
		val isQuiet = enemy.statusMov == EnemyComponent.StatusMov.QUIET
		moveEnemy(entity, playerPosition, isMoving, isQuiet, force, delta)
	}
	private fun walkForce(enemy: EnemyComponent): Float {
		val force = 900f//if(CesDoom.isMobile) 800f else 900f
		return force + (PlayerComponent.currentLevel * 50)
	}
	private fun runForce(enemy: EnemyComponent): Float {
		val force = if(enemy.type == EnemyComponent.TYPE.MONSTER0) 1600f else 1100f
		return force + (PlayerComponent.currentLevel * 75)
	}
	//______________________________________________________________________________________________
	private fun moveEnemy(entity: Entity, playerPosition: Vector3, isWalking: Boolean, isQuiet: Boolean, force: Float, delta: Float) {

		val enemy = EnemyComponent.get(entity)
		val rigidBody = BulletComponent.get(entity).rigidBody

		enemy.positionOld.set(enemy.position)
		val model = ModelComponent.get(entity)
		model.instance.transform.getTranslation(enemy.position)
		//Log.e(tag, "moveEnemy:------------------------------------- pos1=${enemy.position} / ${enemy.positionOld} ---------- yVel=${rigidBody.linearVelocity.y}")

		/// Set velocity
		val dir = enemy.nextStep3D.add(enemy.position.scl(-1f)).nor().scl(force * delta)
		dir.y = rigidBody.linearVelocity.y
		rigidBody.linearVelocity = dir

		/// Calc orientation
		val transf = Matrix4()
		rigidBody.getWorldTransform(transf)
		transf.getTranslation(enemy.position)
		//Log.e(tag, "moveEnemy:------------------------------------- pos2=${enemy.position} / ${enemy.positionOld} ---------- yVel=${rigidBody.linearVelocity.y}")

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

	private fun getTarget(floorPlayer: Int, playerPosition: Vector3, floorEnemy: Int, enemyPosition: Vector2): Vector2 {
		if(floorEnemy < floorPlayer) {
			val target = MazeFactory.getNearerFloorAccess(floorEnemy, enemyPosition)
//Log.e(tag, "getTarget(floorEnemy < floorPlayer): ----- target=$target")
			return target//TODO: mejorar para buscar accesos desde niveles superiores hacia inferiores...Añadir al mapa
		}
		else {
			return Vector2(playerPosition.x, playerPosition.z)
		}
	}

	private fun getPlayerFloor(playerPositionY: Float): Int {
		return if(playerPositionY > 2*WallFactory.HIGH) 1 else 0
	}
	private fun getEnemyFloor(enemy: EnemyComponent): Int {
		//val enemy = EnemyComponent.get(entity)
		return if(enemy.position.y > 2*WallFactory.HIGH + enemy.radio) 1 else 0
	}

	//----------------------------------------------------------------------------------------------
	private fun calcPath(entity: Entity, playerPosition: Vector3) {
		val enemy = EnemyComponent.get(entity)
		val floorEnemy = getEnemyFloor(enemy)
		val floorPlayer = getPlayerFloor(playerPosition.y)
//com.cesoft.cesdoom.util.Log.e(tag, "id=${enemy.id} : LEVELS ----------------(isAccessFloorPath=${enemy.isAccessFloorPath})-------------------- $floorEnemy  <>  $floorPlayer  / player2D=${enemy.player2D} ")

		//Distintas plantas: Buscar en mapa de accessos
		if(floorEnemy != floorPlayer && !enemy.isAccessFloorPath) {
			enemy.isAccessFloorPath = true
			enemy.player2D.set(getTarget(floorPlayer, playerPosition, floorEnemy, enemy.currentPos2D))
//Log.e(tag, "${enemy.id} : Distintas plantas -----------------floor: player=$floorPlayer / enemy=$floorEnemy------------- target=${enemy.player2D} ")
			recalcPath(enemy, playerPosition)
		}
		//Misma planta: Buscar a jundador en mapa
		else if(floorEnemy == floorPlayer && enemy.isAccessFloorPath) {
			enemy.isAccessFloorPath = false
			enemy.player2D.set(getTarget(floorPlayer, playerPosition, floorEnemy, enemy.currentPos2D))
//Log.e(tag, "${enemy.id}----- Restaurar misma planta -------------------------------- target=${enemy.player2D} ")
			recalcPath(enemy, playerPosition)
		}
		else if(enemy.stepCounter++ > 100) {
			enemy.player2D.set(getTarget(floorPlayer, playerPosition, floorEnemy, enemy.currentPos2D))
//Log.e(tag, "${enemy.id}--------------------------- RECALCULAR LIMITE DE PASOS : target=${enemy.player2D}")
			recalcPath(enemy, playerPosition)
		}
		else if(enemy.pathIndex == 0 || enemy.pathIndex >= enemy.path!!.size) {
			enemy.player2D.set(getTarget(floorPlayer, playerPosition, floorEnemy, enemy.currentPos2D))
//Log.e(tag, "${enemy.id}--------------------------- RECALCULAR POR FALTA DE PATH : target=${enemy.player2D}  /  enemy.pathIndex=${enemy.pathIndex} path size=${enemy.path?.size}  ")
			recalcPath(enemy, playerPosition)
		}
		else
			usePath(enemy)
	}

	private fun usePath(enemy: EnemyComponent) {
		val next = enemy.path!![enemy.pathIndex]
//Log.e(tag, "${enemy.id} : usePath--------------------pos=${enemy.currentPos2D}---------------------------  path index=${enemy.pathIndex}  /  $next")

		if(enemy.currentPos2D.dst(next) < MazeFactory.scale) {
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

	private fun recalcPath(enemy: EnemyComponent, playerPosition: Vector3) {
		val floorEnemy = getEnemyFloor(enemy)//if(enemy.position.y > 2*WallFactory.HIGH) 1 else 0
		val floorPlayer = getPlayerFloor(playerPosition.y)//if(playerPosition.y > 2*WallFactory.HIGH) 1 else 0
		val accessPoint = getTarget(floorPlayer, playerPosition, floorEnemy, enemy.currentPos2D)
		enemy.player2D.set(accessPoint)
		enemy.stepCounter = 0

		enemy.path = MazeFactory.findPath(floorEnemy, enemy.currentPos2D, enemy.player2D)//, !enemy.isAccessFloorPath)
//Log.e(tag, "${enemy.id} : recalcPath---------pos=${enemy.currentPos2D}/floor=$floorEnemy-------target=${enemy.player2D}------------- path size=${enemy.path?.size}")
		enemy.path?.let { path ->
			if(path.size > 1) {
				for(step in path) {
//Log.e(tag, " recalcPath---------step=$step")
				}

				enemy.pathIndex = 1
				enemy.stepCalc2D = path[1]
//Log.e(tag, "${enemy.id} : recalcPath----------------------------------------------- step=${enemy.stepCalc2D}")
				enemy.nextStep3D = Vector3(enemy.stepCalc2D.x, enemy.position.y, enemy.stepCalc2D.y)
			}
			else
				enemy.nextStep3D = Vector3(enemy.player2D.x, enemy.position.y, enemy.player2D.y)
		}
	}


	//----------------------------------------------------------------------------------------------
	private fun calcStatusMov(entity: Entity, distPlayer: Float) {//: EnemyComponent.StatusMov {
		val enemy = EnemyComponent.get(entity)
		val status = StatusComponent.get(entity)
		val yVel = BulletComponent.get(entity).rigidBody.linearVelocity.y

		/// No está en condiciones de moverse: herido, muerto
		if(status.isAching() || status.isDead() ){//|| enemy.position.y > 2*WallFactory.HIGH + 2*RampFactory.THICK + enemy.radio) {
			enemy.statusMov = EnemyComponent.StatusMov.QUIET
			//EnemyComponent.StatusMov.QUIET
		}
		/// Esta al lado, atacale (Las colisiones no valen, porque aqui ignoro el estado)
		else if(distPlayer < attackRadio(enemy)) {
			enemy.statusMov = EnemyComponent.StatusMov.ATTACK
			///EnemyComponent.StatusMov.ATTACK
		}
		/// Esta cayendo //TODO: Raycast para ver distancia a suelo.... EnemyComponent.RADIO + 2)
		else if(enemy.position.y > 6*WallFactory.HIGH || yVel < -10) {//enemy.positionOld.y - enemy.position.y > 1) {//TODO: or BulletComponent.get(entity).rigidBody.linearVelocity.y > x
			enemy.statusMov = EnemyComponent.StatusMov.FALL
		}
		/// Esta cerca, corre a por el
		else if(distPlayer < 180f) {
			if(enemy.statusMov != EnemyComponent.StatusMov.RUN) {
				enemy.statusMov = EnemyComponent.StatusMov.RUN
				when(EnemyComponent.get(entity).type) {
					EnemyComponent.TYPE.MONSTER0 -> Sounds.play(Sounds.SoundType.ENEMY_NEAR)
					EnemyComponent.TYPE.MONSTER1 -> Sounds.play(Sounds.SoundType.ENEMY1)
				}
			}
			//EnemyComponent.StatusMov.RUN
		}
		/// Esta lejos, camina buscando
		else {
			enemy.statusMov = EnemyComponent.StatusMov.WALK
			//EnemyComponent.StatusMov.WALK	//TODO: una vez que empieza a correr, que no pare?
		}
		//Log.e(tag, "enemy.statusMov-------------------------------------- ${enemy.statusMov}")
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
			when(EnemyComponent.get(entity).type) {
				EnemyComponent.TYPE.MONSTER0 -> Sounds.play(Sounds.SoundType.ENEMY_HURT)
				EnemyComponent.TYPE.MONSTER1 -> Sounds.play(Sounds.SoundType.ENEMY1)
			}
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