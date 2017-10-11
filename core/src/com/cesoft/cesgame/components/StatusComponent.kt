package com.cesoft.cesgame.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.cesoft.cesgame.managers.EnemyAnimations
import com.cesoft.cesgame.managers.EnemyFactory

////////////////////////////////////////////////////////////////////////////////////////////////////
class StatusComponent(private val entity: Entity) : Component//private val animat: AnimationComponent, private val enemy: EnemyComponent) : Component
{
	var alive: Boolean = true
		set(value) {field = value; playDeathAnim()}
	var running: Boolean = true
	var attacking: Boolean = false
	var aliveStateTime: Float = 0f

	fun update(delta: Float) {
		if( ! alive) aliveStateTime += delta
	}

	private fun playDeathAnim() {
		EnemyFactory.animate(entity, EnemyComponent.ACTION.DYING)
	}
}
