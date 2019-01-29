package com.cesoft.cesdoom.components

import com.badlogic.ashley.core.Component


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object GateComponent : Component {

    const val LONG = 25f
    const val HIGH = 25f
    const val THICK = 3f
    const val MAX_OFFSET_OPEN = 2*LONG - 2f
}