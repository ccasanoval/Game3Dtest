package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class BulletComponent(var rigidBody: btRigidBody, val rigidBodyInfo: btRigidBody.btRigidBodyConstructionInfo) : Component
{
	// Si no usas rigidBodyInfo, se destruye por gc y puede dar lugar a problemas en las colisiones¿?
	// Bullet: Disposing btRigidBodyConstructionInfo(452913408,true) due to garbage collection.
	companion object {
		const val GROUND_FLAG = 1 shl 3
		const val SCENE_FLAG = 1 shl 4
		const val PLAYER_FLAG = 1 shl 5
		const val ENEMY_FLAG = 1 shl 6
		const val SHOT_FLAG = 1 shl 7
	}
}
