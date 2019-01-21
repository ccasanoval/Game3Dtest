package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.physics.bullet.collision.Collision
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.bullet.MotionState
import com.cesoft.cesdoom.components.BulletComponent
import com.cesoft.cesdoom.RenderUtils.FrustumCullingData
import com.cesoft.cesdoom.components.ModelComponent
import com.cesoft.cesdoom.map.MapGraphFactory
import com.cesoft.cesdoom.systems.RenderSystem
import com.cesoft.cesdoom.util.Log
import kotlin.math.sign


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object WallFactory {
	const val LONG = 25f//TODO:Change texture length!
	const val HIGH = 25f
	const val THICK = 4f

	// dimension en X grados para frustum culling
	private val dim0 = Vector3(THICK*2, HIGH*2, LONG*2)
	private val dim90= Vector3(LONG*2, HIGH*2, THICK*2)
	private val dim45= Vector3(LONG*2, HIGH*2, LONG*2)

	private val dimCollision = Vector3(THICK+0f,HIGH+0f,LONG+0f)

	private val mb = ModelBuilder()
	private val POSITION_NORMAL =
			(VertexAttributes.Usage.Position
			or VertexAttributes.Usage.Normal
			or VertexAttributes.Usage.TextureCoordinates).toLong()

	var texture: Texture? = null

	//______________________________________________________________________________________________
	fun create(mapFactory: MapGraphFactory, pos: Vector3, angle: Float = 0f): Entity {


		//angle > -5f && angle < 5f)

		/// COLISION
		//val shape = Bullet.obtainStaticNodeShape(model.nodes)
		//modelComponent.instance.transform)
		//BulletComponent.GROUND_FLAG or BulletComponent.PLAYER_FLAG
		//rigidBody.anisotropicFriction = Vector3(1f,1f,1f)
		val thick = WallFactory.THICK.toInt() * 7	// Debe ser mayor para que no haga colision con enemigo, que no es un punto sino un objeto 3D / o cambiar scale
		val long = WallFactory.LONG.toInt()   * 4
		Log.e("WallFactory", "---------------- ${WallFactory.THICK}   ${WallFactory.LONG}")
		when(angle) {//TODO: change by sin + cos of angle...
			+00f -> //--- Vertical
				for(x_ in -thick/2..thick/2)
					for(z_ in -long/2..long/2)
						mapFactory.addCollider(pos.x + x_, pos.z + z_)
			+90f -> //--- Horizontal
				for(z_ in -thick/2..thick/2)
					for(x_ in -long/2..long/2)
						mapFactory.addCollider(pos.x + x_, pos.z + z_)
			+45f ->
				for(z_ in 0..thick)
					for(x_ in z_..z_+(long*0.7971f).toInt())
						mapFactory.addCollider(pos.x + z_, pos.z + z_)
			-45f ->
				for(z_ in 0..thick)
					for(x_ in z_..z_+(long*0.7971f).toInt())
						mapFactory.addCollider(pos.x + x_, pos.z + x_)
		}

		val entity = Entity()
		pos.y += HIGH


		// Mejora para frustum culling

		/// MODELO
		val material = Material(ColorAttribute.createDiffuse(Color.WHITE))
		texture?.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute1 = TextureAttribute(TextureAttribute.Diffuse, texture)
		textureAttribute1.scaleU = 2f
		textureAttribute1.scaleV = 2f//4
		material.set(textureAttribute1)

		val modelo : Model = mb.createBox(THICK*2, HIGH*2, LONG*2, material, POSITION_NORMAL)
		val modelComponent = ModelComponent(modelo, pos)

		// Mejora para frustum culling
		val frustumCullingData : FrustumCullingData
		if(angle == 0f)//angle > -5f && angle < 5f)
			frustumCullingData = FrustumCullingData.create(pos, dim0)
		else if(angle == 90f)
			frustumCullingData = FrustumCullingData.create(pos, dim90)
		else if(angle == 45f || angle == -45f)
			frustumCullingData = FrustumCullingData.create(pos, dim45)
		else {
			val boundingBox = BoundingBox()
			modelComponent.instance.calculateBoundingBox(boundingBox)
			frustumCullingData = FrustumCullingData.create(boundingBox)
		}
		modelComponent.frustumCullingData = frustumCullingData

		modelComponent.instance.materials.get(0).set(textureAttribute1)
		modelComponent.instance.transform.rotate(Vector3.Y, angle)
		entity.add(modelComponent)

		/// COLISION
		val transf = modelComponent.instance.transform
		val shape = btBoxShape(dimCollision)
		//val shape = Bullet.obtainStaticNodeShape(model.nodes)
		val motionState = MotionState(transf)
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(0f, motionState, shape, Vector3.Zero)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = motionState//modelComponent.instance.transform)
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT
		rigidBody.contactCallbackFilter = 0//BulletComponent.GROUND_FLAG or BulletComponent.PLAYER_FLAG
		rigidBody.contactCallbackFlag = BulletComponent.SCENE_FLAG or RenderSystem.CF_OCCLUDER_OBJECT
		rigidBody.userValue = BulletComponent.SCENE_FLAG
		rigidBody.activationState = Collision.DISABLE_DEACTIVATION
		rigidBody.friction = 1f
		rigidBody.rollingFriction = 1f
		//rigidBody.anisotropicFriction = Vector3(1f,1f,1f)
		rigidBody.spinningFriction = 1f
		entity.add(BulletComponent(rigidBody, bodyInfo))

		return entity
	}
}