package com.cesoft.cesdoom.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.cesoft.cesdoom.components.AmmoComponent
import com.cesoft.cesdoom.entities.Ammo


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class AmmoSystem : EntitySystem(), EntityListener {
    private var ammo: ImmutableArray<Ammo>? = null

    override fun entityRemoved(entity: Entity?) {
        ammo = engine.getEntitiesFor(Family.all(AmmoComponent::class.java).get()) as ImmutableArray<Ammo>?
    }

    override fun entityAdded(entity: Entity?) {
        ammo = engine.getEntitiesFor(Family.all(AmmoComponent::class.java).get()) as ImmutableArray<Ammo>?
    }


    //______________________________________________________________________________________________
    override fun addedToEngine(engine: Engine) {
        ammo = engine.getEntitiesFor(Family.all(AmmoComponent::class.java).get()) as ImmutableArray<Ammo>?
    }

    //______________________________________________________________________________________________
    override fun update(delta: Float) {//TODO: Hacer lo mismo con status system... no hace falta usar GameWorld
        ammo?.let { ammo ->
            for(entity in ammo) {
                if(entity.isPickedUp) {
                    engine.removeEntity(entity)
                }
                else {
                    entity.update(delta)
                }
            }
        }
    }
}