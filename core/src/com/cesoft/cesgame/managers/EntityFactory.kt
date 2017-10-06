package com.cesoft.cesgame.managers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.UBJsonReader
import com.cesoft.cesgame.bullet.MotionState
import com.cesoft.cesgame.components.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.cesoft.cesgame.systems.RenderSystem


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object EntityFactory {

	//______________________________________________________________________________________________
	fun createPlayer(x: Float, y: Float, z: Float): Entity {
		val entity = Entity()

		val localInertia = Vector3()
		val shape = btSphereShape(5f)////btCylinderShape(Vector3(3f,1f,3f))//
		shape.calculateLocalInertia(PlayerComponent.MASA, localInertia)
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(PlayerComponent.MASA, null, shape, localInertia)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = MotionState(Matrix4().setToTranslation(x,y,z))
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK
		rigidBody.contactCallbackFilter = 0//BulletComponent.PLAYER_FLAG//BulletComponent.ENEMY_FLAG or BulletComponent.ARENA_FLAG
		rigidBody.contactCallbackFlag = BulletComponent.PLAYER_FLAG
		rigidBody.userValue = BulletComponent.PLAYER_FLAG
		//rigidBody.userIndex = BulletComponent.PLAYER_FLAG
		rigidBody.friction = 3f
		rigidBody.rollingFriction = 3f

		entity.add(BulletComponent(rigidBody, bodyInfo))
		entity.add(PlayerComponent())

		return entity
	}

	//______________________________________________________________________________________________
	private val assetManager = AssetManager()
	fun createEnemy(enemyModel: Model, x: Float, y: Float, z: Float): Entity {
		val entity = Entity()

		val enemyModelComponent = ModelComponent(enemyModel, x, y, z)
		//enemyModelComponent!!.instance.transform.set(enemyModelComponent!!.matrix4.setTranslation(x, y, z))
		entity.add(enemyModelComponent)
		entity.add(EnemyComponent(EnemyComponent.STATE.HUNTING))

		val animationComponent = AnimationComponent(enemyModelComponent.instance)
		animationComponent.animate(EnemyAnimations.id, EnemyAnimations.offsetRun1, EnemyAnimations.durationRun1, -1, 1)    //TODO variable animationspeed
		entity.add(animationComponent)

		entity.add(StatusComponent(animationComponent))
		//entity.add(EnemyDieParticleComponent(RenderSystem.particleSystem, assetManager))

		val localInertia = Vector3()
		val shape = btSphereShape(5f)//btCylinderShape(Vector3(4f,4f,4f))//btBoxShape(Vector3(3f,3f,3f))// btCapsuleShape(3f, 6f)
		shape.calculateLocalInertia(EnemyComponent.MASA, localInertia)
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(EnemyComponent.MASA, null, shape, localInertia)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = MotionState(enemyModelComponent.instance.transform)
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK
		rigidBody.contactCallbackFilter = 0//BulletComponent.ENEMY_FLAG
		rigidBody.contactCallbackFlag = BulletComponent.ENEMY_FLAG
		rigidBody.userValue = BulletComponent.ENEMY_FLAG
		//rigidBody.userIndex = BulletComponent.ENEMY_FLAG
		rigidBody.friction = 3f
		rigidBody.rollingFriction = 3f
		entity.add(BulletComponent(rigidBody, bodyInfo))

		return entity
	}
	//______________________________________________________________________________________________
	//TODO: Change with laser
	val mb = ModelBuilder()
	val material = Material(ColorAttribute.createDiffuse(Color.GREEN))
	val model : Model = mb.createBox(.5f, .5f, .5f, material, (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())

	fun createShot(pos: Vector3, dir: Vector3, mass: Float = ShotComponent.MASA, force: Float = ShotComponent.FUERZA): Entity {
		val entity = Entity()

		//System.err.println("--------------- SHOT CREATE : "+pos+" ::: "+dir)

		// MODEL
		val modelComponent = ModelComponent(model, pos.x, pos.y, pos.z)
		entity.add(modelComponent)

		// BULLET
		val localInertia = Vector3()
		val shape = btBoxShape(Vector3(.5f,.5f,.5f))
		shape.calculateLocalInertia(mass, localInertia)
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = MotionState(modelComponent.instance.transform)
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK
		//rigidBody.contactCallbackFilter = BulletComponent.ENEMY_FLAG
		//rigidBody.contactCallbackFlag = BulletComponent.SHOT_FLAG
		rigidBody.userValue = BulletComponent.SHOT_FLAG
		//rigidBody.userIndex = BulletComponent.SHOT_FLAG
		rigidBody.applyCentralForce(dir.scl(force))
		entity.add(BulletComponent(rigidBody, bodyInfo))

		return entity
	}

	//______________________________________________________________________________________________
	fun loadScene(x: Float, y: Float, z: Float): Entity {
		val entity = Entity()
		val modelLoader = G3dModelLoader(JsonReader())
		val modelData = modelLoader.loadModelData(Gdx.files.internal("data/arena.g3dj"))
		val model = Model(modelData, TextureProvider.FileTextureProvider())
		val modelComponent = ModelComponent(model, x, y, z)
		entity.add(modelComponent)
		val shape = Bullet.obtainStaticNodeShape(model.nodes)
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(0f, null, shape, Vector3.Zero)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = MotionState(modelComponent.instance.transform)
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT
		//rigidBody.contactCallbackFilter = 0
		//rigidBody.contactCallbackFlag = BulletComponent.ARENA_FLAG
		rigidBody.userValue = BulletComponent.ARENA_FLAG
		//rigidBody.userIndex = BulletComponent.ARENA_FLAG
		//rigidBody.activationState = Collision.DISABLE_DEACTIVATION
		entity.add(BulletComponent(rigidBody, bodyInfo))
		return entity
	}

	//______________________________________________________________________________________________
	fun loadDome(x: Float, y: Float, z: Float): Entity {
		val modelLoader = G3dModelLoader(UBJsonReader())
		val model = modelLoader.loadModel(Gdx.files.getFileHandle("data/spacedome.g3db", Files.FileType.Internal))
		return Entity().add(ModelComponent(model, x, y, z))
	}

	//______________________________________________________________________________________________
	fun loadGun(x: Float, y: Float, z: Float): Entity {
		/*val modelLoader = G3dModelLoader(UBJsonReader())
		val model = modelLoader.loadModel(Gdx.files.getFileHandle("data/colt1911.g3db", Files.FileType.Internal))
		val modelComponent = ModelComponent(model, x, y, z)
		//modelComponent.instance.transform.rotate(0f, 1f, 0f, -90f)
		return Entity().add(modelComponent).add(GunComponent())*/
		val modelLoader = G3dModelLoader(JsonReader())
		val modelData = modelLoader.loadModelData(Gdx.files.internal("data/GUNMODEL.g3dj"))
		val model = Model(modelData, TextureProvider.FileTextureProvider())
		val modelComponent = ModelComponent(model, x, y, z)
		modelComponent.instance.transform.rotate(0f, 1f, 0f, 180f)
		val gunEntity = Entity()
		gunEntity.add(modelComponent)
		gunEntity.add(GunComponent())
		gunEntity.add(AnimationComponent(modelComponent.instance))
		return gunEntity
	}

	//______________________________________________________________________________________________
	fun dispose() {
		assetManager.dispose()
	}
}