package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.components.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.cesoft.cesdoom.renderUtils.FrustumCullingData
import com.badlogic.gdx.math.Vector2


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object SceneFactory {

	//______________________________________________________________________________________________
	fun addJunkAntenna(engine: Engine, texture: Texture, len: Float) {
		val ratio = 546f/1000f
		val long = 500f
		val high = long * ratio
		DecalFactory.addDecal(texture, engine, Vector2(long, high), Vector3(-len, high/2, 0f), 0f, +90f)
		DecalFactory.addDecal(texture, engine, Vector2(long, high), Vector3(+len, high/2, 0f), 0f, -90f)
		DecalFactory.addDecal(texture, engine, Vector2(long, high), Vector3(0f, high/2, -len), 0f, 0f)
		DecalFactory.addDecal(texture, engine, Vector2(long, high), Vector3(0f, high/2, +len), 0f, 180f)
	}
	//______________________________________________________________________________________________
	//
	fun addJunkWall(engine: Engine, texture: Texture, len: Float) {
		val ratio = 838f/1764f
		val long = 120f
		val high = long * ratio
		val size = Vector2(long, high)
		DecalFactory.addDecal(texture, engine, size, Vector3(+60f, high/2, -len), 0f, 0f, Color.LIGHT_GRAY)
		DecalFactory.addDecal(texture, engine, size, Vector3(-30f, high/2, +len), 0f, 180f, Color.LIGHT_GRAY)
		//
		DecalFactory.addDecal(texture, engine, size, Vector3(-len, high/2, -100f), 0f, 90f, Color.GRAY)
		DecalFactory.addDecal(texture, engine, size, Vector3(-len, high/2, +250f), 0f, 90f, Color.GRAY)
		DecalFactory.addDecal(texture, engine, size, Vector3(+len, high/2, +150f), 0f, -90f, Color.LIGHT_GRAY)
		DecalFactory.addDecal(texture, engine, size, Vector3(+len, high/2, -350f), 0f, -90f, Color.LIGHT_GRAY)
	}
	//______________________________________________________________________________________________
	//
	fun addJunkBuilding(engine: Engine, texture: Texture, len: Float) {
		val ratio = 668f/1419f
		val long = 600f
		val high = long * ratio
		val size = Vector2(long, high)
		DecalFactory.addDecal(texture, engine, size, Vector3(+len, high/2, -len), 0f, -45f, Color.LIGHT_GRAY)
		DecalFactory.addDecal(texture, engine, size, Vector3(-len, high/2, +len), 0f, 135f, Color.LIGHT_GRAY)
		DecalFactory.addDecal(texture, engine, size, Vector3(+len, high/2, +len), 0f, 225f, Color.GRAY)
		DecalFactory.addDecal(texture, engine, size, Vector3(-len, high/2, -len), 0f, +45f, Color.GRAY)
		//
		DecalFactory.addDecal(texture, engine, Vector2(long, high), Vector3(0f, high/2, -len-200), 0f, 15f, Color.LIGHT_GRAY)
	}

	//______________________________________________________________________________________________
	fun addSkyline(engine: Engine, texture: Texture, len: Float) {
		val ratioSkyline = 380f/1400f
		val long = 2*len
		val high = 2*len*ratioSkyline
		val pto = len*.6f
		DecalFactory.addDecal(texture, engine, Vector2(long, high), Vector3(-pto, high/2, -pto), 0f, +45f, Color.GRAY)
		DecalFactory.addDecal(texture, engine, Vector2(long, high), Vector3(-pto, high/2, +pto), 0f, +135f, Color.GRAY)
		DecalFactory.addDecal(texture, engine, Vector2(long, high), Vector3(+pto, high/2, -pto), 0f, -45f, Color.GRAY)
		DecalFactory.addDecal(texture, engine, Vector2(long, high), Vector3(+pto, high/2, +pto), 0f, -135f, Color.GRAY)
	}

	//______________________________________________________________________________________________
	fun addGround(engine: Engine, texture: Texture, len: Float) {
		val entity = Entity()

		/// MATERIAL
		texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute1 = TextureAttribute(TextureAttribute.Diffuse, texture)
		textureAttribute1.scaleU = 80f
		textureAttribute1.scaleV = 80f
		val material = Material(ColorAttribute.createDiffuse(Color.WHITE))
		material.set(textureAttribute1)
		/// MODEL
		val size = Vector2(len, len)
		val pos = Vector3(0f, 0f, 0f)
		val modelComponent = DecalFactory.createDecal(material, size, pos, -90f, 0f)
		modelComponent.frustumCullingData = FrustumCullingData.create(Vector3.Zero, Vector3(len, 5f, len))
		entity.add(modelComponent)

		/// COLLISION
		val shape = btBoxShape(Vector3(len, 5f, len))//Bullet.obtainStaticNodeShape(modelo.nodes)
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(0f, null, shape, Vector3.Zero)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		//rigidBody.motionState = MotionState(modelComponent.instance.transform)
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT
		rigidBody.contactCallbackFilter = 0
		rigidBody.contactCallbackFlag = BulletComponent.GROUND_FLAG
		rigidBody.userValue = BulletComponent.GROUND_FLAG
		rigidBody.activationState = Collision.DISABLE_DEACTIVATION

		entity.add(BulletComponent(rigidBody, bodyInfo))
		engine.addEntity(entity)
	}

	//______________________________________________________________________________________________
	fun addDome(engine: Engine, model: Model) {
		val modelComponent = ModelComponent(model, Vector3(0f,0f,0f))
		modelComponent.instance.transform.scale(100f,100f,100f)
		engine.addEntity(Entity().add(modelComponent))
	}

}