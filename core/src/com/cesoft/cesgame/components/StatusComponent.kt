package com.cesoft.cesgame.components

import com.badlogic.ashley.core.Component
import com.cesoft.cesgame.managers.EnemyAnimations

////////////////////////////////////////////////////////////////////////////////////////////////////
class StatusComponent(private val animationComponent: AnimationComponent) : Component
{
	var alive: Boolean = true
		set(value) {field = value; playDeathAnim2()}
	var running: Boolean = true
	var attacking: Boolean = false
	var aliveStateTime: Float = 0f

	fun update(delta: Float) {
		if( ! alive) aliveStateTime += delta
	}

	private fun playDeathAnim2() {
		animationComponent.animate(EnemyAnimations.id, EnemyAnimations.offsetDeath2, EnemyAnimations.durationDeath2, 1, 3)
	}
}
