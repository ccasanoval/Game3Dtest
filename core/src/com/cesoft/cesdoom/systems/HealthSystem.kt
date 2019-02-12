package com.cesoft.cesdoom.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.signals.Signal
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.components.HealthComponent
import com.cesoft.cesdoom.components.ModelComponent
import com.cesoft.cesdoom.entities.Health
import com.cesoft.cesdoom.events.GameEvent
import com.cesoft.cesdoom.events.GameQueue
import com.cesoft.cesdoom.util.Log


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

    //______________________________________________________________________________________________
    override fun update(delta: Float) {
        processEvents()
        health.let { health ->
            for(entity in health) {
                (entity as Health).update(engine)

//                val obj = HealthComponent.get(entity)
//                if(obj.isPickedUp) {
//                    engine.removeEntity(entity)
//                }
//                else {
//                    val model = ModelComponent.get(entity)
//                    model.instance.transform.rotate(Vector3.Y, 5f)
//                }
            }
        }
    }

    //______________________________________________________________________________________________
    private fun processEvents() {
        for(event in eventQueue.events) {
            when(event.type) {
                GameEvent.Type.HEALTH_PICKUP -> {
                    //val health = HealthComponent.get(event.entity!!)
                    //health.isPickedUp = true
                    (event.entity!! as Health).isPickedUp = true
                }
                else -> Unit
            }
        }
    }
}