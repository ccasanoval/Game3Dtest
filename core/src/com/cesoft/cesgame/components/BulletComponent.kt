package com.cesoft.cesgame.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class BulletComponent(var rigidBody : btRigidBody, val rigidBodyInfo: btRigidBody.btRigidBodyConstructionInfo) : Component
{
	companion object {
		const val ARENA_FLAG = 1 shl 4
		const val PLAYER_FLAG = 1 shl 5
		const val ENEMY_FLAG = 1 shl 6
		const val SHOT_FLAG = 1 shl 7
	}
}
