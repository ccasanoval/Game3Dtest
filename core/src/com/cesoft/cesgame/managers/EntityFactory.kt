package com.cesoft.cesgame.managers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
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
import com.badlogic.gdx.utils.UBJsonReader
import com.cesoft.cesgame.bullet.MotionState
import com.cesoft.cesgame.components.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.cesoft.cesgame.systems.RenderSystem


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object EntityFactory {
	private val POSITION_NORMAL =
			(VertexAttributes.Usage.Position
			or VertexAttributes.Usage.Normal
			or VertexAttributes.Usage.TextureCoordinates).toLong()

	//______________________________________________________________________________________________
	private val assetManager = AssetManager()//TODO: hacer singleton y no object
	fun createEnemy(enemyModel: Model, x: Float, y: Float, z: Float, index: Int): Entity {
		val entity = Entity()

		val enemyModelComponent = ModelComponent(enemyModel, x, y, z)
		//enemyModelComponent!!.instance.transform.set(enemyModelComponent!!.matrix4.setTranslation(x, y, z))
		entity.add(enemyModelComponent)
		entity.add(EnemyComponent(EnemyComponent.STATE.HUNTING))

		val animationComponent = AnimationComponent(enemyModelComponent.instance)
		animationComponent.animate(EnemyAnimations.id, EnemyAnimations.offsetRun1, EnemyAnimations.durationRun1, -1, 1)    //TODO variable animationspeed
		entity.add(animationComponent)

		entity.add(StatusComponent(animationComponent))
		entity.add(EnemyDieParticleComponent(RenderSystem.particleSystem, assetManager))

		val localInertia = Vector3()
		val shape = btSphereShape(5f)//btCylinderShape(Vector3(4f,4f,4f))//btBoxShape(Vector3(3f,3f,3f))// btCapsuleShape(3f, 6f)
		shape.calculateLocalInertia(EnemyComponent.MASA, localInertia)
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(EnemyComponent.MASA, null, shape, localInertia)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = MotionState(enemyModelComponent.instance.transform)
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK
		rigidBody.contactCallbackFilter = BulletComponent.SHOT_FLAG
		rigidBody.contactCallbackFlag = BulletComponent.ENEMY_FLAG
		rigidBody.userValue = BulletComponent.ENEMY_FLAG
		rigidBody.userIndex = index
		rigidBody.friction = 3f
		rigidBody.rollingFriction = 3f
		entity.add(BulletComponent(rigidBody, bodyInfo))

		return entity
	}
	//______________________________________________________________________________________________
	//TODO: Change again by ray collision¿?
	fun createShot(pos: Vector3, dir: Vector3, mass: Float = ShotComponent.MASA, force: Float = ShotComponent.FUERZA): Entity {
		val entity = Entity()

		/// SHOT
		entity.add(ShotComponent())

		/// MODEL
		//val mb = ModelBuilder()
		//val material = Material(ColorAttribute.createDiffuse(Color.GREEN))
		//val model : Model = mb.createBox(.5f, .5f, .5f, material, POSITION_NORMAL)
		//val modelComponent = ModelComponent(model, pos.x, pos.y, pos.z)
		//entity.add(modelComponent)

		/// COLLISION
		val localInertia = Vector3()
		val shape = btBoxShape(Vector3(.25f,.25f,.25f))
		shape.calculateLocalInertia(mass, localInertia)
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		//rigidBody.motionState = MotionState(modelComponent.instance.transform)
		rigidBody.motionState = MotionState(Matrix4().setTranslation(pos))
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK
		rigidBody.contactCallbackFilter = 0
		rigidBody.contactCallbackFlag = BulletComponent.SHOT_FLAG
		rigidBody.userValue = BulletComponent.SHOT_FLAG
		//rigidBody.applyCentralForce(dir.scl(force))
		entity.add(BulletComponent(rigidBody, bodyInfo))
		rigidBody.applyCentralForce(dir.scl(force))

		return entity
	}

	//______________________________________________________________________________________________
	fun loadScene(x: Float, y: Float, z: Float): Entity {
		val entity = Entity()
		//val modelLoader = G3dModelLoader(JsonReader())
		//val modelData = modelLoader.loadModelData(Gdx.files.internal("data/arena/arena.g3dj"))
		val modelLoader = G3dModelLoader(UBJsonReader())
		val modelData = modelLoader.loadModelData(Gdx.files.internal("data/prision/a.g3db"))

		val model = Model(modelData, TextureProvider.FileTextureProvider())
		//for(i in 0 until model.nodes.size - 1)
		//	model.nodes[i].scale.scl(0.5f)
		val modelComponent = ModelComponent(model, x, y, z)
		entity.add(modelComponent)
		val shape = Bullet.obtainStaticNodeShape(model.nodes)
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(0f, null, shape, Vector3.Zero)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = MotionState(modelComponent.instance.transform)
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT
		rigidBody.contactCallbackFilter = 0
		rigidBody.contactCallbackFlag = BulletComponent.SCENE_FLAG
		rigidBody.userValue = BulletComponent.SCENE_FLAG
		//rigidBody.userIndex = BulletComponent.ARENA_FLAG
		//rigidBody.activationState = Collision.DISABLE_DEACTIVATION
		entity.add(BulletComponent(rigidBody, bodyInfo))
		return entity
	}
	//______________________________________________________________________________________________
	fun loadSuelo(x: Float, y: Float, z: Float): Entity {
		val entity = Entity()

		/// MODEL
		val mb = ModelBuilder()
		val material = Material(ColorAttribute.createDiffuse(Color.DARK_GRAY))
		//val texture = Texture("data/ground.png")
		val texture = Texture(Gdx.files.internal("data/ground.png"))
		val textureAttribute1 = TextureAttribute(TextureAttribute.Diffuse, texture)
		material.set(textureAttribute1)
		val modelo : Model = mb.createBox(20000f, 1f, 20000f, material, POSITION_NORMAL)
		val modelComponent = ModelComponent(modelo, x, y, z)
		modelComponent.instance.materials.get(0).set(textureAttribute1)
		entity.add(modelComponent)

		/// COLLISION
		//val shape = btBoxShape(Vector3(5000f, 1f, 5000f))
		val shape = Bullet.obtainStaticNodeShape(modelo.nodes)
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(0f, null, shape, Vector3.Zero)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = MotionState(modelComponent.instance.transform)
		//rigidBody.motionState = MotionState(Matrix4().setTranslation(Vector3(x,y,z)))
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT
		rigidBody.contactCallbackFilter = 0
		rigidBody.contactCallbackFlag = BulletComponent.GROUND_FLAG
		rigidBody.userValue = BulletComponent.GROUND_FLAG
		rigidBody.activationState = Collision.DISABLE_DEACTIVATION

		entity.add(BulletComponent(rigidBody, bodyInfo))
		return entity
	}


	//______________________________________________________________________________________________
	fun loadDome(x: Float, y: Float, z: Float): Entity {
		val modelLoader = G3dModelLoader(UBJsonReader())
		val model = modelLoader.loadModel(Gdx.files.getFileHandle("data/spaceDome/spacedome.g3db", Files.FileType.Internal))
		return Entity().add(ModelComponent(model, x, y, z))
	}

	//______________________________________________________________________________________________
	//TODO: objeto arma, tipo x, que tenga nombres de animaciones...
	fun loadGun() = GunFactory.new(GunComponent.TYPE.CZ805)

	//______________________________________________________________________________________________
	fun dispose() {
		assetManager.dispose()
	}
}