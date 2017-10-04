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

	fun animate(id: String, loops: Int, speed: Int) {
		animationController.animate(id, loops, speed.toFloat(), null, 0f)
	}

	fun animate(id: String, offset: Float, duration: Float, loopCount: Int, speed: Int) {
		animationController.animate(id, offset, duration, loopCount, speed.toFloat(), null, 0f)
	}

	fun update(delta: Float) {
		animationController.update(delta)
	}
}