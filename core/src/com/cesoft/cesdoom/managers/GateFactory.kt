package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
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
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.RenderUtils.FrustumCullingData
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.bullet.MotionState
import com.cesoft.cesdoom.components.BulletComponent
import com.cesoft.cesdoom.components.GateComponent
import com.cesoft.cesdoom.components.ModelComponent
import com.cesoft.cesdoom.entities.Gate
import com.cesoft.cesdoom.map.MapGraphFactory


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object GateFactory {
    private const val LONG = GateComponent.LONG
    private const val HIGH = GateComponent.HIGH
    private const val THICK = GateComponent.THICK

    // dimension en X grados para frustum culling
    private val dim0 = Vector3(THICK*2, HIGH*2, LONG*5)//5*LONG porque la puerta se mueve
    private val dim90= Vector3(LONG*5, HIGH*2, THICK*2)
    //private val dim45= Vector3(LONG*2, HIGH*2, LONG*2)

    private val dimCollision = Vector3(THICK+5f,HIGH+0f,LONG+0f)

    private val mb = ModelBuilder()
    private const val POSITION_NORMAL =
            (VertexAttributes.Usage.Position
                    or VertexAttributes.Usage.Normal
                    or VertexAttributes.Usage.TextureCoordinates).toLong()

    /*private lateinit var texture: Texture
    fun ini(texture: Texture) {
        this.texture = texture
    }*/


    //______________________________________________________________________________________________
    fun create(mapFactory: MapGraphFactory, engine: Engine, pos: Vector3, angle: Float, id: String): Gate {

        /// GraphMap
        //WallMapFactory.create(mapFactory, pos, angle, 0)

        /// Entity
        val entity = Gate(id)
        pos.y += HIGH

        /// Component
        val enemyComponent = GateComponent
        entity.add(enemyComponent)

        /// Material
        val material = Material(ColorAttribute.createDiffuse(Color.WHITE))
        val texture = CesDoom.instance.assets.getGate()
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        val textureAttribute1 = TextureAttribute(TextureAttribute.Diffuse, texture)
        textureAttribute1.scaleU = 1f
        textureAttribute1.scaleV = 1f
        material.set(textureAttribute1)

        /// Model
        val model : Model = mb.createBox(THICK*2, HIGH*2, LONG*2, material, POSITION_NORMAL)
        val modelComponent = ModelComponent(model, pos)

        // Frustum culling
        val frustumCullingData : FrustumCullingData
        if(angle == 0f)//angle > -5f && angle < 5f)
            frustumCullingData = FrustumCullingData.create(pos, dim0)
        else if(angle == 90f)
            frustumCullingData = FrustumCullingData.create(pos, dim90)
        //else if(angle == 45f || angle == -45f)
        //    frustumCullingData = FrustumCullingData.create(pos, dim45)
        else {
            val boundingBox = BoundingBox()
            modelComponent.instance.calculateBoundingBox(boundingBox)
            frustumCullingData = FrustumCullingData.create(boundingBox)
        }
        modelComponent.frustumCullingData = frustumCullingData

        modelComponent.instance.materials.get(0).set(textureAttribute1)
        modelComponent.instance.transform.rotate(Vector3.Y, angle)
        entity.add(modelComponent)

        /// Position and Shape
        val transf = modelComponent.instance.transform
        val shape = btBoxShape(dimCollision)
        val motionState = MotionState(transf)

        /// Collision
        val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(0f, motionState, shape, Vector3.Zero)
        val rigidBody = btRigidBody(bodyInfo)
        rigidBody.userData = entity
        rigidBody.motionState = motionState
        rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT
        rigidBody.contactCallbackFilter = 0
        rigidBody.contactCallbackFlag = BulletComponent.GATE_FLAG
        rigidBody.userValue = BulletComponent.GATE_FLAG
        rigidBody.activationState = Collision.DISABLE_DEACTIVATION
        rigidBody.friction = 1f
        rigidBody.rollingFriction = 1f
        rigidBody.spinningFriction = 1f
        entity.add(BulletComponent(rigidBody, bodyInfo))

        entity.init(modelComponent, angle, pos, rigidBody, bodyInfo)
        engine.addEntity(entity)
        return entity
    }
}