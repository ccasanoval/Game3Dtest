package com.cesoft.cesdoom.entities

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.Collision
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject
import com.badlogic.gdx.physics.bullet.collision.btSphereShape
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.cesoft.cesdoom.bullet.MotionState
import com.cesoft.cesdoom.components.BulletComponent
import com.cesoft.cesdoom.components.PlayerComponent
import com.cesoft.cesdoom.util.Log

class Player : Entity() {

    companion object {
        fun create(pos: Vector3, colorAmbiente: ColorAttribute, engine: Engine): Player {
            /// Entity
            val entity = Player()
            /// Component
            PlayerComponent.ini(colorAmbiente)
            entity.add(PlayerComponent)
            /// Position and Shape
            val posTemp = Vector3()
            val shape = btSphereShape(PlayerComponent.RADIO)//btCylinderShape(Vector3(3f,ALTURA/2,3f))//btCapsuleShape(6f, ALTURA)//
            shape.calculateLocalInertia(PlayerComponent.MASA, posTemp)
            /// Collision
            val bodyInfo = btRigidBody.btRigidBodyConstructionInfo(PlayerComponent.MASA, null, shape, posTemp)
            val rigidBody = btRigidBody(bodyInfo)
            rigidBody.userData = entity
            rigidBody.motionState = MotionState(Matrix4().translate(pos))
            // The onContactAdded callback will only be triggered if at least one of the two colliding bodies has the CF_CUSTOM_MATERIAL_CALLBACK
            rigidBody.collisionFlags = rigidBody.collisionFlags or btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK
            rigidBody.contactCallbackFilter = BulletComponent.SWITCH_FLAG or BulletComponent.GATE_FLAG or BulletComponent.YOU_WIN_FLAG
                                            //BulletComponent.GROUND_FLAG or BulletComponent.ENEMY_FLAG or BulletComponent.SCENE_FLAG
            rigidBody.contactCallbackFlag = BulletComponent.PLAYER_FLAG
            rigidBody.userValue = BulletComponent.PLAYER_FLAG
            rigidBody.activationState = Collision.DISABLE_DEACTIVATION
            rigidBody.friction = 0f
            rigidBody.rollingFriction = 1000000000f
            entity.add(BulletComponent(rigidBody, bodyInfo))
            //
            engine.addEntity(entity)
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

    fun youWin() {
        val player = getComponent(PlayerComponent::class.java)
        player.winning()
    }
}