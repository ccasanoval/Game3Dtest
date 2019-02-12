package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity

class HealthComponent: Component {

    companion object {
        private val mapper: ComponentMapper<HealthComponent> = ComponentMapper.getFor(HealthComponent::class.java)
        fun get(entity: Entity): HealthComponent = mapper.get(entity)

        const val SIZE = 5f
        const val DRUG_CAPACITY = 25
    }

    //var isPickedUp = false
}