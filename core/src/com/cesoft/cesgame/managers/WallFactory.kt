package com.cesoft.cesgame.managers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.physics.bullet.collision.Collision
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.utils.UBJsonReader
import com.cesoft.cesgame.bullet.MotionState
import com.cesoft.cesgame.components.BulletComponent
import com.cesoft.cesgame.components.ModelComponent


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object WallFactory {
	private val modelLoader = G3dModelLoader(UBJsonReader())
	private val modelData = modelLoader.loadModelData(Gdx.files.internal("scene/wall/a.g3db"))

	//______________________________________________________________________________________________
	fun create(pos: Vector3, angle: Float = 0f): Entity {
		val entity = Entity()

		val model = Model(modelData)

//		for(node in model.nodes)
//			node.scale.scl(.8f)

		pos.y += 0
		val modelComponent = ModelComponent(model, pos)
		//modelComponent.instance.transform.translate(pos)
		modelComponent.instance.transform.rotate(Vector3.Y, angle)
		entity.add(modelComponent)

		/// COLLISION
		val pos2 = Vector3()
		modelComponent.instance.transform.getTranslation(pos)
		pos.y+=20f
		val transf = Matrix4().setTranslation(pos)

		val shape = btBoxShape(Vector3(5.5f,30f,54f))
		//val shape = Bullet.obtainStaticNodeShape(model.nodes)
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(0f, null, shape, Vector3.Zero)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = MotionState(transf)//modelComponent.instance.transform)
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT
		rigidBody.contactCallbackFilter = BulletComponent.GROUND_FLAG or BulletComponent.PLAYER_FLAG
		rigidBody.contactCallbackFlag = BulletComponent.SCENE_FLAG
		rigidBody.userValue = BulletComponent.SCENE_FLAG
		rigidBody.activationState = Collision.DISABLE_DEACTIVATION
		rigidBody.friction = 1000f
		rigidBody.rollingFriction = 10000f
		rigidBody.anisotropicFriction = Vector3(1000f,1000f,1000f)
		rigidBody.spinningFriction = 10000f
		entity.add(BulletComponent(rigidBody, bodyInfo))

		return entity
	}
}