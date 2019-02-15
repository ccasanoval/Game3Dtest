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

	//var isSaltando = true
	var estado = EnemyComponent.ACTION.WALKING

	var deadStateTime: Float = 0f
	var achingStateTime: Float = 0f


	/// DEAD
	fun isDead() = estado == EnemyComponent.ACTION.DYING

	/// ACHING
	var health: Float = 100f
	fun isAching() = estado == EnemyComponent.ACTION.ACHING
	fun setAchingState() {estado = EnemyComponent.ACTION.ACHING}


	/// RUNNING
	fun isRunning() = estado == EnemyComponent.ACTION.RUNNING
	fun setRunningState() {estado = EnemyComponent.ACTION.RUNNING}


	/// WALKING
	fun isWalking() = estado == EnemyComponent.ACTION.WALKING
	fun setWalkingState() {estado = EnemyComponent.ACTION.WALKING}


	/// ATACKING
	fun isAttacking() = estado == EnemyComponent.ACTION.ATTACKING
	fun setAttackingState() {estado = EnemyComponent.ACTION.ATTACKING}


}
