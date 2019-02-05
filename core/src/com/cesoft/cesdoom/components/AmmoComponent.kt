package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component

object AmmoComponent : Component {
    const val SIZE = 5f
    const val CARTRIDGE = 22f

    var reloading: Boolean = false
    var ammo: Int = 0
        private set
    fun reset(cuantity: Int) {
        ammo = cuantity
    }
    fun add(cuantity: Int) {
        if(cuantity in 1..500)
            ammo += cuantity
    }
    fun fire() {
        if( ! PlayerComponent.isGodModeOn && ammo > 0)
            ammo--
    }
}