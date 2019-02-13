package com.cesoft.cesdoom.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.signals.Signal
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.components.AmmoComponent
import com.cesoft.cesdoom.components.ModelComponent
import com.cesoft.cesdoom.entities.Ammo
import com.cesoft.cesdoom.events.GameEvent
import com.cesoft.cesdoom.events.GameQueue


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class AmmoSystem(gameEventSignal: Signal<GameEvent>) : EntitySystem(), EntityListener {

    private val eventQueue = GameQueue()
    init {
        gameEventSignal.add(eventQueue)
    }

    /// Implements EntityListener
    private lateinit var ammo: ImmutableArray<Entity>
    override fun entityRemoved(entity: Entity?) {
        ammo = engine.getEntitiesFor(Family.all(AmmoComponent::class.java).get()) as ImmutableArray<Entity>
    }
    override fun entityAdded(entity: Entity?) {
        ammo = engine.getEntitiesFor(Family.all(AmmoComponent::class.java).get()) as ImmutableArray<Entity>
    }

    /// Extends EntitySystem
    override fun addedToEngine(engine: Engine) {
        ammo = engine.getEntitiesFor(Family.all(AmmoComponent::class.java).get()) as ImmutableArray<Entity>
    }
    override fun update(delta: Float) {
        processEvents()
        for(entity in ammo) {
            (entity as Ammo).update(engine)
        }
    }
    private fun processEvents() {
        for(event in eventQueue.events) {
            when(event.type) {
                GameEvent.Type.AMMO_PICKUP -> {
                    //val ammo = AmmoComponent.get(event.entity!!)
                    //ammo.isPickedUp = true
                    (event.entity!! as Ammo).pickup()
                }
                else -> Unit
            }
        }
    }

}