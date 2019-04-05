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
import com.badlogic.gdx.math.Vector2
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

		const val LONG = 35f
		const val HIGH = 20f
		const val THICK = 1f

		const val LONG_GROUND = 25f
		private const val THICK_GROUND = 2f

		private val dim = Vector3(THICK+5, HIGH, LONG)//Aumenta espesor colision para que player no tenga rayos X !!
		private val dimFCX90Y45Z00 = Vector3(2*.707f*LONG, 2*.707f*LONG, HIGH*2)	///Izquierda y Derecha
		private val dimFCX45Y00Z90 = Vector3(HIGH*2, 2*.707f*LONG, 2*.707f*LONG)	///Adelante y Atras

		private val dimGround = Vector3(LONG_GROUND, THICK_GROUND, LONG_GROUND)
		private val dimFCGround = Vector3(2*LONG_GROUND, 2*THICK_GROUND, 2*LONG_GROUND)

		private const val POSITION_NORMAL =
				(VertexAttributes.Usage.Position
						or VertexAttributes.Usage.Normal
						or VertexAttributes.Usage.TextureCoordinates).toLong()
	}
	enum class Type { STEEL, GRILLE, }

	private val materialSteel = Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY))
	private val materialGrille = Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY))

	private val modelBuilder = ModelBuilder()


	//______________________________________________________________________________________________
	init {
		val textureSteel = assets.getWallSteel()
		val textureGrille = assets.getWallGrille()

		/// MODEL 1 (CORRUGATED STEEL)
		textureSteel.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttributeSteel = TextureAttribute(TextureAttribute.Diffuse, textureSteel)
		textureAttributeSteel.scaleU = 3f
		textureAttributeSteel.scaleV = 3f * LONG / HIGH
		materialSteel.set(textureAttributeSteel)

		/// MODEL 2 (TRANSPARENT GRILLE)
		textureGrille.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttributeGrille = TextureAttribute(TextureAttribute.Diffuse, textureGrille)
		textureAttributeGrille.scaleU = 2f
		textureAttributeGrille.scaleV = 2f * LONG / HIGH
		materialGrille.set(textureAttributeGrille)
		materialGrille.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))
	}

	//______________________________________________________________________________________________
	fun create(mapFactory: MapGraphFactory, engine: Engine, pos: Vector3,
			   angleX: Float=0f, angleY: Float=0f, angleZ: Float=0f,
			   type:Type=Type.STEEL): Entity {

		val entity = Entity()
		//pos.y -= 2f

		/// MAP
		RampMapFactory.create(mapFactory, pos, angleX, angleY, angleZ)

		/// MODEL
		val material = if(type==Type.STEEL) materialSteel else materialGrille
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
			   type:Type=Type.STEEL, xWay: Boolean=false, zWay: Boolean=false) {

		val entity = Entity()

		/// MAP
		val floor = (pos.y / (2*WallFactory.HIGH)).toInt()
		val l = RampFactory.LONG_GROUND.toInt()
		if(xWay) {
			for(x in -l..+l) {
				if(Math.abs(x) < RampFactory.LONG_GROUND/MazeFactory.scale) {
					continue
				}
				val point1 = mapFactory.toMapGraphCoord(floor, Vector2(pos.x+x, pos.z+l))
				val point2 = mapFactory.toMapGraphCoord(floor, Vector2(pos.x+x, pos.z-l))
				mapFactory.addCollider(floor, point1)
				mapFactory.addCollider(floor, point2)
			}
		}

		/// MODEL
		val material = if(type==Type.STEEL) materialSteel else materialGrille
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
	}



	//______________________________________________________________________________________________
	fun createGround2(mapFactory: MapGraphFactory, engine: Engine, assets: Assets,
					  size: Vector2, pos: Vector3, angle: Float=-90f,
					  type:Type=Type.STEEL, double: Boolean=false) {

		if(double) {
			createGround2(mapFactory, engine, assets, size, pos.cpy(), angle + 180f, type)
		}
		else {
			/// PATH FINDING MAP
			val floor = (pos.y / (2*WallFactory.HIGH)).toInt()
			for(x in 0..size.x.toInt()) {
				for(y in 0..size.y.toInt()) {
					val point = mapFactory.toMapGraphCoord(floor, Vector2(pos.x + x, pos.y + y))
					mapFactory.addWay(floor, point)
				}
			}
		}

		val entity = Entity()

		/// MATERIAL
		val texture = when(type) {
			Type.GRILLE -> assets.getWallGrille()
			else -> assets.getWallSteel()
		}
		texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute = TextureAttribute(TextureAttribute.Diffuse, texture)
		textureAttribute.scaleU = size.x/WallFactory.LONG
		textureAttribute.scaleV = textureAttribute.scaleU * size.y/size.x
		val material = Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY))
		material.set(textureAttribute)
		if(type == Type.GRILLE)
			material.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))

		/// MODEL
		val modelComponent = DecalFactory.createDecal(material, size, pos, angle, 0f)
		entity.add(modelComponent)

		/// FRUSTUM CULLING
		modelComponent.frustumCullingData = FrustumCullingData.create(pos, Vector3(size.x, 5f, size.y))

		/// COLLISION
		val shape = btBoxShape(Vector3(size.x/2, size.y/2, 4f))
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
	}


}