package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.components.ModelComponent
import com.cesoft.cesdoom.renderUtils.FrustumCullingData
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.bullet.collision.Collision
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.bullet.MotionState
import com.cesoft.cesdoom.components.BulletComponent
import com.cesoft.cesdoom.map.MapGraphFactory
import com.cesoft.cesdoom.map.Point


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object ColumnFactory {

    //______________________________________________________________________________________________
    fun add(engine: Engine, mapFactory: MapGraphFactory, assets: Assets, size: Vector3, position: Vector3) {

        position.y += WallFactory.HIGH

        val texture = assets.getWallCircuits()
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        val material = Material(ColorAttribute.createDiffuse(Color.WHITE))
        val textureAttribute = TextureAttribute(TextureAttribute.Diffuse, texture)
        textureAttribute.scaleU = 1f//size.z / 15f
        textureAttribute.scaleV = 1f//size.y / WallFactory.HIGH //(size.z/size.y) * textureAttribute.scaleU
        material.set(textureAttribute)

        /// MODEL
        val entity = Entity()
        val modelComponent = create(material, size, position)
        entity.add(modelComponent)

        /// COLLISION
        val shape = btBoxShape(Vector3(size.x/2, size.y/2, size.z/2))
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

        /// PATHFINDER MAP
        val level = when {
                    position.y > 4*WallFactory.HIGH-1 -> 2
                    position.y > 2*WallFactory.HIGH-1 -> 1
                    else -> 0
                }
        val posMap = mapFactory.toMapGraphCoord(level, Vector2(position.x, position.z))
        for(x in -1..1)
            for(y in -1..1)
                mapFactory.addCollider(level, Point(posMap.x+x, posMap.y+y))


        // Entity to engine
        engine.addEntity(entity)

        System.gc()
    }

    //______________________________________________________________________________________________
    private fun create(material: Material, size: Vector3, position: Vector3) : ModelComponent {
        val modelBuilder = ModelBuilder()
        modelBuilder.begin()
        val attributes = VertexAttributes.Usage.Position.toLong() or VertexAttributes.Usage.Normal.toLong() or VertexAttributes.Usage.TextureCoordinates.toLong()
        val mpb = modelBuilder.part(modelBuilder.hashCode().toString(), GL20.GL_TRIANGLES, attributes, material)
        BoxShapeBuilder.build(mpb, size.x, size.y, size.z)
        val model = modelBuilder.end()
        val modelComponent = ModelComponent(model, position)
        modelComponent.frustumCullingData = FrustumCullingData.create(position, size)
        return modelComponent
    }
}
