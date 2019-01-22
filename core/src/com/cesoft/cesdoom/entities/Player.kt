package com.cesoft.cesdoom.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.Collision
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.collision.btSphereShape
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.bullet.MotionState
import com.cesoft.cesdoom.components.BulletComponent
import com.cesoft.cesdoom.components.PlayerComponent

class Player : Entity() {

    companion object {
        fun create(pos: Vector3): Player {
            val entity = Player()
            val posTemp = Vector3()
            val shape = btSphereShape(PlayerComponent.RADIO)//btCylinderShape(Vector3(3f,ALTURA/2,3f))//btCapsuleShape(6f, ALTURA)//
            shape.calculateLocalInertia(PlayerComponent.MASA, posTemp)
            val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(PlayerComponent.MASA, null, shape, posTemp)
            val rigidBody = btRigidBody(bodyInfo)
            rigidBody.userData = entity
            rigidBody.motionState = MotionState(Matrix4().translate(pos))
            rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK
            rigidBody.contactCallbackFilter = BulletComponent.ENEMY_FLAG or BulletComponent.SCENE_FLAG or BulletComponent.GROUND_FLAG or BulletComponent.GATE_FLAG
            rigidBody.contactCallbackFlag = BulletComponent.PLAYER_FLAG
            rigidBody.userValue = BulletComponent.PLAYER_FLAG
            rigidBody.activationState = Collision.DISABLE_DEACTIVATION
            rigidBody.friction = 0f
            rigidBody.rollingFriction = 1000000000f
            entity.add(BulletComponent(rigidBody, bodyInfo))
            entity.add(PlayerComponent)
            return entity
        }
    }

    fun getPosition() : Vector3 {
        val bulletPlayer = getComponent(BulletComponent::class.java)
        val transf = Matrix4()
        bulletPlayer.rigidBody.getWorldTransform(transf)
        val posPlayer = Vector3()
        transf.getTranslation(posPlayer)
        return posPlayer.cpy()
    }
}