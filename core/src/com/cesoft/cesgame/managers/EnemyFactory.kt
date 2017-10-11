package com.cesoft.cesgame.managers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.collision.btSphereShape
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.UBJsonReader
import com.cesoft.cesgame.bullet.MotionState
import com.cesoft.cesgame.components.*
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute



////////////////////////////////////////////////////////////////////////////////////////////////////
//
object EnemyFactory
{
	private val modelLoaderJSON = G3dModelLoader(JsonReader())
	private val modelLoader = G3dModelLoader(UBJsonReader())
	private var models = mutableMapOf<EnemyComponent.TYPE, Model>()
	private var files = mutableMapOf<EnemyComponent.TYPE, FileHandle>()

	init {
		files[EnemyComponent.TYPE.ZOMBIE1] = Gdx.files.getFileHandle("foes/zombie1/zombie_normal.g3db", Files.FileType.Internal)
		files[EnemyComponent.TYPE.MONSTER1] = Gdx.files.getFileHandle("foes/monster1/monster.g3dj", Files.FileType.Internal)
	}

	//______________________________________________________________________________________________
	fun dispose()
	{
		for((_, model) in models)
			model.dispose()
		models = mutableMapOf()
	}

	//______________________________________________________________________________________________
	fun createModel(type: EnemyComponent.TYPE): Model {
		val model: Model
		when(type) {
			EnemyComponent.TYPE.ZOMBIE1 -> {
				model = modelLoader.loadModel(files[type])
				for(i in 0 until model.nodes.size - 1)
					model.nodes[i].scale.scl(10f)
			}
			EnemyComponent.TYPE.MONSTER1 -> {
				model = modelLoaderJSON.loadModel(files[type])
				for(i in 0 until model.nodes.size - 1)
					model.nodes[i].scale.scl(0.01f)
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

		val modelComponent: ModelComponent
		when(type) {
			EnemyComponent.TYPE.ZOMBIE1 -> {
				modelComponent = ModelComponent(models[type]!!, pos)
				entity.add(modelComponent)

				val anim = AnimationComponent(modelComponent.instance)
				entity.add(anim)
				entity.add(StatusComponent(entity))
				animate(entity, EnemyComponent.ACTION.WALKING)
			}
			EnemyComponent.TYPE.MONSTER1 -> {
				modelComponent = ModelComponent(models[type]!!, pos)
				entity.add(modelComponent)

				val anim = AnimationComponent(modelComponent.instance)
				entity.add(anim)
				entity.add(StatusComponent(entity))
				animate(entity, EnemyComponent.ACTION.WALKING)

				//anim.animate(EnemyAnimations.id, EnemyAnimations.offsetRun1, EnemyAnimations.durationRun1, -1, 1f)

				//val animParams = getAnimationParams(type, EnemyComponent.ACTION.WALKING)
				//anim.animate(animParams.id, animParams.offset, animParams.duration, animParams.loop, animParams.speed)

				//TODO: Peta al reiniciar
//				val am = AssetManager()
//				entity.add(EnemyDieParticleComponent(RenderSystem.particleSystem, am))
//				am.dispose()

				val material = modelComponent.instance.materials.get(0)
				val blendingAttribute = BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
				material.set(blendingAttribute)
				modelComponent.blendingAttribute = blendingAttribute
			}
		}

		/// COLLISION
		val localInertia = Vector3()
		val shape = btSphereShape(10f)//btCylinderShape(Vector3(4f,4f,4f))//btBoxShape(Vector3(3f,3f,3f))// btCapsuleShape(3f, 6f)
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
	fun animate(entity: Entity, action: EnemyComponent.ACTION)
	{
		val type = entity.getComponent(EnemyComponent::class.java).type
		val animParams = getAnimationParams(type, action)
		val anim = entity.getComponent(AnimationComponent::class.java)
		anim.animate(animParams.id, animParams.offset, animParams.duration, animParams.loop, animParams.speed)
	}
	//______________________________________________________________________________________________
	class AnimationParams(var id: String, var loop: Int = 1, var speed: Float = 3f, var duration: Float = 0f, var offset: Float = -1f)
	private fun getAnimationParams(type: EnemyComponent.TYPE, action: EnemyComponent.ACTION) : AnimationParams
	{
		val loop = -1
		val speed = 1f
		when(type) {
			EnemyComponent.TYPE.ZOMBIE1 ->
				when(action) {
					EnemyComponent.ACTION.IDLE -> return AnimationParams("Idle")
					EnemyComponent.ACTION.DYING -> return AnimationParams("Dying")
					EnemyComponent.ACTION.ATTACKING -> return AnimationParams("Attacking")
					EnemyComponent.ACTION.WALKING -> return AnimationParams("Walking")
					EnemyComponent.ACTION.REINCARNATING -> return AnimationParams("Reincarnating")
				}
			EnemyComponent.TYPE.MONSTER1 ->
				when(action) {
					EnemyComponent.ACTION.IDLE -> return AnimationParams("MilkShape3D Skele|DefaultAction")
					EnemyComponent.ACTION.DYING -> return AnimationParams("MilkShape3D Skele|DefaultAction", loop, speed, 3.4f, 22.6f)
					EnemyComponent.ACTION.ATTACKING -> return AnimationParams("MilkShape3D Skele|DefaultAction", loop, speed, 10f, 3.32f)
					EnemyComponent.ACTION.WALKING -> return AnimationParams("MilkShape3D Skele|DefaultAction", loop, speed, 6f, 1.6f)
					EnemyComponent.ACTION.REINCARNATING -> return AnimationParams("MilkShape3D Skele|DefaultAction")
					//"MilkShape3D Skele|DefaultAction"
					//"MilkShape3D Skele|DefaultAction.001",
					//"MilkShape3D Skeleton|DefaultAction",
					//"MilkShape3D Skeleton|DefaultAction.001",
				}
		}
	}
}