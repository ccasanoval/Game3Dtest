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

		private const val MASS0 = 110f
		private const val MASS1 = 45f
		private const val RADIO0 = 16f
		private const val RADIO1 = 10f
		private const val KILL_REWARD0 = 20
		private const val KILL_REWARD1 = 10
		//
		const val DELAY_ATTACK = 500
		const val BITE_PAIN = 10
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

	val reward: Int
		get() = when(type) {
			TYPE.MONSTER0 -> KILL_REWARD0
			TYPE.MONSTER1 -> KILL_REWARD1
		}
	val mass: Float
		get() = when(type) {
			TYPE.MONSTER0 -> MASS0
			TYPE.MONSTER1 -> MASS1
		}
	val radio: Float
		get() = when(type) {
			TYPE.MONSTER0 -> RADIO0
			TYPE.MONSTER1 -> RADIO1
		}

	var orientation: Double = 0.0
	var position = Vector3()
	var positionOld = Vector3()
	var nextStep3D = Vector3()
	var stepCalc2D = Vector2()
	var currentPos2D = Vector2()
	//
    var stepCounter = 0
	var isAccessFloorPath = false
	val player2D: Vector2 = Vector2.Zero//TODO: Make 3D?
	var path: ArrayList<Vector2>? = null
	var pathIndex = 0

	var currentAnimation = EnemyComponent.ACTION.IDLE
	var particleEffect: ParticleEffect? = null

	enum class StatusMov { QUIET, ATTACK, RUN, WALK, FALL }
	var statusMov: StatusMov = StatusMov.QUIET
}
