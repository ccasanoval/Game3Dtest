package com.cesoft.cesdoom.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.cesoft.cesdoom.components.GateComponent
import com.cesoft.cesdoom.entities.Gate


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GateSystem : EntitySystem(), EntityListener {
    private var gates: ImmutableArray<Entity>? = null

    override fun entityRemoved(entity: Entity?) {}
    override fun entityAdded(entity: Entity?) {}


    //______________________________________________________________________________________________
    override fun addedToEngine(e: Engine) {
        gates = e.getEntitiesFor(Family.all(GateComponent::class.java).get())
    }

    //______________________________________________________________________________________________
    override fun update(delta: Float) {
        gates?.let { gates ->
            for(entity in gates) {
                (entity as Gate).update(delta)
            }
        }
    }
}