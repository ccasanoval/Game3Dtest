package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GunComponent(val type: TYPE) : Component {

	enum class TYPE {
		CZ805,
	}
	enum class ACTION {
		IDLE,
		SHOOT,
		RELOAD,
		DRAW
	}
}
