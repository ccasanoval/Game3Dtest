package com.cesoft.cesdoom.input

object Inputs {

    enum class Value(val value: Int) { NEGATIVE(-1), ZERO(0), POSITIVE(+1) }

    val Names = arrayOf("Start", "Back", "Exit", "Fire", "Jump", "Look X", "Look Y", "Move X", "Move Y")////TODO:I18N
	enum class ActionName(val value: Int) {
		START(0), BACK(1), EXIT(2), FIRE(3),
		LOOK_X(4), LOOK_Y(5), MOVE_X(6), MOVE_Y(7),
	}

	data class Action private constructor(val action: ActionName, val isButton: Boolean) {
		companion object {
			val Start = Action(ActionName.START, true)
			val Back = Action(ActionName.BACK, true)
			val Exit = Action(ActionName.EXIT, true)
			val Fire = Action(ActionName.FIRE, true)
			val LookX = Action(ActionName.LOOK_X, false)
			val LookY = Action(ActionName.LOOK_Y, false)
			val MoveX = Action(ActionName.MOVE_X, false)
			val MoveY = Action(ActionName.MOVE_Y, false)
			fun getAction(actionName: ActionName): Action =
					when (actionName) {
						ActionName.START -> Start
						ActionName.BACK -> Back
						ActionName.EXIT -> Exit
						ActionName.FIRE -> Fire
						ActionName.LOOK_X -> LookX
						ActionName.LOOK_Y -> LookY
						ActionName.MOVE_X -> MoveX
						ActionName.MOVE_Y -> MoveY
					}
		}
	}

}