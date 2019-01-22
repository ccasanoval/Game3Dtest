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
import com.cesoft.cesdoom.RenderUtils.FrustumCullingData
import com.cesoft.cesdoom.components.ModelComponent
import com.cesoft.cesdoom.map.MapGraphFactory
import com.cesoft.cesdoom.systems.RenderSystem


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

	private val material0 = Material(ColorAttribute.createDiffuse(Color.WHITE))
	private val material1 = Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY))
	private val material2 = Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY))
	fun ini(texture0: Texture, texture1: Texture, texture2: Texture) {

		texture0.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute0 = TextureAttribute(TextureAttribute.Diffuse, texture0)
		textureAttribute0.scaleU = 2f
		textureAttribute0.scaleV = 2f
		material0.set(textureAttribute0)

		texture1.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute1 = TextureAttribute(TextureAttribute.Diffuse, texture1)
		textureAttribute1.scaleU = 3f
		textureAttribute1.scaleV = 3f * RampFactory.LONG / RampFactory.HIGH
		material1.set(textureAttribute1)

		texture2.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute2 = TextureAttribute(TextureAttribute.Diffuse, texture2)
		textureAttribute2.scaleU = 2f
		textureAttribute2.scaleV = 2f * RampFactory.LONG / RampFactory.HIGH
		material2.set(textureAttribute2)
		material2.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))
	}


	//______________________________________________________________________________________________
	fun create(mapFactory: MapGraphFactory, pos: Vector3, angle: Float, engine: Engine, type: Type = Type.BRICK): Entity {

		/// GraphMap
		WallMapFactory.create(mapFactory, pos, angle, 0)

		/// Entity
		val entity = Entity()
		pos.y += HIGH

		/// MATERIAL
		val material = when(type) {
			Type.BRICK -> material0
			Type.STEEL -> material1
			Type.GRILLE -> material2
		}

		/// MODELO
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

		//modelComponent.instance.materials.get(0).set(textureAttribute1)
		modelComponent.instance.transform.rotate(Vector3.Y, angle)
		entity.add(modelComponent)

		/// COLISION
		val transf = modelComponent.instance.transform
		val shape = btBoxShape(dimCollision)
		//val shape = Bullet.obtainStaticNodeShape(model.nodes)
		val motionState = MotionState(transf)
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(0f, motionState, shape, Vector3.Zero)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = motionState//modelComponent.instance.transform)
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT
		rigidBody.contactCallbackFilter = 0//BulletComponent.GROUND_FLAG or BulletComponent.PLAYER_FLAG
		rigidBody.contactCallbackFlag = BulletComponent.SCENE_FLAG or RenderSystem.CF_OCCLUDER_OBJECT
		rigidBody.userValue = BulletComponent.SCENE_FLAG
		rigidBody.activationState = Collision.DISABLE_DEACTIVATION
		rigidBody.friction = 1f
		rigidBody.rollingFriction = 1f
		//rigidBody.anisotropicFriction = Vector3(1f,1f,1f)
		rigidBody.spinningFriction = 1f
		entity.add(BulletComponent(rigidBody, bodyInfo))

		engine.addEntity(entity)
		return entity
	}
}