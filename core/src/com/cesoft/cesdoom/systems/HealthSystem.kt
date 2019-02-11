package com.cesoft.cesdoom.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.cesoft.cesdoom.components.HealthComponent
import com.cesoft.cesdoom.entities.Health


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class HealthSystem : EntitySystem(), EntityListener {
    private lateinit var health: ImmutableArray<Entity>

    override fun entityRemoved(entity: Entity?) {
        health = engine.getEntitiesFor(Family.all(HealthComponent::class.java).get())
    }

    override fun entityAdded(entity: Entity?) {
        health = engine.getEntitiesFor(Family.all(HealthComponent::class.java).get())
    }


    //______________________________________________________________________________________________
    override fun addedToEngine(engine: Engine) {
        health = engine.getEntitiesFor(Family.all(HealthComponent::class.java).get())
    }

    //______________________________________________________________________________________________
    override fun update(delta: Float) {
        health.let { health ->
            for(entity in health) {
                if((entity as Health).isPickedUp) {
                    engine.removeEntity(entity)
                }
                else {
                    entity.update()
                }
            }
        }
    }
}