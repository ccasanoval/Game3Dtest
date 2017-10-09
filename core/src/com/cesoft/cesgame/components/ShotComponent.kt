package com.cesoft.cesgame.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesgame.bullet.MotionState

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by ccasanova on 04/10/2017.
class ShotComponent : Component {

	var aliveTime: Float = 0f
		private set

	fun update(delta: Float) {
		aliveTime += delta
	}
	fun isEnd() = aliveTime > 1f

	companion object {
		const val MASA = .25f
		const val FUERZA = 5000f

		//______________________________________________________________________________________________
		//TODO: Change again by ray collision¿?
		fun createShot(pos: Vector3, dir: Vector3, mass: Float = ShotComponent.MASA, force: Float = ShotComponent.FUERZA): Entity {
			val entity = Entity()

			/// SHOT
			entity.add(ShotComponent())

			/// MODEL
			//val mb = ModelBuilder()
			//val material = Material(ColorAttribute.createDiffuse(Color.GREEN))
			//val model : Model = mb.createBox(.5f, .5f, .5f, material, POSITION_NORMAL)
			//val modelComponent = ModelComponent(model, pos.x, pos.y, pos.z)
			//entity.add(modelComponent)

			/// COLLISION
			val localInertia = Vector3()
			val shape = btBoxShape(Vector3(.25f, .25f, .25f))
			shape.calculateLocalInertia(mass, localInertia)
			val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia)
			val rigidBody = btRigidBody(bodyInfo)
			rigidBody.userData = entity
			//rigidBody.motionState = MotionState(modelComponent.instance.transform)
			rigidBody.motionState = MotionState(Matrix4().setTranslation(pos))
			rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK
			rigidBody.contactCallbackFilter = 0
			rigidBody.contactCallbackFlag = BulletComponent.SHOT_FLAG
			rigidBody.userValue = BulletComponent.SHOT_FLAG
			//rigidBody.applyCentralForce(dir.scl(force))
			entity.add(BulletComponent(rigidBody, bodyInfo))
			rigidBody.applyCentralForce(dir.scl(force))

			return entity
		}
		/*val rayFrom = Vector3()
		val rayTo = Vector3()
		val ray = camera.getPickRay((Gdx.graphics.width / 2).toFloat(), (Gdx.graphics.height / 2).toFloat())
		rayFrom.set(ray.origin)
		rayTo.set(ray.direction).scl(50f).add(rayFrom)
		rayTestCB.collisionObject = null
		rayTestCB.closestHitFraction = 1f
		rayTestCB.setRayFromWorld(rayFrom)
		rayTestCB.setRayToWorld(rayTo)
		gameWorld.bulletSystem.collisionWorld.rayTest(rayFrom, rayTo, rayTestCB)
		if(rayTestCB.hasHit()) {
			Gdx.app.error("CESGAME", "-------------------------- DISPARO DIO ------------------------------")

			val obj = rayTestCB.collisionObject
			if((obj.userData as Entity).getComponent(EnemyComponent::class.java) != null) {
				if((obj.userData as Entity).getComponent(StatusComponent::class.java).alive) {
					(obj.userData as Entity).getComponent(StatusComponent::class.java).alive = false
					PlayerComponent.score += 100
				}
			}
		}*/
	}
}
