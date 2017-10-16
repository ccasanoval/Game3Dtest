package com.cesoft.cesgame.components

import com.badlogic.ashley.core.Component

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GunComponent(val type: TYPE) : Component
{
	enum class TYPE {
		CZ805,
		AK47,
	}
	enum class ACTION
	{
		IDLE,
		SHOOT,
		RELOAD,
		DRAW
	}
}
