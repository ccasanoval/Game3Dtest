package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.AnimationController

////////////////////////////////////////////////////////////////////////////////////////////////////
//
data class AnimationParams(var id: String, var loop: Int = 1, var speed: Float = 1f, var offset: Float = 0f, var duration: Float = -1f, var transitionTime: Float = 0.5f) {
//	init {
//	    com.cesoft.cesdoom.util.Log.e("AnimationParams", "**********************-----id=$id, loop=$loop, speed=$speed, offset=$offset, duration=$duration")
//	}
}
class AnimationComponent(instance: ModelInstance) : Component {

	companion object {
		private val mapper: ComponentMapper<AnimationComponent> = ComponentMapper.getFor(AnimationComponent::class.java)
		fun get(entity: Entity):AnimationComponent = mapper.get(entity)
	}

	private val animationController: AnimationController = AnimationController(instance)

	init {
		animationController.allowSameAnimation = true
	}

	fun animate(params: AnimationParams) {
		animationController.animate(params.id, params.offset, params.duration, params.loop, params.speed, null, params.transitionTime)
	}

	fun update(delta: Float) {
		animationController.update(delta)
	}
}