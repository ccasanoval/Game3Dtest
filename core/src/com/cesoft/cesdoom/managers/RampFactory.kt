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
import com.cesoft.cesdoom.util.Log
import kotlin.math.absoluteValue


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class RampFactory(assets: Assets) {
	companion object {
		const val LONG = 30f
		const val HIGH = 20f
		const val THICK = 2f
	}
	enum class Type { STEEL, GRID, }

	private val dimX00Y90Z90 = Vector3(LONG*2, THICK*2, HIGH*2)
	private val dimX90Y45Z00 = Vector3(0.707f*LONG*2, 0.707f*LONG*2, HIGH*2)
	private val dimX00Y00Z00 = Vector3(THICK*2, HIGH*2, LONG*2)
	private val dimX00Y90Z00 = Vector3(LONG*2, HIGH*2, THICK*2)
	//private val dimX00Y00Z90 = Vector3(HIGH*2, THICK*2, LONG*2)
	//private val dimX90Y00Z00 = Vector3(LONG*2, HIGH*2, THICK*2)
	private val dimMax = Vector3(LONG*2, LONG*2, LONG*2)
	private val dim = Vector3(THICK, HIGH, LONG)

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

		/// MODEL 2 (TRANSPARENT GRID)
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

		/// MAP
		createMap(mapFactory, pos, angleX, angleY, angleZ)

		/// MODEL
		val material = if(type==Type.STEEL) material1 else material2
		val model : Model = modelBuilder.createBox(THICK*2, HIGH*2, LONG*2, material, POSITION_NORMAL)
		val modelComponent = ModelComponent(model, pos)
		entity.add(modelComponent)

		/// Frustum Culling (Should not depend on it material is solid or not?)
		if(angleX == 0f && angleY == 0f && angleZ == 0f)
			modelComponent.frustumCullingData = FrustumCullingData.create(pos, dimX00Y00Z00)
		else if(angleX == 0f && angleY == 90f && angleZ == 0f)
			modelComponent.frustumCullingData = FrustumCullingData.create(pos, dimX00Y90Z00)
		else if(angleX == 0f && angleY == 90f && angleZ == 90f)
			modelComponent.frustumCullingData = FrustumCullingData.create(pos, dimX00Y90Z90)
		else if(angleX == 90f && angleY.absoluteValue == 45f && angleZ == 0f)
			modelComponent.frustumCullingData = FrustumCullingData.create(pos, dimX90Y45Z00)
		else {
			//por que no funciona  con esferica ? Quiza eliminar esferica ?
			/*val boundingBox = BoundingBox()
			modelComponent.instance.calculateBoundingBox(boundingBox)
			frustrumCullingData = FrustumCullingData.create(boundingBox)*/
			modelComponent.frustumCullingData = FrustumCullingData.create(pos, dimMax)
		}

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
		rigidBody.userValue = if(type==Type.STEEL) BulletComponent.SCENE_FLAG else 0	// La regilla no para las balas
		rigidBody.activationState = Collision.DISABLE_DEACTIVATION
		rigidBody.friction = 1f
		rigidBody.rollingFriction = 1f
		rigidBody.spinningFriction = 1f
		entity.add(BulletComponent(rigidBody, bodyInfo))

		engine.addEntity(entity)
		return entity
	}

	//______________________________________________________________________________________________
	private fun createMap(mapFactory: MapGraphFactory, pos: Vector3,
						  angleX: Float=0f, angleY: Float=0f, angleZ: Float=0f) {
		val level = if(pos.y > 2*WallFactory.HIGH) 1 else 0//TODO: Y si hago mas plantas? Refactor in MazeFactory...
		//Log.e("RampFactory", "createMap----------------------level=$level------ angleX=$angleX && angleY=$angleY && angleZ=$angleZ")

		// Sube hacia la derecha  (+X)
		if(angleX == 90f && angleY == -45f && angleZ == 0f) {
			mapFactory.addLevelAccess(level, pos.x+LONG, pos.z)
			for(x_ in -LONG.toInt()..+LONG.toInt()+1) {
				for(z_ in 0..mapFactory.scale) {
					mapFactory.addCollider(level, pos.x + x_, pos.z + HIGH+z_)
					mapFactory.addCollider(level, pos.x + x_, pos.z - HIGH-z_)
				}
			}
			for(z_ in -HIGH.toInt()..+HIGH.toInt()) {
				for(x_ in 0..mapFactory.scale) {
					mapFactory.addCollider(level, pos.x + LONG+x_, pos.z + z_)
				}
			}
		}
		// Sube hacia la izquierda (-X)
		else if(angleX == 90f && angleY == +45f && angleZ == 0f) {
			mapFactory.addLevelAccess(level, pos.x-LONG, pos.z)
			for(x_ in -LONG.toInt()..+LONG.toInt()) {
				for(z_ in 0..mapFactory.scale) {
					mapFactory.addCollider(level, pos.x + x_, pos.z + HIGH+z_)
					mapFactory.addCollider(level, pos.x + x_, pos.z - HIGH-z_)
				}
			}
			for(z_ in -HIGH.toInt()..+HIGH.toInt()) {
				for(x_ in 0..mapFactory.scale) {
					mapFactory.addCollider(level, pos.x - LONG-x_, pos.z + z_)
				}
			}
		}
		// Sube hacia la adelante (-Z)
		else if(angleX == +45f && angleY == 0f && angleZ == 90f) {
			mapFactory.addLevelAccess(level, pos.x, pos.z-LONG)
			for(z_ in -LONG.toInt()..+LONG.toInt()) {
				for(x_ in 0..mapFactory.scale) {
					mapFactory.addCollider(level, pos.x+HIGH+x_, pos.z + z_)
					mapFactory.addCollider(level, pos.x-HIGH-x_,  pos.z+z_)
				}
			}
			for(x_ in -HIGH.toInt()..+HIGH.toInt()) {
				for(z_ in 0..mapFactory.scale) {
					mapFactory.addCollider(level, pos.x + x_, pos.z - LONG-z_)
				}
			}
		}
		// Sube hacia la atras (+Z)
		else if(angleX == -45f && angleY == 0f && angleZ == 90f) {
			mapFactory.addLevelAccess(level, pos.x, pos.z+LONG)
			for(z_ in -LONG.toInt()..+LONG.toInt()) {
				for(x_ in 0..mapFactory.scale) {
					mapFactory.addCollider(level, pos.x + HIGH+x_, pos.z + z_)
					mapFactory.addCollider(level, pos.x - HIGH-x_, pos.z + z_)
				}
			}
			for(x_ in -HIGH.toInt()..+HIGH.toInt()) {
				for(z_ in 0..mapFactory.scale) {
					mapFactory.addCollider(level, pos.x + x_, pos.z + LONG+z_)
				}
			}
		}
		//TODO: else, otros angulos...
	}
}