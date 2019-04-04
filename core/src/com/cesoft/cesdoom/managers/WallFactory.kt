package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
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
		val textureConcrete = assets.getWallConcrete()
		val textureSteel = assets.getWallSteel()
		val textureGrille = assets.getWallGrille()
        val textureCircuits = assets.getWallCircuits()

		textureConcrete.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttributeConcrtete = TextureAttribute(TextureAttribute.Diffuse, textureConcrete)
		textureAttributeConcrtete.scaleU = 2f
		textureAttributeConcrtete.scaleV = textureAttributeConcrtete.scaleU * HIGH/LONG
		materialConcrete.set(textureAttributeConcrtete)

		textureSteel.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute2 = TextureAttribute(TextureAttribute.Diffuse, textureSteel)
		textureAttribute2.scaleU = 3f
		textureAttribute2.scaleV = textureAttribute2.scaleU * HIGH/LONG
		materialSteel.set(textureAttribute2)

		textureGrille.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute3 = TextureAttribute(TextureAttribute.Diffuse, textureGrille)
		textureAttribute3.scaleU = 3f
		textureAttribute3.scaleV = textureAttribute3.scaleU * HIGH/LONG
		materialGrille.set(textureAttribute3)
		materialGrille.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))

        textureCircuits.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        val textureAttribute4 = TextureAttribute(TextureAttribute.Diffuse, textureCircuits)
        textureAttribute4.scaleU = 1f
        textureAttribute4.scaleV = textureAttribute4.scaleU * HIGH/LONG
        materialCircuits.set(textureAttribute4)
	}


	//______________________________________________________________________________________________
	//TODO : no cerrar modelBuilder.end() y crear todos los objetos antes para que sea una sola pieza mas eficiente...
	/*fun create(mapFactory: MapGraphFactory, engine: Engine, pos: Vector3, angle: Float, type: Type = Type.CONCRETE): Entity {

		/// GraphMap
		WallMapFactory.create(mapFactory, pos, angle)

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
		val modelBuilder = ModelBuilder()
		modelBuilder.begin()
		val mpb = modelBuilder.part(modelBuilder.hashCode().toString(), GL20.GL_TRIANGLES, POSITION_NORMAL, material)
		BoxShapeBuilder.build(mpb, THICK*2, HIGH*2, LONG*2)
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
		val shape = btBoxShape(dimCollision)
		val motionState = MotionState(modelComponent.instance.transform)
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
	}*/

	//______________________________________________________________________________________________
	fun createWall(mapFactory: MapGraphFactory, engine: Engine, assets: Assets,
				   size: Vector3, pos: Vector3, angle: Float=0f,
				   type: Type = Type.CONCRETE) {

		/// GraphMap
		WallMapFactory.createLongWall(mapFactory, size, pos, angle)

		/// Entity
		val entity = Entity()
		pos.y += HIGH

		/// MATERIAL
		val length = if(size.x > size.z) size.x else size.z
		val texture = when(type) {
			Type.CONCRETE -> assets.getWallConcrete()
			Type.STEEL -> assets.getWallSteel()
			Type.GRILLE -> assets.getWallGrille()
			Type.CIRCUITS -> assets.getWallCircuits()
		}
		val scale =  when(type) {
			Type.CONCRETE -> .5f
			Type.STEEL -> 1f
			Type.GRILLE -> 1f
			Type.CIRCUITS -> .5f
		}
		texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute = TextureAttribute(TextureAttribute.Diffuse, texture)
		textureAttribute.scaleU = scale*size.y/HIGH
		textureAttribute.scaleV = textureAttribute.scaleU*scale*length/LONG
		val material = Material(ColorAttribute.createDiffuse(Color.WHITE))
		material.set(textureAttribute)

		/// MODEL
		val modelBuilder = ModelBuilder()
		modelBuilder.begin()
		val mpb = modelBuilder.part(modelBuilder.hashCode().toString(), GL20.GL_TRIANGLES, POSITION_NORMAL, material)
		BoxShapeBuilder.build(mpb, size.x, size.y, size.z)
		val model = modelBuilder.end()
		val modelComponent = ModelComponent(model, pos)
		modelComponent.instance.transform.rotate(Vector3.Y, -angle)
		entity.add(modelComponent)

		// FRUSTUM CULLING
		//Los muros en 45 grados fallan un pelin en el frustum culling
		val size2 = size.cpy()
		if(Math.abs(angle) == 45f)size2.add(4f)
		modelComponent.frustumCullingData = FrustumCullingData.create(pos, size2)

		/// COLLISION
		val dim = Vector3(size.x/2, size.y/2, size.z/2)
		val shape = btBoxShape(dim)
		val motionState = MotionState(modelComponent.instance.transform)
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
	}

	//______________________________________________________________________________________________
	fun createGrille(mapFactory: MapGraphFactory, engine: Engine, assets: Assets,
					 size: Vector2, pos: Vector3, angle: Float, double: Boolean=false) {

		if(double) {
			createGrille(mapFactory, engine, assets, size, pos.cpy(), angle+180f, false)
		}

		pos.y += size.y/2

		/// GraphMap
		val size2 = Vector3(size.x, size.y, 2*WallFactory.THICK)
		WallMapFactory.createLongWall(mapFactory, size2, pos, angle)

		/// MATERIAL
		val texture = assets.getWallGrille()
		texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute = TextureAttribute(TextureAttribute.Diffuse, texture)
		textureAttribute.scaleU = size.x/LONG
		textureAttribute.scaleV = textureAttribute.scaleU * size.y/size.x
		val material = Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY))
		material.set(textureAttribute)
		material.set(BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA))

		/// Entity
		val entity = Entity()

		/// MODEL
		// Por simplicidad el DECAL es siempre long=X, hight=Y, thick=Z / angulo=Y / looking=+Z
		val modelComponent = DecalFactory.createDecal(material, size, pos, 0f, angle)
		entity.add(modelComponent)

		/// COLLISION
		val dim = Vector3(size.x/2, size.y/2, WallFactory.THICK)
		val shape = btBoxShape(dim)
		val motionState = MotionState(modelComponent.instance.transform)
		//val motionState = MotionState(Matrix4(pos, Quaternion(), Vector3(1f,1f,1f)))
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
	}
}