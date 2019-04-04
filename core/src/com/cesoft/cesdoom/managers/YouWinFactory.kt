package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.Collision
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.bullet.MotionState
import com.cesoft.cesdoom.components.BulletComponent


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object YouWinFactory {
	const val SIZE = 35f

	//______________________________________________________________________________________________
	fun create(engine: Engine, pos: Vector3, angle: Float, assets: Assets): Entity {

		/// Entity
		val entity = Entity()

		/// Material
		val material = Material(ColorAttribute.createDiffuse(Color.WHITE))
		val texture = assets.getBike()
		texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge)
		val textureAttribute = TextureAttribute(TextureAttribute.Diffuse, texture)
		textureAttribute.scaleU = 1f
		textureAttribute.scaleV = 1f
		material.set(textureAttribute)
		material.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))

		/// Model
		val size = Vector2(100f, 40f)
		pos.y += size.y/2
		val modelComponent = DecalFactory.createDecal(material, size, pos, 0f, angle)
		modelComponent.instance.materials.get(0).set(textureAttribute)
		entity.add(modelComponent)

		/// Position & Shape
		val motionState = MotionState(Matrix4(pos, Quaternion(), Vector3(1f,1f,1f)))
		val shape = btBoxShape(Vector3(2*SIZE, 2*SIZE, 2*SIZE))

		/// Collision
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(0f, motionState, shape, Vector3.Zero)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = motionState
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE
		rigidBody.contactCallbackFilter = 0
		rigidBody.contactCallbackFlag = BulletComponent.YOU_WIN_FLAG
		rigidBody.userValue = BulletComponent.YOU_WIN_FLAG
		rigidBody.activationState = Collision.DISABLE_DEACTIVATION
		entity.add(BulletComponent(rigidBody, bodyInfo))

		engine.addEntity(entity)
		return entity
	}
}