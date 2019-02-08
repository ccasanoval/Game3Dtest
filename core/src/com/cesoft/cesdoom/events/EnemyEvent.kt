package com.cesoft.cesdoom.events

import com.cesoft.cesdoom.entities.Enemy

class EnemyEvent(val type: Type, val enemy: Enemy, val value: Int) {

    enum class Type {
        HURT,
    }
}