package com.cesoft.cesdoom.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.cesoft.cesdoom.components.GateComponent
import com.cesoft.cesdoom.entities.Ammo


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class AmmoSystem : EntitySystem(), EntityListener {
    private var ammo: ImmutableArray<Ammo>? = null

    override fun entityRemoved(entity: Entity?) {}

    override fun entityAdded(entity: Entity?) {}


    //______________________________________________________________________________________________
    override fun addedToEngine(e: Engine) {
        ammo = e.getEntitiesFor(Family.all(GateComponent::class.java).get()) as ImmutableArray<Ammo>?
    }

    //______________________________________________________________________________________________
    override fun update(delta: Float) {
        ammo?.let { ammo ->
            for(entity in ammo) {
               entity.update(delta)
            }
        }
    }
}