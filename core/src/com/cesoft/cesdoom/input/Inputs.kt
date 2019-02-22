package com.cesoft.cesdoom.input

object Inputs {

    enum class Value(val value: Int) { NEGATIVE(-1), ZERO(0), POSITIVE(+1) }
    const val START = 1
    //const val MENU = 2
    const val BACK = 3
    const val FIRE = 4
    const val JUMP = 5
    const val LOOK_X = 6
    const val LOOK_Y = 7
    const val MOVE_X = 8
    const val MOVE_Y = 9
}