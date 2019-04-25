package com.cesoft.cesdoom.events

import com.badlogic.ashley.core.Entity

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameEvent(val type: Type, val value: Int) : Comparable<GameEvent> {

    var entity: Entity? = null
    constructor(type: Type, value: Int, entity: Entity) : this(type, value) {
        this.entity = entity
    }

    companion object {
        val YouWin = GameEvent(Type.YOU_WIN, 0)
        val EnemyDead = GameEvent(Type.ENEMY_DEAD, 0)
    }

    enum class Type {
        YOU_WIN,

        PLAYER_HURT,
        AMMO_PICKUP,
        HEALTH_PICKUP,

        ENEMY_DEAD,
    }

    /// Implements Comparable<GameEvent>
    override fun compareTo(other: GameEvent): Int {
        //Log.e("GameEvent", "compareTo-------------------equal=${(type == other.type && value == other.value && entity == other.entity)}--------------------------- ${this.type} <> ${other.type}   /// ${this.value} <> ${other.value} ")
        if(type == other.type && value == other.value && entity == other.entity)
            return 0
        return 1
    }
}
