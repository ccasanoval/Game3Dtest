package com.cesoft.cesdoom.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.PovDirection
import com.cesoft.cesdoom.CesDoom
import kotlin.math.absoluteValue
import com.cesoft.cesdoom.input.Inputs.Value
import com.cesoft.cesdoom.screens.MainMenuScreen
import com.cesoft.cesdoom.util.Log
import com.sun.org.apache.xpath.internal.operations.Bool

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class InputMapper {
	companion object {
		private val tag = InputMapper::class.java.simpleName
	}

    // ANDROID TV
    var center = false
    var left = false
    var right = false
    var up = false
    var down = false

    // GAMEPAD
    private val mapper: MutableMap<Int, Inputs.Action> = mutableMapOf()       // Evento       -> Constante
    private val values: MutableMap<Inputs.Action, Value> = mutableMapOf()     // Constante    -> ON, OFF  o  LEFT, CENTER, RIGHT
    private val axisOffset: MutableMap<Inputs.Action, Float> = mutableMapOf() // Offset para Ejes
	fun addMap(event: Int, action: Inputs.Action, offset: Float=.1f) {
        mapper[event] = action
        axisOffset[action] = offset
    }

    // KEYBOARD
    private data class AxisKey(val axis: Inputs.Action, val positive: Boolean) : Comparable<AxisKey> {
        override fun compareTo(other: AxisKey): Int {
            return if(axis == other.axis && positive == other.positive) return 0 else 1
        }
    }
    private val mapperKey: MutableMap<AxisKey, Int> = mutableMapOf()// (Constante, Signo) -> Evento
    private fun addMapKey(event: Int, button: Inputs.Action, positive: Boolean=true) {
        mapperKey[AxisKey(button, positive)] = event
    }
	private fun addKeyboard() {
        addMapKey(Input.Keys.ENTER, Inputs.Action.Start)
        addMapKey(Input.Keys.DEL, Inputs.Action.Back)
        addMapKey(Input.Keys.X, Inputs.Action.Exit)
        addMapKey(Input.Keys.CONTROL_LEFT, Inputs.Action.Fire)
        addMapKey(Input.Keys.CONTROL_RIGHT, Inputs.Action.Fire)
        addMapKey(Input.Keys.LEFT, Inputs.Action.LookX ,false)
        addMapKey(Input.Keys.RIGHT, Inputs.Action.LookX, true)
        addMapKey(Input.Keys.DOWN, Inputs.Action.LookY, false)
        addMapKey(Input.Keys.UP, Inputs.Action.LookY, true)
        addMapKey(Input.Keys.A, Inputs.Action.MoveX, false)
        addMapKey(Input.Keys.D, Inputs.Action.MoveX, true)
        addMapKey(Input.Keys.S, Inputs.Action.MoveY, false)
        addMapKey(Input.Keys.W, Inputs.Action.MoveY, true)
    }
	init {
		if( ! CesDoom.isMobile)
			addKeyboard()
	}


    fun axisMoved(axisCode: Int, value: Float) {
        mapper[axisCode]?.let { axis ->
            if(value.absoluteValue > axisOffset[axis]?:0.1f) {
                values[axis] = if(value < 0) Value.POSITIVE else Value.NEGATIVE
				//Log.e(tag, "axisMoved-----axisCode=$axisCode---axis=$axis ------- ${axisOffset[axis]} ------------------- "+values[axis])
            }
            else {
                values[axis] = Value.ZERO
				//Log.e(tag, "axisMoved-------- $axis ------- ${axisOffset[axis]} ------------------- "+values[axis])
            }
        }
    }
    fun povMoved(povCode: Int, value: PovDirection?) {
        Log.e(tag, "--------------------------------------------$value")
        when(value) {
            PovDirection.east -> values[Inputs.Action.MoveX] = Value.NEGATIVE
            PovDirection.west -> values[Inputs.Action.MoveX] = Value.POSITIVE
            PovDirection.south -> values[Inputs.Action.MoveY] = Value.NEGATIVE
            PovDirection.north -> values[Inputs.Action.MoveY] = Value.POSITIVE
            PovDirection.southEast -> {
                values[Inputs.Action.MoveX] = Value.NEGATIVE
                values[Inputs.Action.MoveY] = Value.NEGATIVE
            }
            PovDirection.southWest -> {
                values[Inputs.Action.MoveX] = Value.POSITIVE
                values[Inputs.Action.MoveY] = Value.NEGATIVE
            }
            PovDirection.northEast -> {
                values[Inputs.Action.MoveX] = Value.NEGATIVE
                values[Inputs.Action.MoveY] = Value.POSITIVE
            }
            PovDirection.northWest -> {
                values[Inputs.Action.MoveX] = Value.POSITIVE
                values[Inputs.Action.MoveY] = Value.POSITIVE
            }
            else -> Unit
        }
    }
    fun buttonUp(buttonCode: Int) {
        mapper[buttonCode]?.let { button ->
            if(button.isButton)
                values[button] = Value.ZERO
        }
    }
    fun buttonDown(buttonCode: Int) {
        mapper[buttonCode]?.let { button ->
            if(button.isButton)
                values[button] = Value.POSITIVE
        }
    }


    fun isButtonPressed(button: Inputs.Action):Boolean {
        return (values[button] == Value.POSITIVE && button.isButton)
            || (!CesDoom.isMobile
				&& mapperKey[AxisKey(button, true)] != null
				&& Gdx.input.isKeyPressed(mapperKey[AxisKey(button, true)]!!))
	}
    fun getAxisValue(axis: Inputs.Action):Value {
        var v = values[axis]?:Value.ZERO
        if(axis.isButton) v = Value.ZERO
        if(v == Value.ZERO && !CesDoom.isMobile) {
            if(Gdx.input.isKeyPressed(mapperKey[AxisKey(axis, false)]?:-1))
                v = Value.NEGATIVE
            else if(Gdx.input.isKeyPressed(mapperKey[AxisKey(axis, true)]?:-1))
                v = Value.POSITIVE
        }
        return v
    }
    fun isAxisValuePositive(axis: Inputs.Action):Boolean
            = (values[axis] == Value.POSITIVE && !axis.isButton)
            || (!CesDoom.isMobile && Gdx.input.isKeyPressed(mapperKey[AxisKey(axis, true)]?:-1))
    fun isAxisValueNegative(axis: Inputs.Action):Boolean
            = (values[axis] == Value.NEGATIVE && !axis.isButton)
            || (!CesDoom.isMobile && Gdx.input.isKeyPressed(mapperKey[AxisKey(axis, false)]?:-1))

    fun isGoingUp(): Boolean = isAxisValuePositive(Inputs.Action.MoveY) || isAxisValuePositive(Inputs.Action.LookY)
    fun isGoingDown(): Boolean = isAxisValueNegative(Inputs.Action.MoveY) || isAxisValueNegative(Inputs.Action.LookY)
    fun isGoingBackwards(): Boolean = isAxisValuePositive(Inputs.Action.MoveX) || isAxisValuePositive(Inputs.Action.LookX)
    fun isGoingForward(): Boolean = isAxisValueNegative(Inputs.Action.MoveX) || isAxisValueNegative(Inputs.Action.LookX)
}