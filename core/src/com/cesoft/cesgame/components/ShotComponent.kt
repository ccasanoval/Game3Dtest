package com.cesoft.cesgame.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
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
		const val MASA = .15f
		const val FUERZA = 3500f

		//______________________________________________________________________________________________
		//
		fun createShot(pos: Vector3, dir: Vector3, mass: Float = ShotComponent.MASA, force: Float = ShotComponent.FUERZA): Entity {
			val entity = Entity()

			/// SHOT
			entity.add(ShotComponent())

			//https://stackoverflow.com/questions/31211829/add-glow-to-image-actor-in-libgdx
			/// MODEL
			val mb = ModelBuilder()
			val material = Material(ColorAttribute.createDiffuse(Color.RED))
			val flags = VertexAttributes.Usage.ColorUnpacked or VertexAttributes.Usage.Position
			val model : Model = mb.createBox(4.5f, 4.5f, 4.5f, material, flags.toLong())
			val modelComponent = ModelComponent(model, pos)
			entity.add(modelComponent)

			/// COLLISION

			/*val transf = modelComponent.instance.transform
			val pos2 = Vector3()
			transf.getTranslation(pos2)
			val transf2 = transf.cpy()
			pos2.y+=20f
			transf2.setTranslation(pos2)*/

			val shape = btBoxShape(Vector3(.35f, .35f, .35f))
			val localInertia = Vector3()
			shape.calculateLocalInertia(mass, localInertia)
			val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia)
			val rigidBody = btRigidBody(bodyInfo)
			rigidBody.userData = entity
			rigidBody.motionState = MotionState(modelComponent.instance.transform)
			//rigidBody.motionState = MotionState(Matrix4().setTranslation(pos))
			rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK
			rigidBody.contactCallbackFilter = 0
			rigidBody.contactCallbackFlag = BulletComponent.SHOT_FLAG
			rigidBody.userValue = BulletComponent.SHOT_FLAG
			//rigidBody.applyCentralForce(dir.scl(force))
			entity.add(BulletComponent(rigidBody, bodyInfo))

			/// Fuerza
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
