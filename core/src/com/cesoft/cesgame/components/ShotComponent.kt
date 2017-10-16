package com.cesoft.cesgame.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
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
		const val MASA = .10f
		const val FUERZA = 3500f

		//______________________________________________________________________________________________
		//
		/*fun createShot(pos: Vector3, dir: Vector3, mass: Float = ShotComponent.MASA, force: Float = ShotComponent.FUERZA): Entity {
			///----------------------------
			/// COLLISION BY BULLETS
			val entity = Entity()

			pos.add(dir.scl(2f))

			/// SHOT
			entity.add(ShotComponent())

			//https://stackoverflow.com/questions/31211829/add-glow-to-image-actor-in-libgdx
			/// MODEL
			val mb = ModelBuilder()
			val material = Material(ColorAttribute.createDiffuse(Color.RED))
			val flags = VertexAttributes.Usage.ColorUnpacked or VertexAttributes.Usage.Position
			val model : Model = mb.createBox(.5f, .5f, .5f, material, flags.toLong())
			val modelComponent = ModelComponent(model, pos)
			entity.add(modelComponent)

			/// COLLISION
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
			entity.add(BulletComponent(rigidBody, bodyInfo))

			/// Fuerza
			rigidBody.applyCentralForce(dir.scl(force))

			return entity

		}*/


	}
}
