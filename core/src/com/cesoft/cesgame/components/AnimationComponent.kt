package com.cesoft.cesgame.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.AnimationController

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class AnimationComponent(instance: ModelInstance) : Component {
	private val animationController: AnimationController = AnimationController(instance)

	init {
		animationController.allowSameAnimation = true
	}

	fun animate(id: String, loops: Int = 1, speed: Float = 1f, offset: Float = 0f, duration: Float = -1f) {
		animationController.animate(id, offset, duration, loops, speed, null, 0f)
	}

	fun update(delta: Float) {
		animationController.update(delta)
	}
}