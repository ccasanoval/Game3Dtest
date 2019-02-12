package com.cesoft.cesdoom.entities

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.Collision
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.bullet.MotionState
import com.cesoft.cesdoom.components.AmmoComponent
import com.cesoft.cesdoom.components.BulletComponent
import com.cesoft.cesdoom.components.ModelComponent
import com.cesoft.cesdoom.components.PlayerComponent


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class Ammo(pos: Vector3, model: Model, engine: Engine) : Entity() {

    companion object {
        private val dimCollision = Vector3(AmmoComponent.SIZE, AmmoComponent.SIZE, AmmoComponent.SIZE)
    }

    private val modelComponent: ModelComponent
    init {
        pos.y += PlayerComponent.TALL + AmmoComponent.SIZE/2f

        /// Component
        add(AmmoComponent())

        /// Model
        modelComponent = ModelComponent(model, pos)
        add(modelComponent)

        /// Position and Shape
        val transf = modelComponent.instance.transform
        val shape = btBoxShape(dimCollision)
        val motionState = MotionState(transf)

        /// Collision
        val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(0f, motionState, shape, Vector3.Zero)
        val rigidBody = btRigidBody(bodyInfo)
        rigidBody.userData = this
        rigidBody.motionState = motionState
        rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE
        rigidBody.contactCallbackFilter = 0
        rigidBody.contactCallbackFlag = BulletComponent.AMMO_FLAG
        rigidBody.userValue = BulletComponent.AMMO_FLAG
        rigidBody.activationState = Collision.DISABLE_DEACTIVATION
        add(BulletComponent(rigidBody, bodyInfo))

        engine.addEntity(this)
    }

    private var isPickedUp = false
    fun pickup() {
        isPickedUp = true
    }

    fun update(engine: Engine) {
        if(isPickedUp) {
            engine.removeEntity(this)
        }
        else {
            //val model = ModelComponent.get(this)
            modelComponent.instance.transform.rotate(Vector3.Y, 5f)
        }
    }
}