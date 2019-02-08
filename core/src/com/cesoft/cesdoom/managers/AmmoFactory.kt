package com.cesoft.cesdoom.managers

import com.badlogic.ashley.core.Engine
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
import com.cesoft.cesdoom.entities.Ammo

// DECAL & BILLBOARD :
// https://github.com/libgdx/libgdx/blob/master/tests/gdx-tests/src/com/badlogic/gdx/tests/SimpleDecalTest.java
// https://github.com/libgdx/libgdx/wiki/Decals
////////////////////////////////////////////////////////////////////////////////////////////////////
//
object AmmoFactory {

    private val dimCollision = Vector3(AmmoComponent.SIZE, AmmoComponent.SIZE, AmmoComponent.SIZE)

    //______________________________________________________________________________________________
    fun create(pos: Vector3, model: Model, engine: Engine): Ammo {

        /// Entity
        val entity = Ammo()
        pos.y = PlayerComponent.TALL + AmmoComponent.SIZE

        /// Component
        entity.add(AmmoComponent())

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
        rigidBody.contactCallbackFlag = BulletComponent.AMMO_FLAG
        rigidBody.userValue = BulletComponent.AMMO_FLAG
        rigidBody.activationState = Collision.DISABLE_DEACTIVATION
        entity.add(BulletComponent(rigidBody, bodyInfo))

        //TODO entity.init(modelComponent, pos, rigidBody, bodyInfo)
        engine.addEntity(entity)
        return entity
    }

}