package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.util.Log

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class EnemyComponent(val type: TYPE, var id: Int) : Component
{
	companion object {
		private val mapper: ComponentMapper<EnemyComponent> = ComponentMapper.getFor(EnemyComponent::class.java)
		fun get(entity: Entity):EnemyComponent = mapper.get(entity)

		private const val MASS0 = 110f
		private const val MASS1 = 45f
		private const val RADIO0 = 12f///////16
		private const val RADIO1 = 10f////////12
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

	var forceWalk = 0f
	var forceRun = 0f

	var orientation: Double = 0.0
	var position = Vector3()
	var positionOld = Vector3()
	var nextStep3D = Vector3()
	var stepCalc2D = Vector2()
	var currentPos2D = Vector2()

	/// Get out of trap
	var nextEscape = 0
	private val lastNodes: ArrayList<Int> = arrayListOf(0)
	private var repeatedPos: Int = 0
		fun incRepeatedPos(lastNode: Int) {
			for(node in lastNodes) {
				if(lastNode == node) {
					//Log.e("EnemyComp", "incRepeatedPos id=$id------------------------------------------node=$node repeatedPos=$repeatedPos nextEscape=$nextEscape")
					repeatedPos++
					return
				}
			}
			//if(repeatedPos > 4)Log.e("EnemyComp", "incRepeatedPos id=$id------------------CLEANING------------------------ repeatedPos=$repeatedPos nextEscape=$nextEscape")
			repeatedPos = 0
			nextEscape = 0
			//lastNodes.clear()
			if(lastNodes.size > 7) lastNodes.removeAt(0)
			lastNodes.add(lastNode)
			//Log.e("EnemyComponent", "incRepeatedPos : id=$id repeatedPos=$repeatedPos")
		}
		fun isTrapped() = repeatedPos > 3

	/// Path finding
    var stepCounter = 0
	var isAccessFloorPath = false
	val player2D: Vector2 = Vector2.Zero//TODO: Make 3D?
	var path: ArrayList<Vector2>? = null
	var pathIndex = 0

	var currentAnimation = ACTION.IDLE
	var particleEffect: ParticleEffect? = null

	enum class StatusMov { QUIET, ATTACK, RUN, WALK, FALL }
	var statusMov: StatusMov = StatusMov.QUIET


	fun reset(level: Int, random: Int) {
		currentAnimation = ACTION.WALKING
		isAccessFloorPath = false
		player2D.set(Vector2.Zero)
		path = null
		pathIndex = 0
		//
		repeatedPos = 0
		nextEscape = 0
		lastNodes.clear()
		//
		forceWalk = (level * 75) + random + if(type == TYPE.MONSTER0) 1200f else 1000f
		forceRun = (level * 75) + random + if(type == TYPE.MONSTER0) 1700f else 1300f
	}
}
