package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component

object HealthComponent : Component {
    const val SIZE = 5f

    var reloading: Boolean = false
    /*var health: Int = 0
        private set
    fun reset(cuantity: Int) {
        health = cuantity
    }
    fun add(cuantity: Int) {
        if(cuantity in 1..500)
            health += cuantity
    }*/
}