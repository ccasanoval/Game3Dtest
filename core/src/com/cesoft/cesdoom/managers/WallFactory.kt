package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.physics.bullet.collision.Collision
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.bullet.MotionState
import com.cesoft.cesdoom.components.BulletComponent
import com.cesoft.cesdoom.renderUtils.FrustumCullingData
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.components.ModelComponent
import com.cesoft.cesdoom.map.MapGraphFactory


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object WallFactory {
	const val LONG = 25f
	const val HIGH = 25f
	const val THICK = 4f

	enum class Type { BRICK, STEEL, GRILLE }

	// dimension en X grados para frustum culling
	private val dim0 = Vector3(THICK*2, HIGH*2, LONG*2)
	private val dim90= Vector3(LONG*2, HIGH*2, THICK*2)
	private val dim45= Vector3(LONG*2, HIGH*2, LONG*2)

	private val dimCollision = Vector3(THICK+0f,HIGH+0f,LONG+0f)

	private val mb = ModelBuilder()
	private val POSITION_NORMAL =
			(VertexAttributes.Usage.Position
			or VertexAttributes.Usage.Normal
			or VertexAttributes.Usage.TextureCoordinates).toLong()

	private val material1 = Material(ColorAttribute.createDiffuse(Color.WHITE))
	private val material2 = Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY))
	private val material3 = Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY))

	fun iniMaterials(assets: Assets) {
		val texture1 = assets.getWallMetal1()
		val texture2 = assets.getWallMetal2()
		val texture3 = assets.getWallMetal3()

		texture1.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute1 = TextureAttribute(TextureAttribute.Diffuse, texture1)
		textureAttribute1.scaleU = 2f
		textureAttribute1.scaleV = 2f
		material1.set(textureAttribute1)

		texture2.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute2 = TextureAttribute(TextureAttribute.Diffuse, texture2)
		textureAttribute2.scaleU = 3f
		textureAttribute2.scaleV = 3f * RampFactory.LONG / RampFactory.HIGH
		material2.set(textureAttribute2)

		texture3.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute3 = TextureAttribute(TextureAttribute.Diffuse, texture3)
		textureAttribute3.scaleU = 3f
		textureAttribute3.scaleV = 2f * RampFactory.LONG / RampFactory.HIGH
		material3.set(textureAttribute3)
		material3.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))
	}


	//______________________________________________________________________________________________
	fun create(mapFactory: MapGraphFactory, engine: Engine, pos: Vector3, angle: Float, type: Type = Type.BRICK): Entity {

		/// GraphMap
		WallMapFactory.create(mapFactory, pos, angle, 0)

		/// Entity
		val entity = Entity()
		pos.y += HIGH

		/// MATERIAL
		val material = when(type) {
			Type.BRICK -> material1
			Type.STEEL -> material2
			Type.GRILLE -> material3
		}

		/// MODEL
		val modelo : Model = mb.createBox(THICK*2, HIGH*2, LONG*2, material, POSITION_NORMAL)
		val modelComponent = ModelComponent(modelo, pos)

		// Mejora para frustum culling
		val frustumCullingData : FrustumCullingData
		if(angle == 0f)//angle > -5f && angle < 5f)
			frustumCullingData = FrustumCullingData.create(pos, dim0)
		else if(angle == 90f)
			frustumCullingData = FrustumCullingData.create(pos, dim90)
		else if(angle == 45f || angle == -45f)
			frustumCullingData = FrustumCullingData.create(pos, dim45)
		else {
			val boundingBox = BoundingBox()
			modelComponent.instance.calculateBoundingBox(boundingBox)
			frustumCullingData = FrustumCullingData.create(boundingBox)
		}
		modelComponent.frustumCullingData = frustumCullingData

		modelComponent.instance.transform.rotate(Vector3.Y, angle)
		entity.add(modelComponent)

		/// COLLISION
		val transf = modelComponent.instance.transform
		val shape = btBoxShape(dimCollision)
		val motionState = MotionState(transf)
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(0f, motionState, shape, Vector3.Zero)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = motionState
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT
		rigidBody.contactCallbackFilter = 0
		rigidBody.userValue = BulletComponent.SCENE_FLAG
		rigidBody.activationState = Collision.DISABLE_DEACTIVATION
		rigidBody.friction = 1f
		rigidBody.rollingFriction = 1f
		rigidBody.spinningFriction = 1f
		entity.add(BulletComponent(rigidBody, bodyInfo))

		engine.addEntity(entity)
		return entity
	}
}