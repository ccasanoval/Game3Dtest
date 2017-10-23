package com.cesoft.cesgame.managers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.Collision
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesgame.bullet.MotionState
import com.cesoft.cesgame.components.BulletComponent
import com.cesoft.cesgame.components.ModelComponent
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute



////////////////////////////////////////////////////////////////////////////////////////////////////
//
object RampFactory {
	const val LONG = 30f
	const val HIGH = 20f
	const val THICK = 1f

	private val text1 = Gdx.files.internal("scene/wall/metal2.png")
	private val text2 = Gdx.files.internal("scene/wall/metal3.png")

	private val material1 = Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY))
	private val material2 = Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY))

	private val modelBuilder = ModelBuilder()
	private val POSITION_NORMAL =
			(VertexAttributes.Usage.Position
					or VertexAttributes.Usage.Normal
					or VertexAttributes.Usage.TextureCoordinates).toLong()

	init {
		/// MODELO1
		val texture1 = Texture(text1)
		texture1.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute1 = TextureAttribute(TextureAttribute.Diffuse, texture1)
		textureAttribute1.scaleU = 3f
		textureAttribute1.scaleV = 3f * LONG / HIGH
		material1.set(textureAttribute1)
		//model1 = modelBuilder1.createBox(THICK *2, HIGH *2, LONG *2, material1, POSITION_NORMAL)

		/// MODELO2
		val texture2 = Texture(text2)
		texture2.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute2 = TextureAttribute(TextureAttribute.Diffuse, texture2)
		textureAttribute2.scaleU = 2f
		textureAttribute2.scaleV = 2f * LONG / HIGH
		material2.set(textureAttribute2)
		material2.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))
	}

	//______________________________________________________________________________________________
	fun create(pos: Vector3, angleX: Float = 0f, angleY: Float = 0f, angleZ: Float = 0f, type: Boolean = true): Entity {
		val entity = Entity()

		/// MODELO
		//modelComponent.instance.materials.get(0).set(textureAttribute1)
		val material = if(type) material1 else material2
		val modelo : Model = modelBuilder.createBox(THICK *2, HIGH *2, LONG *2, material, POSITION_NORMAL)
		val modelComponent = ModelComponent(modelo, pos)
		//
		modelComponent.instance.transform.rotate(Vector3.X, angleX)
		modelComponent.instance.transform.rotate(Vector3.Y, angleY)
		modelComponent.instance.transform.rotate(Vector3.Z, angleZ)
		entity.add(modelComponent)

		/// COLISION
		val transf = modelComponent.instance.transform
		val pos2 = Vector3()
		transf.getTranslation(pos2)
		val transf2 = transf.cpy()
		transf2.setTranslation(pos2)

		val shape = btBoxShape(Vector3(THICK + 0f, HIGH + 0f, LONG + 0f))
		//val shape = Bullet.obtainStaticNodeShape(model.nodes)
		val motionState = MotionState(transf2)
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(0f, motionState, shape, Vector3.Zero)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = motionState//modelComponent.instance.transform)
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT
		rigidBody.contactCallbackFilter = BulletComponent.GROUND_FLAG or BulletComponent.PLAYER_FLAG
		rigidBody.contactCallbackFlag = BulletComponent.SCENE_FLAG
		rigidBody.userValue = BulletComponent.SCENE_FLAG
		rigidBody.activationState = Collision.DISABLE_DEACTIVATION
		rigidBody.friction = 1f
		rigidBody.rollingFriction = 1f
		rigidBody.anisotropicFriction = Vector3(1f, 1f, 1f)
		rigidBody.spinningFriction = 1f
		entity.add(BulletComponent(rigidBody, bodyInfo))

		return entity
	}
}