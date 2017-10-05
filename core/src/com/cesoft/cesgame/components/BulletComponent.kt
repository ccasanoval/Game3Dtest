package com.cesoft.cesgame.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class BulletComponent(var rigidBody : btRigidBody) : Component
{
	companion object {
		const val ARENA_FLAG = 1	//(1 shl 7)
		const val PLAYER_FLAG = 2
		const val ENEMY_FLAG = 3
		const val SHOT_FLAG = 4
	}
}
