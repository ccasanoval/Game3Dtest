package com.cesoft.cesdoom.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.cesoft.cesdoom.components.GateComponent
import com.cesoft.cesdoom.entities.Gate
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GateSystem : EntitySystem(), EntityListener {
    private var gates: ImmutableArray<Gate>? = null
    //private var player: Player? = null
    //private val gates = ArrayList<Gate>()

    init {
        //GateFactory.init(game)
    }

    override fun entityRemoved(entity: Entity?) {
    }

    override fun entityAdded(entity: Entity?) {
    }


    //______________________________________________________________________________________________
    override fun addedToEngine(e: Engine) {
        gates = e.getEntitiesFor(Family.all(GateComponent::class.java).get()) as ImmutableArray<Gate>?
        //e.addEntityListener(Family.one(PlayerComponent::class.java).get(), this)
    }

    //______________________________________________________________________________________________
    override fun update(delta: Float) {
        gates?.let { gates ->
            for(entity in gates) {
               entity.update(delta)
            }
        }
    }
}