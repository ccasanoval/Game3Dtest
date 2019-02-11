package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
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
import com.cesoft.cesdoom.bullet.MotionState
import com.cesoft.cesdoom.components.BulletComponent
import com.cesoft.cesdoom.components.ModelComponent
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.cesoft.cesdoom.RenderUtils.FrustumCullingData
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.map.MapGraphFactory
import com.cesoft.cesdoom.systems.RenderSystem


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class RampFactory(assets: Assets) {
	companion object {
		const val LONG = 30f
		const val HIGH = 20f
		const val THICK = 1f
	}

	private val dim0 = Vector3(THICK*2, HIGH*2, LONG*2)
	private val dim90 = Vector3(LONG*2, THICK*2, HIGH*2)
	private val dimMax = Vector3(LONG*2, LONG*2, LONG*2)
	private val dim = Vector3(THICK, HIGH, LONG)

	private val material1 = Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY))
	private val material2 = Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY))

	private val modelBuilder = ModelBuilder()
	private val POSITION_NORMAL =
			(VertexAttributes.Usage.Position
					or VertexAttributes.Usage.Normal
					or VertexAttributes.Usage.TextureCoordinates).toLong()

	init {
		val texture1 = assets.getWallMetal2()
		val texture2 = assets.getWallMetal3()

		/// MODELO1
		texture1.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute1 = TextureAttribute(TextureAttribute.Diffuse, texture1)
		textureAttribute1.scaleU = 3f
		textureAttribute1.scaleV = 3f * LONG / HIGH
		material1.set(textureAttribute1)

		/// MODELO2
		texture2.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute2 = TextureAttribute(TextureAttribute.Diffuse, texture2)
		textureAttribute2.scaleU = 2f
		textureAttribute2.scaleV = 2f * LONG / HIGH
		material2.set(textureAttribute2)
		material2.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))
	}

	//______________________________________________________________________________________________
	fun create(mapFactory: MapGraphFactory, engine: Engine, pos: Vector3, angleX: Float = 0f, angleY: Float = 0f, angleZ: Float = 0f, type: Boolean = true): Entity {
		val entity = Entity()

		/// MODELO
		//modelComponent.instance.materials.get(0).set(textureAttribute1)
		val material = if(type) material1 else material2
		val modelo : Model = modelBuilder.createBox(THICK*2, HIGH*2, LONG*2, material, POSITION_NORMAL)
		val modelComponent = ModelComponent(modelo, pos)
		//
		if(angleX == 0f && angleY == 0f && angleZ == 0f) {
			modelComponent.frustumCullingData = FrustumCullingData.create(pos, dim0)
		}
		else if(angleX == 0f && angleY == 90f && angleZ == 90f) {
			modelComponent.frustumCullingData = FrustumCullingData.create(pos, dim90)
		}
		else {
			//TODO: ? por que no funciona  con esferica ? Quiza eliminar esferica ?
			/*val boundingBox = BoundingBox()
			modelComponent.instance.calculateBoundingBox(boundingBox)
			frustrumCullingData = FrustumCullingData.create(boundingBox)*/
			modelComponent.frustumCullingData = FrustumCullingData.create(pos, dimMax)
		}

		//
		modelComponent.instance.transform.rotate(Vector3.X, angleX)
		modelComponent.instance.transform.rotate(Vector3.Y, angleY)
		modelComponent.instance.transform.rotate(Vector3.Z, angleZ)
		entity.add(modelComponent)

		/// COLISION
		val shape = btBoxShape(dim)
		val motionState = MotionState(modelComponent.instance.transform)
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(0f, motionState, shape, Vector3.Zero)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = motionState
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT
		rigidBody.contactCallbackFilter = 0
		rigidBody.contactCallbackFlag = RenderSystem.CF_OCCLUDER_OBJECT//BulletComponent.SCENE_FLAG or
		rigidBody.userValue = 0//BulletComponent.SCENE_FLAG
		rigidBody.activationState = Collision.DISABLE_DEACTIVATION
		rigidBody.friction = 1f
		rigidBody.rollingFriction = 1f
		rigidBody.spinningFriction = 1f
		entity.add(BulletComponent(rigidBody, bodyInfo))

		engine.addEntity(entity)
		return entity
	}
}