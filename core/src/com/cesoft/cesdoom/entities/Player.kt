package com.cesoft.cesdoom.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.components.BulletComponent


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class Player : Entity() {

//TODO: refactor as entity is not supposed to have functions... entities are just bags of components
    fun getPosition() : Vector3 {
        val transform = Matrix4()
        BulletComponent.get(this).rigidBody.motionState.getWorldTransform(transform)
        val posTemp = Vector3()
        return transform.getTranslation(posTemp)
    }
}