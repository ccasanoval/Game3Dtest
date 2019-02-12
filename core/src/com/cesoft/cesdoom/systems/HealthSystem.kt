package com.cesoft.cesdoom.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.signals.Signal
import com.badlogic.ashley.utils.ImmutableArray
import com.cesoft.cesdoom.components.HealthComponent
import com.cesoft.cesdoom.entities.Health
import com.cesoft.cesdoom.events.GameEvent
import com.cesoft.cesdoom.events.GameQueue


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class HealthSystem(gameEventSignal: Signal<GameEvent>) : EntitySystem(), EntityListener {

    private val eventQueue = GameQueue()
    init {
        gameEventSignal.add(eventQueue)
    }

    /// Implements EntityListener
    private lateinit var health: ImmutableArray<Entity>
    override fun entityRemoved(entity: Entity?) {
        health = engine.getEntitiesFor(Family.all(HealthComponent::class.java).get())
    }
    override fun entityAdded(entity: Entity?) {
        health = engine.getEntitiesFor(Family.all(HealthComponent::class.java).get())
    }

    /// Extends EntitySystem
    override fun addedToEngine(engine: Engine) {
        health = engine.getEntitiesFor(Family.all(HealthComponent::class.java).get())
    }
    override fun update(delta: Float) {
        processEvents()
        health.let { health ->
            for(entity in health) {
                (entity as Health).update(engine)
            }
        }
    }
    private fun processEvents() {
        for(event in eventQueue.events) {
            when(event.type) {
                GameEvent.Type.HEALTH_PICKUP -> {
                    (event.entity!! as Health).pickup()
                }
                else -> Unit
            }
        }
    }
}