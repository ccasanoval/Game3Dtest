package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.Collision
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.bullet.MotionState
import com.cesoft.cesdoom.components.BulletComponent


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object YouWinFactory {
	const val SIZE = 30f

	//______________________________________________________________________________________________
	fun create(engine: Engine, pos: Vector3): Entity {

		/// Entity
		val entity = Entity()
		pos.y += SIZE

		/// Position & Shape
		val rot = Quaternion()
		val transf = Matrix4(pos, rot, Vector3(1f,1f,1f))
		val motionState = MotionState(transf)
		val shape = btBoxShape(Vector3(2*SIZE, 2*SIZE, 2*SIZE))

		/// Collision
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(0f, motionState, shape, Vector3.Zero)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = motionState
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE
		rigidBody.contactCallbackFilter = 0//BulletComponent.PLAYER_FLAG
		rigidBody.contactCallbackFlag = BulletComponent.YOU_WIN_FLAG
		rigidBody.userValue = BulletComponent.YOU_WIN_FLAG
		rigidBody.activationState = Collision.DISABLE_DEACTIVATION
		entity.add(BulletComponent(rigidBody, bodyInfo))

		engine.addEntity(entity)
		return entity
	}
}