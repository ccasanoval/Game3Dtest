package com.cesoft.cesgame.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.cesoft.cesgame.GameWorld
import com.cesoft.cesgame.components.ShotComponent

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class ShotSystem(private val gameWorld: GameWorld) : EntitySystem() {
	private lateinit var entities: ImmutableArray<Entity>

	//______________________________________________________________________________________________
	override fun addedToEngine(engine: Engine) {
		entities = engine.getEntitiesFor(Family.all(ShotComponent::class.java).get())
	}

	//______________________________________________________________________________________________
	override fun update(delta: Float) {
		for(entity in entities) {
			/// Los tiros duran solo un tiempo
			val shot = entity.getComponent(ShotComponent::class.java)
			shot.update(delta)
			if(shot.isEnd()) {
				gameWorld.remove(entity)
				continue
			}
		}
	}
}