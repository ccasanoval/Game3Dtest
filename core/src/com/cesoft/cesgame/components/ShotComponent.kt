package com.cesoft.cesgame.components

import com.badlogic.ashley.core.Component

////////////////////////////////////////////////////////////////////////////////////////////////////
// Created by ccasanova on 04/10/2017.
class ShotComponent : Component {

	var aliveTime: Float = 0f
		private set

	fun update(delta: Float) {
		aliveTime += delta
	}

	companion object {
		const val MASA = 1f
		const val FUERZA = 5000f
	}
}
