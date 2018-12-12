package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class EnemyComponent(val type: TYPE) : Component
{
	var isShowingParticles = false

	enum class TYPE {
		MONSTER1,
	}
	enum class ACTION
	{
		IDLE,
		DYING,
		ACHING,
		ATTACKING,
		WALKING,
		RUNNING,
		REINCARNATING,
	}
}
