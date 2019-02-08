package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity

class WidgetComponent : Component {
    companion object {
        private val mapper: ComponentMapper<WidgetComponent> = ComponentMapper.getFor(WidgetComponent::class.java)
        fun get(entity: Entity):WidgetComponent = mapper.get(entity)

//        var score: Int = 0
//        var ammo: Int = 0
//        var health: Int = 0
//        var message: String = ""
    }
}