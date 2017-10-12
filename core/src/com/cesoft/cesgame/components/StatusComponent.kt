package com.cesoft.cesgame.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.cesoft.cesgame.managers.EnemyFactory

////////////////////////////////////////////////////////////////////////////////////////////////////
class StatusComponent(private val entity: Entity) : Component//private val animat: AnimationComponent, private val enemy: EnemyComponent) : Component
{
	private var health: Float = 100f
		fun hurt(pain: Float = 34f){health -= pain}

	private var aliveStateTime: Float = 0f
		fun isOut() = aliveStateTime > 3.4f//TODO: Constante de tiempo de animacion dying en EnemyFactory

	var isAlive: Boolean = true
		private set
		//set(value) {field = value; playDeath()}

	var running: Boolean = true
		set(value) {if(!isAlive || !value)return; field = value; playRun()}
	var walking: Boolean = true
		set(value) {if(!isAlive || !value)return; field = value; playWalk()}
	var attacking: Boolean = false
		set(value) {if(!isAlive || !value)return; field = value; playAttack()}

	//______________________________________________________________________________________________
	fun update(delta: Float) {
		if(isAlive && health < 0)
		{
			isAlive = false
			playDeath()
		}
		if( ! isAlive)
		{
			aliveStateTime += delta
		}
	}

	//______________________________________________________________________________________________
	private fun playDeath() {
		EnemyFactory.setAnimation(entity, EnemyComponent.ACTION.DYING)
	}
	//______________________________________________________________________________________________
	private fun playRun() {
		EnemyFactory.setAnimation(entity, EnemyComponent.ACTION.RUNNING)
	}
	//______________________________________________________________________________________________
	private fun playWalk() {
		EnemyFactory.setAnimation(entity, EnemyComponent.ACTION.WALKING)
	}
	//______________________________________________________________________________________________
	private fun playAttack() {
		EnemyFactory.setAnimation(entity, EnemyComponent.ACTION.ATTACKING)
	}
}
