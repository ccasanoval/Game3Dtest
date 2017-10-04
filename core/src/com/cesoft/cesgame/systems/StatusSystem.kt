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
	private var entities: ImmutableArray<Entity>? = null

	override fun addedToEngine(engine: Engine?) {
		entities = engine!!.getEntitiesFor(Family.all(StatusComponent::class.java).get())
	}

	override fun update(delta: Float) {
		for(i in 0 until entities!!.size()) {
			val entity = entities!!.get(i)
			entity.getComponent(StatusComponent::class.java).update(delta)
			if(entity.getComponent(StatusComponent::class.java).aliveStateTime >= 3.4f)
				gameWorld.remove(entity)
		}
	}
}
