package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.Collision
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.bullet.MotionState
import com.cesoft.cesdoom.components.BulletComponent
import com.cesoft.cesdoom.components.ModelComponent
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.cesoft.cesdoom.renderUtils.FrustumCullingData
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.map.MapGraphFactory
import kotlin.math.absoluteValue
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class RampFactory(assets: Assets) {
	companion object {
		private val tag: String = RampFactory::class.java.simpleName

		const val LONG = 32f
		const val HIGH = 20f//TODO: Cambiar para hacer cuadrado... mas facil construir...
		const val THICK = 1f

		const val LONG_GROUND = 25f
		private const val THICK_GROUND = 2f

		private val dim = Vector3(THICK+5, HIGH, LONG)//Aumenta espesor colision para que player no tenga rayos X !!
		private val dimFCX90Y45Z00 = Vector3(2*.707f*LONG, 2*.707f*LONG, HIGH*2)	///Izquierda y Derecha
		private val dimFCX45Y00Z90 = Vector3(HIGH*2, 2*.707f*LONG, 2*.707f*LONG)	///Adelante y Atras

		private val dimGround = Vector3(LONG_GROUND, THICK_GROUND, LONG_GROUND)
		private val dimFCGround = Vector3(2*LONG_GROUND, 2*THICK_GROUND, 2*LONG_GROUND)
		//
//		private val dimX00Y90Z90 = Vector3(2*LONG, 2*THICK, 2*HIGH)
//		private val dimX00Y00Z00 = Vector3(THICK*2, HIGH*2, LONG*2)
//		private val dimX00Y90Z00 = Vector3(LONG*2, HIGH*2, THICK*2)
		//private val dimX00Y00Z90 = Vector3(HIGH*2, THICK*2, LONG*2)
		//private val dimX90Y00Z00 = Vector3(LONG*2, HIGH*2, THICK*2)
		//private val dimMax = Vector3(LONG*2, LONG*2, LONG*2)

	}
	enum class Type { STEEL, GRILLE, }

	private val material1 = Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY))
	private val material2 = Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY))

	private val modelBuilder = ModelBuilder()
	private val POSITION_NORMAL =
			(VertexAttributes.Usage.Position
					or VertexAttributes.Usage.Normal
					or VertexAttributes.Usage.TextureCoordinates).toLong()

	//______________________________________________________________________________________________
	init {
		val texture1 = assets.getWallMetal2()
		val texture2 = assets.getWallMetal3()

		/// MODEL 1 (CORRUGATED STEEL)
		texture1.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute1 = TextureAttribute(TextureAttribute.Diffuse, texture1)
		textureAttribute1.scaleU = 3f
		textureAttribute1.scaleV = 3f * LONG / HIGH
		material1.set(textureAttribute1)

		/// MODEL 2 (TRANSPARENT GRILLE)
		texture2.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute2 = TextureAttribute(TextureAttribute.Diffuse, texture2)
		textureAttribute2.scaleU = 2f
		textureAttribute2.scaleV = 2f * LONG / HIGH
		material2.set(textureAttribute2)
		material2.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))
	}

	//______________________________________________________________________________________________
	fun create(mapFactory: MapGraphFactory, engine: Engine, pos: Vector3,
			   angleX: Float=0f, angleY: Float=0f, angleZ: Float=0f,
			   type:Type=Type.STEEL): Entity {

		val entity = Entity()
		//pos.y -= 2f

		/// MAP
		RampMapFactory.createMap(mapFactory, pos, angleX, angleY, angleZ)

		/// MODEL
		val material = if(type==Type.STEEL) material1 else material2
		val model : Model = modelBuilder.createBox(THICK*2, HIGH*2, LONG*2, material, POSITION_NORMAL)
		val modelComponent = ModelComponent(model, pos)
		entity.add(modelComponent)

		/// Frustum Culling (Should not depend on it material is solid or not?)
		if(angleX == 90f && angleY.absoluteValue == 45f && angleZ == 0f)
			modelComponent.frustumCullingData = FrustumCullingData.create(pos, dimFCX90Y45Z00)
		else if(angleX.absoluteValue == 45f && angleY == 0f && angleZ == 90f)
			modelComponent.frustumCullingData = FrustumCullingData.create(pos, dimFCX45Y00Z90)
		else
			Log.e(tag, "create:--------------------------angleX=$angleX / angleY=$angleY / angleZ=$angleZ")
//		if(angleX == 0f && angleY == 0f && angleZ == 0f)
//			modelComponent.frustumCullingData = FrustumCullingData.create(pos, dimX00Y00Z00)
//		else if(angleX == 0f && angleY == 90f && angleZ == 0f)
//			modelComponent.frustumCullingData = FrustumCullingData.create(pos, dimX00Y90Z00)
//		else if(angleX == 0f && angleY == 90f && angleZ == 90f)
//			modelComponent.frustumCullingData = FrustumCullingData.create(pos, dimX00Y90Z90)

		// ROTATION
		modelComponent.instance.transform.rotate(Vector3.X, angleX)
		modelComponent.instance.transform.rotate(Vector3.Y, angleY)
		modelComponent.instance.transform.rotate(Vector3.Z, angleZ)

		/// COLLISION
		val shape = btBoxShape(dim)
		val motionState = MotionState(modelComponent.instance.transform)
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(0f, motionState, shape, Vector3.Zero)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = motionState
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT
		rigidBody.contactCallbackFilter = 0
		rigidBody.userValue = if(type==Type.STEEL) BulletComponent.STEEL_RAMP_FLAG else 0	// La regilla no para las balas
		rigidBody.activationState = Collision.DISABLE_DEACTIVATION
		rigidBody.friction = 1f
		rigidBody.rollingFriction = 1f
		rigidBody.spinningFriction = 1f
		entity.add(BulletComponent(rigidBody, bodyInfo))

		engine.addEntity(entity)
		return entity
	}

	//______________________________________________________________________________________________
	fun createGround(mapFactory: MapGraphFactory, engine: Engine, pos: Vector3,
			   type:Type=Type.STEEL): Entity {
		val entity = Entity()

		/// MAP
		//RampMapFactory.createGroundMap(mapFactory, pos)

		/// MODEL
		val material = if(type==Type.STEEL) material1 else material2
		val model : Model = modelBuilder.createBox(dimFCGround.x, dimFCGround.y, dimFCGround.z, material, POSITION_NORMAL)
		val modelComponent = ModelComponent(model, pos)
		entity.add(modelComponent)

		/// Frustum Culling (Should not depend on it material is solid or not?)
		modelComponent.frustumCullingData = FrustumCullingData.create(pos, dimFCGround)

		/// COLLISION
		val shape = btBoxShape(dimGround)
		val motionState = MotionState(modelComponent.instance.transform)
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(0f, motionState, shape, Vector3.Zero)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = motionState
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT
		rigidBody.contactCallbackFilter = 0
		rigidBody.userValue = if(type==Type.STEEL) BulletComponent.STEEL_RAMP_FLAG else 0	// La regilla no para las balas
		rigidBody.activationState = Collision.DISABLE_DEACTIVATION
		rigidBody.friction = 1f
		rigidBody.rollingFriction = 1f
		rigidBody.spinningFriction = 1f
		entity.add(BulletComponent(rigidBody, bodyInfo))

		engine.addEntity(entity)
		return entity
	}

}