package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.Collision
import com.badlogic.gdx.physics.bullet.collision.btBoxShape
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.bullet.MotionState
import com.cesoft.cesdoom.components.BulletComponent
import com.cesoft.cesdoom.components.HealthComponent
import com.cesoft.cesdoom.components.ModelComponent
import com.cesoft.cesdoom.components.PlayerComponent
import com.cesoft.cesdoom.entities.Health


object HealthFactory {

    private val dimCollision = Vector3(HealthComponent.SIZE, HealthComponent.SIZE, HealthComponent.SIZE)

    //______________________________________________________________________________________________
    fun create(pos: Vector3, model: Model, engine: Engine): Health {

        /// Entity
        val entity = Health()
        pos.y = PlayerComponent.TALL + HealthComponent.SIZE/2f

        /// Component
        entity.add(HealthComponent())

        /// Model
        val modelComponent = ModelComponent(model, pos)
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
        rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE
        rigidBody.contactCallbackFilter = 0
        rigidBody.contactCallbackFlag = BulletComponent.HEALTH_FLAG
        rigidBody.userValue = BulletComponent.HEALTH_FLAG
        rigidBody.activationState = Collision.DISABLE_DEACTIVATION
        entity.add(BulletComponent(rigidBody, bodyInfo))

        //entity.init(modelComponent, pos, rigidBody, bodyInfo)
        engine.addEntity(entity)
        return entity
    }

}