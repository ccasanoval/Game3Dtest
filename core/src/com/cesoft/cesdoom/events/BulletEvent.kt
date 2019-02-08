package com.cesoft.cesdoom.events

import com.badlogic.ashley.core.Entity
import com.cesoft.cesdoom.util.Log


class BulletEvent(val type: Type, val entity: Entity) : Comparable<BulletEvent> {

    enum class Type {
        REMOVE,
    }

    /// Implements Comparable<BulletEvent>
    override fun compareTo(other: BulletEvent): Int {
        Log.e("BulletEvent", "compareTo---------------equal(${(type == other.type && entity== other.entity)})------------------ ${this.type} <> ${other.type}   /// ${this.entity} <> ${other.entity} ")
        if(type == other.type && entity == other.entity)
            return 0
        return 1
    }
}