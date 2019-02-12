package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GunComponent(val type: TYPE) : Component {

	companion object {
		private val mapper: ComponentMapper<GunComponent> = ComponentMapper.getFor(GunComponent::class.java)
		fun get(entity: Entity): GunComponent = mapper.get(entity)
	}

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
