package com.cesoft.cesgame.components

import com.badlogic.ashley.core.Component

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class EnemyComponent(var state: STATE) : Component {

	enum class STATE {
		IDLE,
		FLEEING,
		HUNTING
	}

	companion object {
		const val MASA = 4f
	}
}
