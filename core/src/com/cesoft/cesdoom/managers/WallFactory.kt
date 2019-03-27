package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import com.badlogic.gdx.physics.bullet.collision.Collision
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.bullet.MotionState
import com.cesoft.cesdoom.components.BulletComponent
import com.cesoft.cesdoom.renderUtils.FrustumCullingData
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.components.ModelComponent
import com.cesoft.cesdoom.map.MapGraphFactory


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object WallFactory {
	const val LONG = 25f
	const val HIGH = 25f
	const val THICK = 4f

	enum class Type { CONCRETE, STEEL, GRILLE, CIRCUITS }

	// dimension en X grados para frustum culling
	private val dim0 = Vector3(THICK*2, HIGH*2, LONG*2)
	private val dim90= Vector3(LONG*2, HIGH*2, THICK*2)
	private val dim45= Vector3(LONG*2, HIGH*2, LONG*2)

	private val dimCollision = Vector3(THICK+0f,HIGH+0f,LONG+0f)

	private val mb = ModelBuilder()
	private const val POSITION_NORMAL =
			(VertexAttributes.Usage.Position
			or VertexAttributes.Usage.Normal
			or VertexAttributes.Usage.TextureCoordinates).toLong()

	private val materialConcrete = Material(ColorAttribute.createDiffuse(Color.WHITE))
	private val materialSteel = Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY))
	private val materialGrille = Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY))
    private val materialCircuits = Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY))

	fun iniMaterials(assets: Assets) {
		val texture1 = assets.getWallConcrete()
		val texture2 = assets.getWallSteel()
		val texture3 = assets.getWallGrille()
        val texture4 = assets.getWallCircuits()

		texture1.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute1 = TextureAttribute(TextureAttribute.Diffuse, texture1)
		textureAttribute1.scaleU = 2f
		textureAttribute1.scaleV = textureAttribute1.scaleU * LONG / HIGH
		materialConcrete.set(textureAttribute1)

		texture2.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute2 = TextureAttribute(TextureAttribute.Diffuse, texture2)
		textureAttribute2.scaleU = 3f
		textureAttribute2.scaleV = textureAttribute2.scaleU * LONG / HIGH
		materialSteel.set(textureAttribute2)

		texture3.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute3 = TextureAttribute(TextureAttribute.Diffuse, texture3)
		textureAttribute3.scaleU = 3f
		textureAttribute3.scaleV = textureAttribute3.scaleU * LONG / HIGH
		materialGrille.set(textureAttribute3)
		materialGrille.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))

        texture4.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        val textureAttribute4 = TextureAttribute(TextureAttribute.Diffuse, texture4)
        textureAttribute4.scaleU = 1f
        textureAttribute4.scaleV = textureAttribute4.scaleU * LONG / HIGH
        materialCircuits.set(textureAttribute4)
	}


	//______________________________________________________________________________________________
	fun create(mapFactory: MapGraphFactory, engine: Engine, pos: Vector3, angle: Float, type: Type = Type.CONCRETE): Entity {

		/// GraphMap
		WallMapFactory.create(mapFactory, pos, angle, 0)

		/// Entity
		val entity = Entity()
		pos.y += HIGH

		/// MATERIAL
		val material = when(type) {
			Type.CONCRETE -> materialConcrete
			Type.STEEL -> materialSteel
			Type.GRILLE -> materialGrille
            Type.CIRCUITS -> materialCircuits
		}

		/// MODEL
		//val model : Model = mb.createBox(THICK*2, HIGH*2, LONG*2, material, POSITION_NORMAL)
		val modelBuilder = ModelBuilder()
		modelBuilder.begin()
		val mpb = modelBuilder.part(modelBuilder.hashCode().toString(), GL20.GL_TRIANGLES, POSITION_NORMAL, material)
		mpb.box(THICK*2, HIGH*2, LONG*2)
		val model = modelBuilder.end()
		val modelComponent = ModelComponent(model, pos)

		// Mejora para frustum culling
		val frustumCullingData =
			if(angle == 0f)
				FrustumCullingData.create(pos, dim0)
			else if(angle == 90f)
				FrustumCullingData.create(pos, dim90)
			else if(angle == 45f || angle == -45f)
				FrustumCullingData.create(pos, dim45)
			else {
				val boundingBox = BoundingBox()
				modelComponent.instance.calculateBoundingBox(boundingBox)
				FrustumCullingData.create(boundingBox)
			}
		modelComponent.frustumCullingData = frustumCullingData

		modelComponent.instance.transform.rotate(Vector3.Y, angle)
		entity.add(modelComponent)

		/// COLLISION
		val transf = modelComponent.instance.transform
		val shape = btBoxShape(dimCollision)
		val motionState = MotionState(transf)
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(0f, motionState, shape, Vector3.Zero)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = motionState
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT
		rigidBody.contactCallbackFilter = 0
		rigidBody.userValue = BulletComponent.SCENE_FLAG
		rigidBody.activationState = Collision.DISABLE_DEACTIVATION
		rigidBody.friction = 1f
		rigidBody.rollingFriction = 1f
		rigidBody.spinningFriction = 1f
		entity.add(BulletComponent(rigidBody, bodyInfo))

		engine.addEntity(entity)
		return entity
	}

	//______________________________________________________________________________________________
	//TODO
	fun createGrille(mapFactory: MapGraphFactory, engine: Engine, size: Vector2, pos: Vector3, angle: Float): Entity {

		/// GraphMap
		//WallMapFactory.createGrille(mapFactory, pos, angle, 0)

		/// Entity
		val entity = Entity()

		/// MODEL
		val material = materialGrille.copy()
//		val textureAttribute = TextureAttribute(TextureAttribute.Diffuse, texture)
//		textureAttribute.scaleU = 3f
//		textureAttribute.scaleV = textureAttribute.scaleU * LONG / HIGH
//		materialGrille.set(textureAttribute)
		val modelComponent = DecalFactory.createDecal(material, size, pos, 0f, angle)
		entity.add(modelComponent)

		/// COLLISION
		val transf = modelComponent.instance.transform
		val shape = btBoxShape(dimCollision)
		val motionState = MotionState(transf)
		val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(0f, motionState, shape, Vector3.Zero)
		val rigidBody = btRigidBody(bodyInfo)
		rigidBody.userData = entity
		rigidBody.motionState = motionState
		rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT
		rigidBody.contactCallbackFilter = 0
		rigidBody.userValue = BulletComponent.SCENE_FLAG
		rigidBody.activationState = Collision.DISABLE_DEACTIVATION
		rigidBody.friction = 1f
		rigidBody.rollingFriction = 1f
		rigidBody.spinningFriction = 1f
		entity.add(BulletComponent(rigidBody, bodyInfo))

		engine.addEntity(entity)
		return entity
	}
}