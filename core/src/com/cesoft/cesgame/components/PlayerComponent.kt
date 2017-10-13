package com.cesoft.cesgame.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesgame.bullet.MotionState


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object PlayerComponent : Component
{
	var isSaltando = false
	var health: Float = 100f //TODO: pasar a la clase
	var score: Int = 0 //TODO: pasar a la clase?
	const val MASA = 65f
	const val ALTURA = 15f
	const val FUERZA_MOVIL = 2000f
	const val FUERZA_PC = 5000f
	//const val FRICTION = 10f
	//const val FRICTION_ROLLING = 10f

	//______________________________________________________________________________________________
	private var lastHurt = 0L
	fun hurt(pain: Float)
	{
		if(System.currentTimeMillis() > lastHurt+500) {
			health -= pain
			lastHurt = System.currentTimeMillis()
		}
	}

	//______________________________________________________________________________________________
	fun create(pos: Vector3): Entity {
		val entity = Entity()

		val localInertia = Vector3()
		val shape = btSphereShape(ALTURA)//btCylinderShape(Vector3(3f,ALTURA/2,3f))//btCapsuleShape(1f, ALTURA)////
		shape.calculateLocalInertia(PlayerComponent.MASA, localInertia)
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(PlayerComponent.MASA, null, shape, localInertia)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = MotionState(Matrix4().translate(pos))
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK
		rigidBody.contactCallbackFilter = BulletComponent.ENEMY_FLAG or BulletComponent.SCENE_FLAG or BulletComponent.GROUND_FLAG
		rigidBody.contactCallbackFlag = BulletComponent.PLAYER_FLAG
		rigidBody.userValue = BulletComponent.PLAYER_FLAG
		//rigidBody.friction = FRICTION
		//rigidBody.rollingFriction = FRICTION_ROLLING
		rigidBody.activationState = Collision.DISABLE_DEACTIVATION

		entity.add(BulletComponent(rigidBody, bodyInfo))
		entity.add(this)

		return entity
	}

}
