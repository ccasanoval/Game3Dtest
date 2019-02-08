package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class BulletComponent(
		val rigidBody: btRigidBody,
		val rigidBodyInfo: btRigidBody.btRigidBodyConstructionInfo)
	: Component
{
	// Si no usas rigidBodyInfo, se destruye por gc y puede dar lugar a problemas en las colisiones¿?
	// Bullet: Disposing btRigidBodyConstructionInfo(452913408,true) due to garbage collection.
	companion object {
		private val mapper: ComponentMapper<BulletComponent> = ComponentMapper.getFor(BulletComponent::class.java)
		fun get(entity: Entity):BulletComponent = mapper.get(entity)

		const val SCENE_FLAG = 1 shl 0		// 1
		const val PLAYER_FLAG = 1 shl 1		// 2
		const val ENEMY_FLAG = 1 shl 2		// 4
		const val GATE_FLAG = 1 shl 3		// 8
		const val YOU_WIN_FLAG = 1 shl 4	// 16
		const val SWITCH_FLAG = 1 shl 5		// 32
		const val AMMO_FLAG = 1 shl 6		// 64
		const val HEALTH_FLAG = 1 shl 7		// 128
		const val GROUND_FLAG = 1 shl 8		// 256

		private const val INDEX_MASK = 0x7FFF0000
		private const val FLAG_MASK = 0x0000FFFF
		fun calcCode(flag: Int, index: Int) = (flag and FLAG_MASK) or (index shl 16)
		fun getFlag(code : Int) = code and FLAG_MASK
		fun getIndex(code : Int) = (code and INDEX_MASK) ushr 16
	}
}
