package com.cesoft.cesdoom.input

object Inputs {

    enum class Value(val value: Int) { NEGATIVE(-1), ZERO(0), POSITIVE(+1) }

    val Names = arrayOf("Start", "Back", "Exit", "Fire", "Jump", "Look X", "Look Y", "Move X", "Move Y")////TODO:I18N
	enum class Action(val value: Int) {
		START(0), BACK(1), EXIT(2), FIRE(3), JUMP(4),
		LOOK_X(5), LOOK_Y(6), MOVE_X(7), MOVE_Y(8),
	}
}