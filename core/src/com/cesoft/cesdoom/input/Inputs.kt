package com.cesoft.cesdoom.input

object Inputs {

    enum class Value(val value: Int) { NEGATIVE(-1), ZERO(0), POSITIVE(+1) }

    val Names = arrayOf("Start", "Back", "Exit", "Fire", "Jump", "Look X", "Look Y", "Move X", "Move Y")////TODO:I18N
    const val START = 0
    const val BACK = 1
    const val EXIT = 2
    const val FIRE = 3
    const val JUMP = 4
    const val LOOK_X = 5
    const val LOOK_Y = 6
    const val MOVE_X = 7
    const val MOVE_Y = 8

    const val MAX = 9
}