package com.cesoft.cesgame.managers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.physics.bullet.collision.Collision
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesgame.bullet.MotionState
import com.cesoft.cesgame.components.BulletComponent
import com.cesoft.cesgame.components.FrustumCullingData
import com.cesoft.cesgame.components.ModelComponent


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object WallFactory {
	const val LONG = 50f
	const val HIGH = 25f
	const val THICK = 4f

	// dimension en X grados para frustum culling
	private val dim0 = Vector3(THICK*2, HIGH*2, LONG*2)
	private val dim90= Vector3(LONG*2, HIGH*2, THICK*2)
	private val dim45= Vector3(LONG*2, HIGH*2, LONG*2)

	private val dimCollision = Vector3(THICK+0f,HIGH+0f,LONG+0f)

	val mb = ModelBuilder()
	private val fileTexture = Gdx.files.internal("scene/wall/metal1.jpg")
	val POSITION_NORMAL =
			(VertexAttributes.Usage.Position
			or VertexAttributes.Usage.Normal
			or VertexAttributes.Usage.TextureCoordinates).toLong()

	//______________________________________________________________________________________________
	fun create(pos: Vector3, angle: Float = 0f): Entity {
		val entity = Entity()
		pos.y += HIGH

		/// MODELO
		val material = Material(ColorAttribute.createDiffuse(Color.WHITE))
		val texture = Texture(fileTexture)
		texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute1 = TextureAttribute(TextureAttribute.Diffuse, texture)
		textureAttribute1.scaleU = 2f
		textureAttribute1.scaleV = 4f
		material.set(textureAttribute1)

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

		modelComponent.instance.materials.get(0).set(textureAttribute1)
		modelComponent.instance.transform.rotate(Vector3.Y, angle)
		entity.add(modelComponent)

		/// COLISION
		val transf = modelComponent.instance.transform
		val pos2 = Vector3()
		transf.getTranslation(pos2)
		val transf2 = transf.cpy()
		transf2.setTranslation(pos2)

		val shape = btBoxShape(dimCollision)
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
		rigidBody.anisotropicFriction = Vector3(1f,1f,1f)
		rigidBody.spinningFriction = 1f
		entity.add(BulletComponent(rigidBody, bodyInfo))

		return entity
	}
}