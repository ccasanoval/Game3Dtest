package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.cesoft.cesdoom.managers.EnemyFactory

////////////////////////////////////////////////////////////////////////////////////////////////////
class StatusComponent(private val entity: Entity) : Component//private val animat: AnimationComponent, private val enemy: EnemyComponent) : Component
{
	var isSaltando = true
	private var estado = EnemyComponent.ACTION.WALKING
	val type = entity.getComponent(EnemyComponent::class.java).type

	/// DEAD
	private var deadStateTime: Float = 0f
	fun isDead() = estado == EnemyComponent.ACTION.DYING
	private fun setDeadState() { estado = EnemyComponent.ACTION.DYING }

	/// ACHING
	private var health: Float = 100f
		private set
	private var achingStateTime: Float = 0f
	fun isAching() = estado == EnemyComponent.ACTION.ACHING
	private fun setAchingState() {estado = EnemyComponent.ACTION.ACHING}
	fun hurt(pain: Float = 50f)
	{
		if(isDead())return
		if( ! isAching())
		{
			health -= pain
			achingStateTime = 0f
			setAchingState()
			EnemyFactory.playAching(entity)
		}
		else
			health -= 5f
	}

	/// RUNNING
	fun isRunning() = estado == EnemyComponent.ACTION.RUNNING
	private fun setRunningState() {estado = EnemyComponent.ACTION.RUNNING}
	fun setRunning()
	{
		if(isDead() || isAching())return
		if( ! isRunning()) {
			setRunningState()
			EnemyFactory.playRunning(entity)
		}
	}

	/// WALKING
	fun isWalking() = estado == EnemyComponent.ACTION.WALKING
	private fun setWalkingState() {estado = EnemyComponent.ACTION.WALKING}
	fun setWalking()
	{
		if(isDead() || isAching())return
		if( ! isWalking()) {
			setWalkingState()
			EnemyFactory.playWalking(entity)
		}
	}

	/// ATACKING
	fun isAttacking() = estado == EnemyComponent.ACTION.ATTACKING
	private fun setAttackingState() {estado = EnemyComponent.ACTION.ATTACKING}
	fun setAttacking()
	{
		if(isDead() || isAching())return
		if( ! isAttacking()) {
			setAttackingState()
			EnemyFactory.playAttack(entity)
		}
	}

	//______________________________________________________________________________________________
	fun update(delta: Float) {
		//EnemyFactory.update(entity, delta)
		if( ! isDead() && health < 0)
		{
			setDeadState()
			EnemyFactory.playDying(entity)
		}
		else if(isDead())
		{
			deadStateTime += delta
		}
		else if(isAching())
		{
			achingStateTime += delta
			if(isAchingOver()) {
				achingStateTime=0f
				estado = EnemyComponent.ACTION.IDLE
				setWalking()
			}
		}
	}

	//______________________________________________________________________________________________
	fun isDeadOver() = deadStateTime > EnemyFactory.getActionDuration(type, EnemyComponent.ACTION.DYING)
	fun deathProgres() = deadStateTime / EnemyFactory.getActionDuration(type, EnemyComponent.ACTION.DYING)

	//______________________________________________________________________________________________
	fun isAchingOver() = achingStateTime > EnemyFactory.getActionDuration(type, EnemyComponent.ACTION.ACHING)
}