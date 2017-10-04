package com.cesoft.cesgame.components

import com.badlogic.ashley.core.Component

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class PlayerComponent : Component
{
	var isSaltando = false

	companion object {
		var health: Float = 100f //TODO: pasar a la clase
		var score: Int = 0 //TODO: pasar a la clase?
		const val MASA = 1f
	}
}
