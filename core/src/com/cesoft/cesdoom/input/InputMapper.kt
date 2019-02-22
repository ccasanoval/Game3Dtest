package com.cesoft.cesdoom.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.PovDirection
import kotlin.math.absoluteValue
import com.cesoft.cesdoom.input.Inputs.Value

////////////////////////////////////////////////////////////////////////////////////////////////////
//
//TODO: Add keyboard mapping Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)
class InputMapper {

    // GAMEPAD
    private val mapper: MutableMap<Int, Int> = mutableMapOf()       // Evento       -> Constante
    private val values: MutableMap<Int, Value> = mutableMapOf()     // Constante    -> ON, OFF  o  LEFT, CENTER, RIGHT
    private val axisOffset: MutableMap<Int, Float> = mutableMapOf() // Offset para Ejes

    // KEYBOARD
    private data class AxisKey(val axis: Int, val positive: Boolean) : Comparable<AxisKey> {
        override fun compareTo(other: AxisKey): Int {
            return if(axis == other.axis && positive == other.positive) return 0 else 1
        }
    }
    private val mapperKey: MutableMap<AxisKey, Int> = mutableMapOf()// (Constante, Signo) -> Evento

    fun addMap(event: Int, button: Int, offset: Float=0f) {
        mapper[event] = button
        axisOffset[event] = offset
    }
    fun addMapKey(event: Int, button: Int, positive: Boolean=true) {
        mapperKey[AxisKey(button, positive)] = event
    }


    fun axisMoved(axisCode: Int, value: Float) {
        mapper[axisCode]?.let { axis ->
            if(value.absoluteValue > axisOffset[axis]?:999f) {
                values[axis] = if(value > 0) Value.POSITIVE else Value.NEGATIVE
            }
            else {
                values[axis] = Value.ZERO
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
        }
    }


    fun isButtonPressed(button: Int):Boolean
            = values[button] == Value.POSITIVE
            || Gdx.input.isKeyPressed(mapperKey[AxisKey(button, true)]?:-1)
    fun getAxisValue(axis: Int):Value {
        var v = values[axis]?:Value.ZERO
        if(v == Value.ZERO) {
            if(Gdx.input.isKeyPressed(mapperKey[AxisKey(axis, false)]?:-1))
                v = Inputs.Value.NEGATIVE
            else if(Gdx.input.isKeyPressed(mapperKey[AxisKey(axis, true)]?:-1))
                v = Inputs.Value.POSITIVE
        }
        return v
    }
    fun isAxisValuePositive(axis: Int):Boolean
            = values[axis] == Value.POSITIVE
            || Gdx.input.isKeyPressed(mapperKey[AxisKey(axis, true)]?:-1)
    fun isAxisValueNegative(axis: Int):Boolean
            = values[axis] == Value.NEGATIVE
            || Gdx.input.isKeyPressed(mapperKey[AxisKey(axis, false)]?:-1)
}