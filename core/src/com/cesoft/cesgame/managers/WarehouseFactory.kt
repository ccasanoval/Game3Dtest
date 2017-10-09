package com.cesoft.cesgame.managers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
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
object WarehouseFactory
{
	val modelLoader = G3dModelLoader(UBJsonReader())
	val modelData = modelLoader.loadModelData(Gdx.files.internal("data/warehouse/a.g3db"))

	//______________________________________________________________________________________________
	fun create(pos: Vector3, angle: Float = 0f): Entity {
		val entity = Entity()

		pos.y += 50f
		pos.x += 50f

		/// MODEL
		val model = Model(modelData)
		val modelComponent = ModelComponent(model, pos)
		//modelComponent.instance.transform.translate(pos)
		modelComponent.instance.transform.rotate(Vector3.Y, angle)
		entity.add(modelComponent)

		/// COLLISION
		val shape = createShape()

		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(0f, null, shape, Vector3.Zero)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = MotionState(modelComponent.instance.transform)
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

	//______________________________________________________________________________________________
	private fun createShape(): btCompoundShape
	{
		val shape = btCompoundShape()
		shape.addChildShape(Matrix4().setTranslation(-90f,	-10f, -5f), btBoxShape(Vector3(5f,40f,85f)))//LEFT_WALL
		shape.addChildShape(Matrix4().setTranslation(-10f,	15f, -5f), btBoxShape(Vector3(5f,14f,85f)))//RIGHT WALL
		shape.addChildShape(Matrix4().setTranslation(-10f,	-40f, -5f), btBoxShape(Vector3(5f,7f,85f)))//RIGHT WALL
		shape.addChildShape(Matrix4().setTranslation(-10f,	-10f, 65f), btBoxShape(Vector3(5f,40f,15f)))//RIGHT WALL
		shape.addChildShape(Matrix4().setTranslation(-10f,	-10f, -80f), btBoxShape(Vector3(5f,40f,15f)))//RIGHT WALL
		shape.addChildShape(Matrix4().setTranslation(-80f,	-10f, 77f), btBoxShape(Vector3(14f,40f,5f)))//LEFT_FRONT_COLUMN
		shape.addChildShape(Matrix4().setTranslation(-20f,	-10f, 77f), btBoxShape(Vector3(14f,40f,5f)))//RIGHT_FRONT_COLUMN
		shape.addChildShape(Matrix4().setTranslation(-50f,	+12f, +70f), btBoxShape(Vector3(50f,10f,5f)))//FRONT
		shape.addChildShape(Matrix4().setTranslation(-80f,	-10f, -85f), btBoxShape(Vector3(14f,40f,5f)))//LEFT_BACK_COLUMN
		shape.addChildShape(Matrix4().setTranslation(-20f,	-10f, -85f), btBoxShape(Vector3(14f,40f,5f)))//RIGHT_BACK_COLUMN
		shape.addChildShape(Matrix4().setTranslation(-50f,	+12f, -85f), btBoxShape(Vector3(50f,10f,5f)))//BACK
		shape.addChildShape(Matrix4().setTranslation(-50f,	28f, 0f), btBoxShape(Vector3(60f,5f,100f)))//ROOF
		return shape
	}
}