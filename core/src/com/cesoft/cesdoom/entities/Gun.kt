package com.cesoft.cesdoom.entities

import com.badlogic.ashley.core.Entity
import com.cesoft.cesdoom.ui.GunFireWidget

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class Gun : Entity() {

	lateinit var fire: GunFireWidget

	fun dispose() {
		fire.remove()
		removeAll()
	}

    fun init(fire: GunFireWidget) {
		this.fire = fire
    }
}