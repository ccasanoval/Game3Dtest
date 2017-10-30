package com.cesoft.cesgame.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.utils.UBJsonReader
import com.cesoft.cesgame.bullet.MotionState
import com.cesoft.cesgame.components.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object SceneFactory {
	private val POSITION_NORMAL =
			(VertexAttributes.Usage.Position
			or VertexAttributes.Usage.Normal
			or VertexAttributes.Usage.TextureCoordinates).toLong()

	private val mb = ModelBuilder()

	//______________________________________________________________________________________________
	val materialJunk = Material(ColorAttribute.createDiffuse(Color.WHITE))
	val imgJunk = Gdx.files.internal("scene/junk.png")
	val ratioJunk = 546f/1000f
	//
	fun loadJunk(engine: Engine, len: Float) {
		val THICK = 0.1f
		val LONG = 500f
		val HIGH = 500f * ratioJunk
		val UP = HIGH/2

		/// MODEL
		val texture = Texture(imgJunk)
		texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge)
		val textureAttribute = TextureAttribute(TextureAttribute.Diffuse, texture)
		materialJunk.set(textureAttribute)
		materialJunk.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))
		val modelo: Model = mb.createBox(THICK, HIGH, LONG, materialJunk, POSITION_NORMAL)

		val dim = Vector3(THICK, HIGH, LONG)
		var pos : Vector3
		/// -X
		pos = Vector3(-len, UP, 0f)
		val modelComponent1 = ModelComponent(modelo, pos)
		modelComponent1.frustumCullingData = FrustumCullingData.create(pos, dim)
		val entity1 = Entity()
		entity1.add(modelComponent1)
		engine.addEntity(entity1)

		/// +X
		pos = Vector3(+len, UP, 0f)
		val modelComponent2 = ModelComponent(modelo, pos)
		modelComponent2.frustumCullingData = FrustumCullingData.create(pos, dim)
		val entity2 = Entity()
		entity2.add(modelComponent2)
		engine.addEntity(entity2)
	}

	//______________________________________________________________________________________________
	val materialSkyline = Material(ColorAttribute.createDiffuse(Color.DARK_GRAY))
	val imgSkyline = Gdx.files.internal("scene/skyline.png")
	val ratioSkyline = 380f/1400f
	//
	fun loadSkyline(engine: Engine, len: Float) {
		val THICK = 0.1f
		val LONG = 2*len
		val HIGH = 2*len*ratioSkyline
		val UP = len*ratioSkyline*3/5

		/// MODEL
		val texture = Texture(imgSkyline)
		texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge)
		val textureAttribute0 = TextureAttribute(TextureAttribute.Diffuse, texture)
		materialSkyline.set(textureAttribute0)
		materialSkyline.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))
		val modelo : Model = mb.createBox(THICK, HIGH, LONG, materialSkyline, POSITION_NORMAL)

		var dim = Vector3(THICK, HIGH, LONG)
		var pos: Vector3
		/// -X ?????????????????????????????????????????????????????????????????????????
		pos = Vector3(-len, UP, 0f)
		val modelComponentX_ = ModelComponent(modelo, pos)
		modelComponentX_.frustumCullingData = FrustumCullingData.create(pos, dim)
		engine.addEntity(Entity().add(modelComponentX_))

		/// +X
		pos = Vector3(+len, UP, 0f)
		val modelComponentX = ModelComponent(modelo, pos)
		modelComponentX.frustumCullingData = FrustumCullingData.create(pos, dim)
		engine.addEntity(Entity().add(modelComponentX))

		dim = Vector3(LONG, HIGH, THICK)
		/// -Z
		pos = Vector3(0f, UP, -len)
		val modelComponentZ_ = ModelComponent(modelo, pos)
		modelComponentZ_.frustumCullingData = FrustumCullingData.create(pos, dim)
		modelComponentZ_.instance.transform.rotate(Vector3.Y, 90f)
		engine.addEntity(Entity().add(modelComponentZ_))
		/// +Z
		pos = Vector3(0f, UP, +len)
		val modelComponentZ = ModelComponent(modelo, pos)
		modelComponentZ.frustumCullingData = FrustumCullingData.create(pos, dim)
		modelComponentZ.instance.transform.rotate(Vector3.Y, 90f)
		engine.addEntity(Entity().add(modelComponentZ))
	}

	//______________________________________________________________________________________________
	val imgSuelo = Gdx.files.internal("scene/ground.jpg")
	val materialSuelo = Material(ColorAttribute.createDiffuse(Color.WHITE))
	fun loadSuelo(len: Float): Entity {
		val entity = Entity()

		/// MODEL
		val texture = Texture(imgSuelo)
		texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute1 = TextureAttribute(TextureAttribute.Diffuse, texture)
		textureAttribute1.scaleU = 80f
		textureAttribute1.scaleV = 80f
		materialSuelo.set(textureAttribute1)
		val modelo : Model = mb.createBox(len, 5f, len, materialSuelo, POSITION_NORMAL)
		val pos = Vector3(0f,0f,0f)
		val dim = Vector3(len, 5f, len)
		val modelComponent = ModelComponent(modelo, pos)
		modelComponent.frustumCullingData = FrustumCullingData.create(pos, dim)
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
	private val modelLoader = G3dModelLoader(UBJsonReader())
	private val fileDome = Gdx.files.getFileHandle("scene/spaceDome/spacedome.g3db", Files.FileType.Internal)
	fun loadDome(): Entity {
		val model = modelLoader.loadModel(fileDome)
		return Entity().add(ModelComponent(model, Vector3(0f,0f,0f)))
	}

}