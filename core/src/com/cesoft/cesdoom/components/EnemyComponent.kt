package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class EnemyComponent(val type: TYPE, var id: Int) : Component
{
	companion object {
		private val mapper: ComponentMapper<EnemyComponent> = ComponentMapper.getFor(EnemyComponent::class.java)
		fun get(entity: Entity):EnemyComponent = mapper.get(entity)

		const val MASS = 100f
		const val RADIO = 18f
		const val DELAY_ATTACK = 500
		const val BITE_PAIN = 10
		const val KILL_REWARD = 20
	}

	enum class TYPE {
		MONSTER0, MONSTER1,
	}
	enum class ACTION {
		IDLE,
		DYING,
		ACHING,
		ATTACKING,
		WALKING,
		RUNNING,
		REINCARNATING,
	}

	var orientation: Double = 0.0
	var position = Vector3()
	var nextStep3D = Vector3()
	var stepCalc2D = Vector2()
	var currentPos2D = Vector2()
	//
	var isAccessLevelPath = false
	val player2D: Vector2 = Vector2.Zero//TODO: Make 3D?
	var path: ArrayList<Vector2>? = null
	var pathIndex = 0


	var currentAnimation = EnemyComponent.ACTION.IDLE
	var particleEffect: ParticleEffect? = null
}
