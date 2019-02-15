package com.cesoft.cesdoom.events

import com.cesoft.cesdoom.entities.Enemy
import com.cesoft.cesdoom.util.Log

class EnemyEvent(val type: Type, val enemy: Enemy, val value: Int) : Comparable<EnemyEvent> {

    enum class Type {
        HURT,
    }

    /// Implements Comparable<EnemyEvent>
    override fun compareTo(other: EnemyEvent): Int {
        Log.e("EnemyEvent", "compareTo---------------equal(${(type == other.type && enemy== other.enemy)})------------------ $type <> ${other.type}   /// ${enemy.hashCode()} <> ${other.enemy.hashCode()} ")
        if(type == other.type && enemy == other.enemy)
            return 0
        return 1
    }
}