package com.cesoft.cesdoom.entities

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.cesoft.cesdoom.UI.GunFireWidget
import com.cesoft.cesdoom.components.GunComponent
import com.cesoft.cesdoom.util.Log

class Gun : Entity() {

	lateinit var fire: GunFireWidget

	fun dispose_() {
		Log.e("Gun", "dispose:------------------------------------------------------")
		fire.remove()
		fire.dispose()
		removeAll()
	}
	fun reset() {
		Log.e("Gun", "reset:------------------------------------------------------")
		//removeAll()
	}

    fun init(fire: GunFireWidget) {
        //this.position.set(position)
        //this.type = type
		this.fire = fire
    }
}