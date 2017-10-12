package com.cesoft.cesgame.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.utils.Disposable

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class EnemyComponent(val type: TYPE) : Component
{
	enum class TYPE {
		MONSTER1,
	}
	enum class ACTION
	{
		IDLE,
		DYING,
		ATTACKING,
		WALKING,
		RUNNING,
		REINCARNATING,
	}
}
