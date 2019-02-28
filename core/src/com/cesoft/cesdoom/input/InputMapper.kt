package com.cesoft.cesdoom.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.PovDirection
import com.cesoft.cesdoom.CesDoom
import kotlin.math.absoluteValue
import com.cesoft.cesdoom.input.Inputs.Value
import com.cesoft.cesdoom.util.Log

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class InputMapper {
	companion object {
		private val tag = InputMapper::class.java.simpleName
	}

    // GAMEPAD
    private val mapper: MutableMap<Int, Inputs.Action> = mutableMapOf()       // Evento       -> Constante
    private val values: MutableMap<Inputs.Action, Value> = mutableMapOf()     // Constante    -> ON, OFF  o  LEFT, CENTER, RIGHT
    private val axisOffset: MutableMap<Inputs.Action, Float> = mutableMapOf() // Offset para Ejes
	fun addMap(event: Int, button: Inputs.Action, offset: Float=.1f) {
        mapper[event] = button
        axisOffset[button] = offset
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
        addMapKey(Input.Keys.ENTER, Inputs.Action.START)
        addMapKey(Input.Keys.DEL, Inputs.Action.BACK)
        addMapKey(Input.Keys.X, Inputs.Action.EXIT)
        addMapKey(Input.Keys.CONTROL_LEFT, Inputs.Action.FIRE)
        addMapKey(Input.Keys.CONTROL_RIGHT, Inputs.Action.FIRE)
        addMapKey(Input.Keys.SPACE, Inputs.Action.JUMP)
        addMapKey(Input.Keys.LEFT, Inputs.Action.LOOK_X, false)
        addMapKey(Input.Keys.RIGHT, Inputs.Action.LOOK_X, true)
        addMapKey(Input.Keys.DOWN, Inputs.Action.LOOK_Y, false)
        addMapKey(Input.Keys.UP, Inputs.Action.LOOK_Y, true)
        addMapKey(Input.Keys.A, Inputs.Action.LOOK_X, false)
        addMapKey(Input.Keys.D, Inputs.Action.LOOK_X, true)
        addMapKey(Input.Keys.S, Inputs.Action.LOOK_Y, false)
        addMapKey(Input.Keys.W, Inputs.Action.LOOK_Y, true)
    }
	init {
		if( ! CesDoom.isMobile)
			addKeyboard()

		Log.e(tag, ":::::::::::::::::::::::::::::::::1:"+isButtonPressed(Inputs.Action.START))
		Log.e(tag, ":::::::::::::::::::::::::::::::::2:"+values[Inputs.Action.START])
        Log.e(tag, ":::::::::::::::::::::::::::::::::3:"+Gdx.input.isKeyPressed(mapperKey[AxisKey(Inputs.Action.START, true)]?:-1))
	}


    fun axisMoved(axisCode: Int, value: Float) {
        mapper[axisCode]?.let { axis ->
            if(value.absoluteValue > axisOffset[axis]?:0.1f) {
                values[axis] = if(value < 0) Value.POSITIVE else Value.NEGATIVE
				Log.e(tag, "axisMoved-----axisCode=$axisCode---axis=$axis ------- ${axisOffset[axis]} ------------------- "+values[axis])
            }
            else {
                values[axis] = Value.ZERO
				//Log.e(tag, "axisMoved-------- $axis ------- ${axisOffset[axis]} ------------------- "+values[axis])
            }
        }
    }
    fun povMoved(povCode: Int, value: PovDirection?) {
        //TODO: asociar con Axis en addMap(event: Int, axis: Int, offset: Float, isPovX: Boolean, isPovY: Boolean)
        /*when(value) {
            PovDirection.east ->
            else -> Unit
        }*/
    }
    fun buttonUp(buttonCode: Int) {
        mapper[buttonCode]?.let { button ->
            values[button] = Value.ZERO
        }
    }
    fun buttonDown(buttonCode: Int) {
        mapper[buttonCode]?.let { button ->
            values[button] = Value.POSITIVE
			Log.e(tag, "buttonDown-----------------buttonCode=$buttonCode---------$button=${values[button]}")
        }
    }


    fun isButtonPressed(button: Inputs.Action):Boolean {
		return values[button] == Value.POSITIVE
            || (!CesDoom.isMobile
				&& mapperKey[AxisKey(button, true)] != null
				&& Gdx.input.isKeyPressed(mapperKey[AxisKey(button, true)]!!))
	}
    fun getAxisValue(axis: Inputs.Action):Value {
        var v = values[axis]?:Value.ZERO
		//if(v != Value.ZERO)Log.e(tag, "getAxisValue-------------------------------$axis=$v")
        if(v == Value.ZERO && !CesDoom.isMobile) {
            if(Gdx.input.isKeyPressed(mapperKey[AxisKey(axis, false)]?:-1))
                v = Inputs.Value.NEGATIVE
            else if(Gdx.input.isKeyPressed(mapperKey[AxisKey(axis, true)]?:-1))
                v = Inputs.Value.POSITIVE
        }
        return v
    }
    fun isAxisValuePositive(axis: Inputs.Action):Boolean
            = values[axis] == Value.POSITIVE
            || (!CesDoom.isMobile && Gdx.input.isKeyPressed(mapperKey[AxisKey(axis, true)]?:-1))
    fun isAxisValueNegative(axis: Inputs.Action):Boolean
            = values[axis] == Value.NEGATIVE
            || (!CesDoom.isMobile && Gdx.input.isKeyPressed(mapperKey[AxisKey(axis, false)]?:-1))
}