package com.cesoft.cesdoom.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.cesoft.cesdoom.GameWorld
import com.cesoft.cesdoom.components.StatusComponent
import com.cesoft.cesdoom.entities.Enemy

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class StatusSystem(private val gameWorld: GameWorld) : EntitySystem() {
	private lateinit var entities: ImmutableArray<Entity>

	//______________________________________________________________________________________________
	override fun addedToEngine(engine: Engine?) {
		entities = engine!!.getEntitiesFor(Family.all(StatusComponent::class.java).get())
	}

	//______________________________________________________________________________________________
	override fun update(delta: Float) {
		for(entity in entities) {
			val status = entity.getComponent(StatusComponent::class.java)
			status.update(delta)
			if(status.isDead())
				gameWorld.removeEnemyCollider(entity as Enemy)
			if(status.isDeadOver()) {
				gameWorld.enemyDied(entity as Enemy)
			}
		}
	}
}
