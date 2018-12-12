package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.bullet.MotionState


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object PlayerComponent : Component
{
	var isSaltando = false
	var health: Float = 100f
	var score: Int = 0
	const val MASA = .65f
	const val ALTURA = 15f
	const val RADIO = 12f
	const val FUERZA_MOVIL = 2000f
	const val FUERZA_PC = 5000f

	lateinit var colorAmbiente : ColorAttribute

	//______________________________________________________________________________________________
	private var lastHurt = 0L
	fun hurt(pain: Float)
	{
		if(System.currentTimeMillis() > lastHurt+800) {
			health -= pain
			colorAmbiente.color.set(.8f, 0f, 0f, 1f)//Pasar RenderObject y llamar a CamaraRoja(true)...
			lastHurt = System.currentTimeMillis()
		}
		//else if(lastHurt+50 < System.currentTimeMillis())
		//	colorAmbiente.color.set(.8f, .8f, .8f, 1f)//Pasar RenderObject y llamar a CamaraRoja(false)...
	}

	//______________________________________________________________________________________________
	fun update()
	{
		if(lastHurt+50 < System.currentTimeMillis())
			colorAmbiente.color.set(.8f, .8f, .8f, 1f)//Pasar RenderObject y llamar a CamaraRoja(false)...
	}

	//______________________________________________________________________________________________
	private val posTemp = Vector3()
	fun create(pos: Vector3): Entity {
		val entity = Entity()

		val shape = btSphereShape(RADIO)//btCylinderShape(Vector3(3f,ALTURA/2,3f))//btCapsuleShape(6f, ALTURA)//
		shape.calculateLocalInertia(MASA, posTemp)
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(MASA, null, shape, posTemp)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = MotionState(Matrix4().translate(pos))
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK
		rigidBody.contactCallbackFilter = BulletComponent.ENEMY_FLAG or BulletComponent.SCENE_FLAG or BulletComponent.GROUND_FLAG
		rigidBody.contactCallbackFlag = BulletComponent.PLAYER_FLAG
		rigidBody.userValue = BulletComponent.PLAYER_FLAG
		rigidBody.activationState = Collision.DISABLE_DEACTIVATION
		rigidBody.friction = 0f
		rigidBody.rollingFriction = 1000000000f

		entity.add(BulletComponent(rigidBody, bodyInfo))
		entity.add(this)

		return entity
	}

}
