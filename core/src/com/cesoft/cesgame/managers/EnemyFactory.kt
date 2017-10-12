package com.cesoft.cesgame.managers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.collision.btSphereShape
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.utils.UBJsonReader
import com.cesoft.cesgame.bullet.MotionState
import com.cesoft.cesgame.components.*
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object EnemyFactory
{
	//private val modelLoaderJSON = G3dModelLoader(JsonReader())
	private val modelLoader = G3dModelLoader(UBJsonReader())
	private var models = mutableMapOf<EnemyComponent.TYPE, Model>()

	private val modelDataMonster1 = modelLoader.loadModelData(Gdx.files.internal("foes/monster1/a.g3db"))


	//______________________________________________________________________________________________
	fun dispose()
	{
		for((_, model) in models)
			model.dispose()
		models = mutableMapOf()
	}

	//______________________________________________________________________________________________
	private fun createModel(type: EnemyComponent.TYPE): Model {
		val model: Model
		when(type) {
			EnemyComponent.TYPE.MONSTER1 -> {
				model = Model(modelDataMonster1)
				//model = modelLoaderJSON.loadModel(Gdx.files.getFileHandle("foes/monster1/monster.g3dj", Files.FileType.Internal))
//				for(i in 0 until model.nodes.size - 1)
//					model.nodes[i].scale.scl(0.01f)
			}
		}
		return model
	}

	//______________________________________________________________________________________________
	fun create(type: EnemyComponent.TYPE, pos: Vector3, mase: Float = 100f) : Entity
	{
		val entity = Entity()

		/// ENEMY
		val enemy = EnemyComponent(type)
		entity.add(enemy)

		/// MODEL
		if(models[type] == null)
			models[type] = createModel(type)
		//
		val modelComponent: ModelComponent
		when(type) {
			EnemyComponent.TYPE.MONSTER1 -> {
				modelComponent = ModelComponent(models[type]!!, pos)
				entity.add(modelComponent)

				val anim = AnimationComponent(modelComponent.instance)
				entity.add(anim)
				setAnimation(entity, EnemyComponent.ACTION.IDLE)
				//TODO: Peta al reiniciar
//				val am = AssetManager()
//				entity.add(EnemyDieParticleComponent(RenderSystem.particleSystem, am))
//				am.dispose()
			}
		}
		// (desaparecer)
		System.err.println("MAT SIZE-----------------------------------------------"+modelComponent.instance.materials.size)
		if(modelComponent.instance.materials.size > 0) {
			val material = modelComponent.instance.materials.get(0)
			val blendingAttribute = BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
			material.set(blendingAttribute)
			modelComponent.blendingAttribute = blendingAttribute
		}

		/// STATUS
		entity.add(StatusComponent(entity))

		/// COLLISION
		val localInertia = Vector3()
		val shape = btSphereShape(20f)//btCylinderShape(Vector3(4f,4f,4f))//btBoxShape(Vector3(3f,3f,3f))// btCapsuleShape(3f, 6f)
		shape.calculateLocalInertia(mase, localInertia)
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(mase, null, shape, localInertia)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = MotionState(modelComponent.instance.transform)
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK
		rigidBody.contactCallbackFilter = BulletComponent.SHOT_FLAG
		rigidBody.contactCallbackFlag = BulletComponent.ENEMY_FLAG
		rigidBody.userValue = BulletComponent.ENEMY_FLAG
		//rigidBody.userIndex = index
		rigidBody.friction = 3f
		rigidBody.rollingFriction = 3f
		entity.add(BulletComponent(rigidBody, bodyInfo))

		return entity
	}

	//______________________________________________________________________________________________
	fun setAnimation(entity: Entity, action: EnemyComponent.ACTION)
	{
		val type = entity.getComponent(EnemyComponent::class.java).type
		val animParams = getAnimationParams(type, action)
		if(animParams.id.isEmpty())return
		val anim = entity.getComponent(AnimationComponent::class.java)
		anim.animate(animParams.id, animParams.loop, animParams.speed, animParams.offset, animParams.duration)
	}
	//______________________________________________________________________________________________
	private val random = java.util.Random()
	private class AnimationParams(var id: String, var loop: Int = -1, var speed: Float = 1f, var duration: Float = 0f, var offset: Float = -1f)
	private fun getAnimationParams(type: EnemyComponent.TYPE, action: EnemyComponent.ACTION) : AnimationParams
	{
		val loop = -1
		val speed = 1f
		when(type) {
			EnemyComponent.TYPE.MONSTER1 ->
				return when(action) {//TODO: Acer estos objetos staticos!!! asi no tienes que crearlos....
					EnemyComponent.ACTION.WALKING -> //TODO: Random
						AnimationParams("MilkShape3D Skele|DefaultAction", loop, speed, 4.8f, 0f)
					EnemyComponent.ACTION.RUNNING ->
						AnimationParams("MilkShape3D Skele|DefaultAction", loop, speed, 2.4f, 6f)
					EnemyComponent.ACTION.ATTACKING -> {
						when(random.nextInt(1))
						{
							0 -> AnimationParams("MilkShape3D Skele|DefaultAction", loop, speed, 3.32f, 10f)
							else -> AnimationParams("MilkShape3D Skele|DefaultAction", loop, speed, 3.2f, 12.8f)
						}
					}
					EnemyComponent.ACTION.IDLE ->
						AnimationParams("MilkShape3D Skele|DefaultAction", loop, speed, 0.88f, 19.12f)
					EnemyComponent.ACTION.REINCARNATING ->
						AnimationParams("MilkShape3D Skele|DefaultAction", loop, speed, 26f, 0f)
					EnemyComponent.ACTION.DYING -> {
						when(random.nextInt(2))
						{
							0 -> AnimationParams("MilkShape3D Skele|DefaultAction", loop, speed, 1.12f, 15.6f)
							1 -> AnimationParams("MilkShape3D Skele|DefaultAction", loop, speed, 2f, 20f)
							else -> AnimationParams("MilkShape3D Skele|DefaultAction", loop, speed, 3.4f, 22.6f)
						}
					}
				}
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
		}
	}

	//______________________________________________________________________________________________
	fun mover(entity: Entity, playerPosition: Vector3, delta: Float)
	{
		val status = entity.getComponent(StatusComponent::class.java)
		if( ! status.isAlive)return

		val enemyPosition = Vector3()
		val model = entity.getComponent(ModelComponent::class.java)
		model.instance.transform.getTranslation(enemyPosition)
		val dX = playerPosition.x - enemyPosition.x
		val dZ = playerPosition.z - enemyPosition.z
		val dire = playerPosition.add(enemyPosition.scl(-1f)).nor()

		val fuerza: Float
		/// Esta lejos, camina buscando
		if( !status.walking && enemyPosition.dst(playerPosition) > 100f)
		{
			//TODO: Wandering ?
			//TODO: Movil?
			//TODO: dX,dZ tienes que normalizarlas
			fuerza = 500f
			status.walking = true
		}
		/// Esta cerca, corre a por el
		else if( !status.running && enemyPosition.dst(playerPosition) < 100f)
		{
			fuerza = 1900f
			status.running = true
		}
		else fuerza = 0f

		// Fuerzas // TODO: cambiar por velocidad lineal?
		val bullet = entity.getComponent(BulletComponent::class.java)
		val dir = dire.scl(fuerza*delta)
		dir.y = bullet.rigidBody.linearVelocity.y
		bullet.rigidBody.linearVelocity = dir
		//bullet.rigidBody.applyCentralForce(Vector3(dX, 0f, dZ).nor().scl(fuerza))

		// Orientacion
		val theta = Math.atan2(dX.toDouble(), dZ.toDouble()).toFloat()
		val quat = Quaternion()

		val enemy = entity.getComponent(EnemyComponent::class.java)
		val angulo0 :Float
		angulo0 = when(enemy.type) {
			EnemyComponent.TYPE.MONSTER1 -> 90f
		}
		val rot = quat.setFromAxis(0f, 1f, 0f, Math.toDegrees(theta.toDouble()).toFloat() + angulo0)
		val transf = Matrix4()
		bullet.rigidBody.getWorldTransform(transf)
		transf.getTranslation(enemyPosition)
		val altura = when(enemy.type) {
			EnemyComponent.TYPE.MONSTER1 -> 0f
			//else -> 0f
		}
		model.instance.transform.set(enemyPosition.x, enemyPosition.y+altura, enemyPosition.z, rot.x, rot.y, rot.z, rot.w)
	}

}