package com.cesoft.cesgame.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.cesoft.cesgame.GameWorld
import com.cesoft.cesgame.components.StatusComponent

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class StatusSystem(private val gameWorld: GameWorld) : EntitySystem() {
	private lateinit var entities: ImmutableArray<Entity>

	override fun addedToEngine(engine: Engine?) {
		entities = engine!!.getEntitiesFor(Family.all(StatusComponent::class.java).get())
	}

	override fun update(delta: Float) {
		for(e in entities) {
			val status = e.getComponent(StatusComponent::class.java)
			status.update(delta)
			if(status.aliveStateTime >= 3.4f)
				gameWorld.remove(e)
		}
	}
}
