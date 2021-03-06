package com.cesoft.cesdoom.events

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class RenderEvent(val type: Type, val param: Any) : Comparable<RenderEvent> {

    enum class Type {
        SET_AMBIENT_COLOR,
        ADD_PARTICLE_FX
    }

    /// Implements Comparable<GameEvent>
    override fun compareTo(other: RenderEvent): Int {
        //com.cesoft.cesdoom.util.Log.e("RenderEvent", "compareTo---------------equal(${(type == other.type && param == other.param)})------------------ ${this.type} <> ${other.type}   /// ${this.param} <> ${other.param} ")
        if(type == other.type && param == other.param)
            return 0
        return 1
    }
}