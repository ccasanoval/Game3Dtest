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
import com.badlogic.gdx.physics.bullet.collision.Collision
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.bullet.MotionState
import com.cesoft.cesdoom.components.BulletComponent
import com.cesoft.cesdoom.components.ModelComponent
import com.cesoft.cesdoom.components.PlayerComponent
import com.cesoft.cesdoom.components.SwitchComponent
import com.cesoft.cesdoom.entities.Switch


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object SwitchFactory {

    private val dimCollision = Vector3(SwitchComponent.SIZE, SwitchComponent.SIZE, SwitchComponent.SIZE)

    private val mb = ModelBuilder()
    private const val POSITION_NORMAL =
            (VertexAttributes.Usage.Position
                    or VertexAttributes.Usage.Normal
                    or VertexAttributes.Usage.TextureCoordinates).toLong()

    //______________________________________________________________________________________________
    fun create(engine: Engine, pos: Vector3, angle: Float, id: String, assets: Assets): Switch {

        /// Entity
        val entity = Switch(id, assets)
        pos.y += PlayerComponent.TALL + SwitchComponent.SIZE +2

        /// Component
        entity.add(SwitchComponent)

        /// Material
        val textureOff = assets.getSwitchOff()
        val materialOff = Material(ColorAttribute.createDiffuse(Color.WHITE))
        textureOff.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        val textureAttributeOff = TextureAttribute(TextureAttribute.Diffuse, textureOff)
        textureAttributeOff.scaleU = 1f
        textureAttributeOff.scaleV = 1f
        materialOff.set(textureAttributeOff)

        /// Model
        val model : Model = mb.createBox(SwitchComponent.SIZE, SwitchComponent.SIZE, SwitchComponent.SIZE, materialOff, POSITION_NORMAL)
        val modelComponent = ModelComponent(model, pos)

        modelComponent.instance.materials.get(0).set(textureAttributeOff)
        //modelComponent.instance.materials.get(1).set(textureAttributeOn)
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
        rigidBody.contactCallbackFlag = BulletComponent.SWITCH_FLAG
        rigidBody.userValue = BulletComponent.SWITCH_FLAG
        rigidBody.activationState = Collision.DISABLE_DEACTIVATION
        rigidBody.friction = 1f
        rigidBody.rollingFriction = 1f
        rigidBody.spinningFriction = 1f
        entity.add(BulletComponent(rigidBody, bodyInfo))

        entity.init(angle, pos, rigidBody, bodyInfo)
        engine.addEntity(entity)
        return entity
    }
}