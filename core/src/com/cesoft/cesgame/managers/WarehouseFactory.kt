package com.cesoft.cesgame.managers

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.Collision
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesgame.bullet.MotionState
import com.cesoft.cesgame.components.BulletComponent
import com.cesoft.cesgame.components.ModelComponent
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute
import com.badlogic.gdx.physics.bullet.Bullet
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader
import com.badlogic.gdx.utils.UBJsonReader


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object WarehouseFactory
{
	private val modelLoader = G3dModelLoader(UBJsonReader())
	private val modelData = modelLoader.loadModelData(Gdx.files.internal("scene/warehouse/a.g3db"))

	//______________________________________________________________________________________________
	fun create(pos: Vector3, angle: Float = 0f): Entity {
		val entity = Entity()

		pos.y += 50f
		pos.x += 50f

		/// MODEL
		val model = Model(modelData)

		/*val POSITION_NORMAL =
				(VertexAttributes.Usage.Position
				or VertexAttributes.Usage.Normal
				or VertexAttributes.Usage.TextureCoordinates).toLong()
		val mb = ModelBuilder()
		val material = Material(ColorAttribute.createDiffuse(Color.DARK_GRAY))
		val texture = Texture(Gdx.files.internal("data/ground.jpg"))
		texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
		val textureAttribute1 = TextureAttribute(TextureAttribute.Diffuse, texture)
		textureAttribute1.scaleU = 80f
		textureAttribute1.scaleV = 80f
		material.set(textureAttribute1)

		//-------------------------
		val dimensions = arrayListOf<Vector3>()
		/*dimensions.add(Vector3(5f,	40f,	85f).add(-90f,	-10f, -5f))
		dimensions.add(Vector3(5f,	14f,	85f).add(-10f,	15f, -5f))
		dimensions.add(Vector3(5f,	7f,		85f).add(-10f,	-40f, -5f))
		dimensions.add(Vector3(5f,	40f,	15f).add(-10f,	-10f, 65f))
		dimensions.add(Vector3(5f,	40f,	15f).add(-10f,	-10f, -80f))
		dimensions.add(Vector3(14f,	40f,	5f).add(-80f,	-10f, 77f))
		dimensions.add(Vector3(14f,	40f,	5f).add(-20f,	-10f, 77f))
		dimensions.add(Vector3(50f,	10f,	5f).add(-50f,	+12f, +70f))
		dimensions.add(Vector3(14f,	40f,	5f).add(-80f,	-10f, -85f))
		dimensions.add(Vector3(14f,	40f,	5f).add(-20f,	-10f, -85f))
		dimensions.add(Vector3(50f,	10f,	5f).add(-50f,	+12f, -85f))
		dimensions.add(Vector3(60f,	5f,		100f).add(-50f,	28f, 	0f))*/
		dimensions.add(Vector3(100f,100f,100f))

		var i = 0
		val meshBuilder = MeshBuilder()
		val meshes = arrayListOf<Mesh>()
		for(dim in dimensions)
		{
			meshBuilder.begin((VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong())// or VertexAttributes.Usage.TextureCoordinates).toLong())
			meshBuilder.part((++i).toString(), GL20.GL_LINES)
			meshBuilder.box(dim.x, dim.y, dim.z)
			meshes.add(meshBuilder.end())
		}

		i=0
		val modelBuilder = ModelBuilder()
		modelBuilder.begin()
		for(mesh in meshes)
		{
			modelBuilder.part(
					(++i).toString(),
					mesh,
					VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal,// or VertexAttributes.Usage.TextureCoordinates,
					material)
					//Material())
		}
		val model = modelBuilder.end()

		/*mb.begin()
		for(dim in dimensions)
			mb.createBox(dim.x, dim.y, dim.z, material, POSITION_NORMAL)
		val model : Model = mb.end()*/
		//-------------------------

		/*meshBuilder.begin(POSITION_NORMAL, GL20.GL_TRIANGLES)
		meshBuilder.cylinder(4f, 6f, 4f, 16)
		val cylinder1 = meshBuilder.end()
		meshBuilder.begin(POSITION_NORMAL, GL20.GL_TRIANGLES)
		meshBuilder.cylinder(4f, 6f, 4f, 16)
		val cylinder2 = meshBuilder.end()

		val modelBuilder2 = ModelBuilder()
		modelBuilder2.begin()
		modelBuilder2.part("cylinder1",
				cylinder1,
				VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal,
				Material(
						TextureAttribute.createDiffuse(texture),
						ColorAttribute.createSpecular(1f, 1f, 1f, 1f),
						FloatAttribute.createShininess(8f)))
		modelBuilder2.part("cylinder2",
				cylinder2,
				VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal,
				Material(
						TextureAttribute.createDiffuse(texture),
						ColorAttribute.createSpecular(1f, 1f, 1f, 1f),
						FloatAttribute.createShininess(8f)))
			.mesh.transform(Matrix4().translate(2f, 2f, -10f))

		val model = modelBuilder2.end()*/*/

		//----

		val modelComponent = ModelComponent(model, pos)
		//modelComponent.instance.transform.translate(pos)
		modelComponent.instance.transform.rotate(Vector3.Y, angle)
		entity.add(modelComponent)

		/// COLLISION
		val shape = createShape()
		//val shape = Bullet.obtainStaticNodeShape(model.nodes)
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