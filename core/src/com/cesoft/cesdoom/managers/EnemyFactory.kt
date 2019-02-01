package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.bullet.MotionState
import com.cesoft.cesdoom.components.*
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter
import com.badlogic.gdx.physics.bullet.collision.btSphereShape
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.components.EnemyComponent.ACTION.*
import com.cesoft.cesdoom.entities.Enemy
import com.cesoft.cesdoom.assets.ParticleEffectPool
import com.cesoft.cesdoom.assets.Sounds
import com.cesoft.cesdoom.systems.RenderSystem


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object EnemyFactory {
	private val tag: String = EnemyFactory::class.java.simpleName

	private var renderSystem: RenderSystem? = null
	private val activeEnemies = ArrayList<Enemy>()

	private lateinit var game: CesDoom
	fun init(game: CesDoom) {
		this.game = game
	}

	//______________________________________________________________________________________________
	private val posTemp = Vector3()
	fun create(
			id: Int,
			particlePool: ParticleEffectPool,
			render: RenderSystem,
			model: Model,
			type: EnemyComponent.TYPE,
			pos: Vector3,
			mase: Float = 100f) : Enemy {

		renderSystem = render
		val entity = Enemy(id)//enemyPool.obtain()


		/// Enemy Component
		val enemyComponent = EnemyComponent(type)
		entity.add(enemyComponent)

		/// Status Component
		val stat = StatusComponent(entity)
		stat.setWalking()
		entity.add(stat)

		/// Model
		val modelComponent: ModelComponent
		when(type) {
			EnemyComponent.TYPE.MONSTER1 -> {
				modelComponent = ModelComponent(model, pos)
				//modelComponent.frustumCullingData =
				//	FrustumCullingData.create(Vector3(0f,0f,0f), Vector3(RADIO,RADIO,RADIO), modelComponent.instance)
				entity.add(modelComponent)
				/// ANIMATION
				entity.add(AnimationComponent(modelComponent.instance))
				setAnimation(entity, EnemyComponent.ACTION.WALKING)
			}
		}

		// Evanesce Effect
		if(modelComponent.instance.materials.size > 0) {
			val material = modelComponent.instance.materials.get(0)
			val blendingAttribute = BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
			material.set(blendingAttribute)
			modelComponent.blendingAttribute = blendingAttribute
		}

		// Position and Shape
		val shape = btSphereShape(Enemy.RADIO-1)////btCylinderShape(Vector3(RADIO/2f,12f,14f))//btBoxShape(Vector3(diametro, diametro, diametro))//btCylinderShape(Vector3(14f,5f,14f))// btCapsuleShape(3f, 6f)
		shape.calculateLocalInertia(mase, posTemp)

		/// Collision
		val rigidBodyInfo = btRigidBody.btRigidBodyConstructionInfo(mase, null, shape, posTemp)
		val rigidBody = btRigidBody(rigidBodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = MotionState(modelComponent.instance.transform)
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT//CF_CUSTOM_MATERIAL_CALLBACK
		rigidBody.contactCallbackFilter = 0//BulletComponent.PLAYER_FLAG //BulletComponent.SHOT_FLAG or
		rigidBody.contactCallbackFlag = BulletComponent.ENEMY_FLAG
		rigidBody.userValue = BulletComponent.ENEMY_FLAG
		rigidBody.friction = 0f
		rigidBody.rollingFriction = 1000000f
		entity.add(BulletComponent(rigidBody, rigidBodyInfo))

		/// STEERING
		//entity.add(SteeringComponent(rigidBody, RADIO))
		//https://www.gamedevelopment.blog/full-libgdx-game-tutorial-ashley-steering-behaviors/

		entity.init(type, mase, particlePool, rigidBody, rigidBodyInfo)
		activeEnemies.add(entity)
		return entity
	}

	//______________________________________________________________________________________________
	private var currentAnimat = EnemyComponent.ACTION.IDLE
	private fun setAnimation(entity: Entity, action: EnemyComponent.ACTION) {
		currentAnimat = action
		val type = entity.getComponent(EnemyComponent::class.java).type
		val animParams = getAnimationParams(type, action)
		if(animParams.id.isEmpty())return
		entity.getComponent(AnimationComponent::class.java).animate(animParams)
	}
	//______________________________________________________________________________________________
	private val random = java.util.Random()
	private const val ACTION_NAME = "MilkShape3D Skele|DefaultAction"
	private fun getAnimationParams(type: EnemyComponent.TYPE, action: EnemyComponent.ACTION) : AnimationParams {
		val loop = -1
		val speed = 1f
		val time = getActionDuration(type, action)
		when(type) {
			EnemyComponent.TYPE.MONSTER1 ->
				return when(action) {//TODO: Hacer estos objetos staticos!!! asi no tienes que crearlos....
					EnemyComponent.ACTION.WALKING ->
						AnimationParams(ACTION_NAME, loop, speed, 0f, time)
					EnemyComponent.ACTION.RUNNING ->
						AnimationParams(ACTION_NAME, loop, speed, 6f, time)
					EnemyComponent.ACTION.ATTACKING -> {
						AnimationParams(ACTION_NAME, loop, speed, 12.8f, time)
//						when(random.nextInt(3)) {
//							0 -> AnimationParams(ACTION_NAME, loop, speed, 12.8f, time)
//							else -> AnimationParams(ACTION_NAME, loop, speed, 10f, time)
//						}
					}
					EnemyComponent.ACTION.IDLE ->
						AnimationParams(ACTION_NAME, loop, speed, 19.12f, time)
					EnemyComponent.ACTION.REINCARNATING ->
						AnimationParams(ACTION_NAME, loop, speed, 0f, time)
					EnemyComponent.ACTION.ACHING -> {
						if(random.nextInt(2) == 0)
							AnimationParams(ACTION_NAME, loop, speed, 15.6f, time)
						else
							AnimationParams(ACTION_NAME, loop, speed, 20f, time)
					}
					EnemyComponent.ACTION.DYING -> {
						AnimationParams(ACTION_NAME, loop, speed, 22.6f, time)
						/*if(random.nextInt(2) == 0) {
							Log.e("----******************--------- Enemy Factory DYING 1")
							AnimationParams(ACTION_NAME, loop, speed, 15.6f, time)
						}
						else {
							Log.e("-----*********************-------- Enemy Factory DYING 2")
							AnimationParams(ACTION_NAME, loop, speed, 20f, time)
						}*/
					}
				}
		}
	}
	//______________________________________________________________________________________________
	private object ActDuration {
		/*
				0-30  walk				0-1.2
				0-120 walk				0-4.8
				150-190 run				6-7.6
				150-210 run				6-8.4
				250-333 attack-01		10-13.32
				320-400 attack-02		12.8-16
				390-418 death-01		15.6-16.72
				478-500 growl			19.12-20
				500-550 death-02		20-22
				565-650 death-03		22.6-26
				//
				650 --> 26s   ==> 25 fps */
		val actionDuration = mapOf(
				WALKING to 4.8f,
				RUNNING to 2.4f,
				ATTACKING to 3.15f,
				IDLE to 0.88f,
				REINCARNATING to 26f,//TODO
				ACHING to 2.5f,
				DYING to 3.4f
		)
	}
	private val typeActionDuration = mapOf(EnemyComponent.TYPE.MONSTER1 to ActDuration)
	fun getActionDuration(type: EnemyComponent.TYPE, action: EnemyComponent.ACTION) =
		typeActionDuration[type]!!.actionDuration[action]!!


	//______________________________________________________________________________________________
	fun playRunning(entity: Entity) {
		setAnimation(entity, RUNNING)
	}
	//______________________________________________________________________________________________
	fun playWalking(entity: Entity) {
		setAnimation(entity, WALKING)
	}
	//______________________________________________________________________________________________
	fun playAttack(entity: Entity) {
		setAnimation(entity, ATTACKING)
	}
	//______________________________________________________________________________________________
	fun playAching(entity: Entity) {
		setAnimation(entity, ACHING)
	}
	//______________________________________________________________________________________________
	fun playDying(entity: Entity) {
		//si no termino la animacion ACHIN, no poner esta....
		if(currentAnimat != ACHING)
			setAnimation(entity, EnemyComponent.ACTION.DYING)
		else
			setAnimation(entity, EnemyComponent.ACTION.ACHING)

		val model = entity.getComponent(ModelComponent::class.java)
		val enemy = entity as Enemy
		val effect = enemy.particleEffect!!
		val re = effect.controllers.first().emitter as RegularEmitter

		re.emissionMode = RegularEmitter.EmissionMode.EnabledUntilCycleEnd
		effect.setTransform(model.instance.transform)
		effect.scale(5f, 8f, 5f)
		effect.init()
		effect.start()
		renderSystem?.addParticleEffect(effect)
	}

	fun update(delta: Float, enemy: Enemy, posPlayer: Vector3) {
		val status = enemy.getComponent(StatusComponent::class.java)
		if(status.isDead())
		{
			Sounds.play(Sounds.SoundType.ENEMY_DIE)

			val model = enemy.getComponent(ModelComponent::class.java)
			if(model.blendingAttribute != null)
				model.blendingAttribute!!.opacity = 1 - status.deathProgres()

			if(status.deathProgres() == 1f) {
				activeEnemies.remove(enemy)
				return
			}
		}

		///
		val animat = enemy.getComponent(AnimationComponent::class.java)
		animat?.update(delta)
		///
		enemy.update(posPlayer, delta)
	}
}