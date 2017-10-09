package com.cesoft.cesgame.managers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
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
import com.badlogic.gdx.math.Vector
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
	fun createEnemy(enemyModel: Model, pos: Vector3, index: Int): Entity {
		val entity = Entity()

		val enemyModelComponent = ModelComponent(enemyModel, pos)
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
	fun load1(pos: Vector3): Entity {
		val entity = Entity()

		val modelLoader = G3dModelLoader(UBJsonReader())
		val modelData = modelLoader.loadModelData(Gdx.files.internal("data/ruins/a.g3db"))

		pos.y -=20
		val model = Model(modelData, TextureProvider.FileTextureProvider())
		/*for(i in 0 until model.nodes.size - 1)
			model.nodes[i].scale.scl(.5f)*/
		val modelComponent = ModelComponent(model, pos)
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
	fun load2(pos: Vector3): Entity {
		val entity = Entity()

		val modelLoader = G3dModelLoader(UBJsonReader())
		val modelData = modelLoader.loadModelData(Gdx.files.internal("data/maze/a.g3db"))

		val model = Model(modelData, TextureProvider.FileTextureProvider())
		//for(i in 0 until model.nodes.size - 1)
		//	model.nodes[i].scale.scl(205f)
		val modelComponent = ModelComponent(model, pos)
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
	fun loadSuelo(pos: Vector3): Entity {
		val entity = Entity()

		/// MODEL
		val mb = ModelBuilder()
		val material = Material(ColorAttribute.createDiffuse(Color.DARK_GRAY))
		val texture = Texture(Gdx.files.internal("data/ground.jpg"))
		texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute1 = TextureAttribute(TextureAttribute.Diffuse, texture)
		textureAttribute1.scaleU = 80f
		textureAttribute1.scaleV = 80f
		material.set(textureAttribute1)
		val modelo : Model = mb.createBox(20000f, 1f, 20000f, material, POSITION_NORMAL)
		val modelComponent = ModelComponent(modelo, pos)
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
	fun loadDome(pos: Vector3): Entity {
		val modelLoader = G3dModelLoader(UBJsonReader())
		val model = modelLoader.loadModel(Gdx.files.getFileHandle("data/spaceDome/spacedome.g3db", Files.FileType.Internal))
		return Entity().add(ModelComponent(model, pos))
	}


	//______________________________________________________________________________________________
	fun dispose() {
		assetManager.dispose()
	}
}