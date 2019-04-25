package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class StatusComponent: Component {

	companion object {
		private val mapper: ComponentMapper<StatusComponent> = ComponentMapper.getFor(StatusComponent::class.java)
		fun get(entity: Entity): StatusComponent = mapper.get(entity)
	}

	var alive = false

	var status = EnemyComponent.ACTION.WALKING

	var deadStateTime: Float = 0f
	var achingStateTime: Float = 0f


	/// DEAD
	fun isDead() = status == EnemyComponent.ACTION.DYING

	/// ACHING
	var health: Float = 100f
	fun isAching() = status == EnemyComponent.ACTION.ACHING
	fun setAchingState() {status = EnemyComponent.ACTION.ACHING}


	/// RUNNING
	fun isRunning() = status == EnemyComponent.ACTION.RUNNING
	fun setRunningState() {status = EnemyComponent.ACTION.RUNNING}


	/// WALKING
	fun isWalking() = status == EnemyComponent.ACTION.WALKING
	fun setWalkingState() {status = EnemyComponent.ACTION.WALKING}


	/// ATTACKING
	fun isAttacking() = status == EnemyComponent.ACTION.ATTACKING
	fun setAttackingState() {status = EnemyComponent.ACTION.ATTACKING}


}
