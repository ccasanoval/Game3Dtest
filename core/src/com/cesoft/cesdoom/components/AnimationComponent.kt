package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.AnimationController

////////////////////////////////////////////////////////////////////////////////////////////////////
//
data class AnimationParams(var id: String, var loop: Int = 1, var speed: Float = 1f, var offset: Float = 0f, var duration: Float = -1f)
class AnimationComponent(instance: ModelInstance) : Component {
	private val animationController: AnimationController = AnimationController(instance)

	init {
		animationController.allowSameAnimation = true
	}

	fun animate(params: AnimationParams) {
		animationController.animate(params.id, params.offset, params.duration, params.loop, params.speed, null, 0f)
	}

	fun update(delta: Float) {
		animationController.update(delta)
	}
}