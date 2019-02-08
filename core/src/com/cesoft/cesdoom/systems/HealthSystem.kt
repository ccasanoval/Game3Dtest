package com.cesoft.cesdoom.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.signals.Signal
import com.badlogic.ashley.utils.ImmutableArray
import com.cesoft.cesdoom.components.AmmoComponent
import com.cesoft.cesdoom.entities.Health
import com.cesoft.cesdoom.events.GameEvent

class HealthSystem(private val gameEventSignal: Signal<GameEvent>) : EntitySystem(), EntityListener {

    /// Implements EntityListener
    private lateinit var health: ImmutableArray<Health>
    override fun entityRemoved(entity: Entity?) {
        health = engine.getEntitiesFor(Family.all(AmmoComponent::class.java).get()) as ImmutableArray<Health>
    }
    override fun entityAdded(entity: Entity?) {
        health = engine.getEntitiesFor(Family.all(AmmoComponent::class.java).get()) as ImmutableArray<Health>
    }

    /// Extends EntitySystem
    override fun addedToEngine(engine: Engine) {
        health = engine.getEntitiesFor(Family.all(AmmoComponent::class.java).get()) as ImmutableArray<Health>
    }


    //______________________________________________________________________________________________
    override fun update(delta: Float) {
    }
}