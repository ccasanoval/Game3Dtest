package com.cesoft.cesgame.managers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.Collision
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.utils.UBJsonReader
import com.cesoft.cesgame.bullet.MotionState
import com.cesoft.cesgame.components.BulletComponent
import com.cesoft.cesgame.components.ModelComponent


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object WallFactory {
	const val LONG = 48f
	const val HIGH = 25.5f
	const val THICK = 3f

	private val modelLoader = G3dModelLoader(UBJsonReader())
	private val modelData = modelLoader.loadModelData(Gdx.files.internal("scene/wall/wall.g3db"))

	//______________________________________________________________________________________________
	fun create(pos: Vector3, angle: Float = 0f): Entity {
		val entity = Entity()

		/// MODELO
		val model = Model(modelData)
		val modelComponent = ModelComponent(model, pos)
		//modelComponent.instance.transform.translate(pos)
		modelComponent.instance.transform.rotate(Vector3.Y, angle)
		entity.add(modelComponent)

		/// COLISION
		val transf = modelComponent.instance.transform
		val pos2 = Vector3()
		transf.getTranslation(pos2)
		val transf2 = transf.cpy()
		pos2.y+=20f
		transf2.setTranslation(pos2)

		val shape = btBoxShape(Vector3(THICK+1f,HIGH+1f,LONG+2f))
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