package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity

class AmmoComponent : Component {

    companion object {
        private val mapper: ComponentMapper<AmmoComponent> = ComponentMapper.getFor(AmmoComponent::class.java)
        fun get(entity: Entity):AmmoComponent = mapper.get(entity)

        const val SIZE = 5f
        const val MAGAZINE_CAPACITY = 30
    }

    //val cuantity: Int = 0//Tendria que saber como pasar valores con los eventos...
    var isPickedUp = false
    var angle = 0f
    //private lateinit var pos: Vector3
    //lateinit var rigidBody: btRigidBody
    //private lateinit var rigidBodyInfo: btRigidBody.btRigidBodyConstructionInfo
    //private lateinit var model: ModelComponent

}