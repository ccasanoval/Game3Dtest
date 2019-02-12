package com.cesoft.cesdoom.events

import com.cesoft.cesdoom.util.Log


class RenderEvent(val type: Type, val param: Any) : Comparable<RenderEvent> {

    enum class Type {
        SET_AMBIENT_COLOR,
    }

    /// Implements Comparable<GameEvent>
    override fun compareTo(other: RenderEvent): Int {
        Log.e("RenderEvent", "compareTo---------------equal(${(type == other.type && param == other.param)})------------------ ${this.type} <> ${other.type}   /// ${this.param} <> ${other.param} ")
        if(type == other.type && param == other.param)
            return 0
        return 1
    }
}