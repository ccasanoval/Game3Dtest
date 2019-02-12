package com.cesoft.cesdoom.entities

import com.badlogic.ashley.core.Entity
import com.cesoft.cesdoom.ui.GunFireWidget
import com.cesoft.cesdoom.util.Log

class Gun : Entity() {

	lateinit var fire: GunFireWidget

	fun dispose() {
		fire.remove()
		fire.dispose()
		removeAll()
	}

    fun init(fire: GunFireWidget) {
		this.fire = fire
    }
}