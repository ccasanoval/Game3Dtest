package com.cesoft.cesdoom.managers

import com.badlogic.gdx.controllers.mappings.Ouya;
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.PovDirection
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.systems.PlayerSystem
import com.cesoft.cesdoom.util.Log

////////////////////////////////////////////////////////////////////////////////////////////////////
//TODO: Joystick!!
//https://github.com/libgdx/libgdx/wiki/Controllers
class PlayerInput : ControllerListener {
	companion object {
		val tag: String = PlayerInput::class.java.simpleName
	}

    //----------------------------------------------------------------------------------------------
    //
    enum class Direccion { NONE, ATRAS, ADELANTE, IZQUIERDA, DERECHA }
    enum class Mirada { NONE, ARRIBA, ABAJO, IZQUIERDA, DERECHA }
    var dxPad: Direccion = Direccion.NONE
    var dyPad: Direccion = Direccion.NONE
    var mxPad: Mirada = Mirada.NONE
    var myPad: Mirada = Mirada.NONE
    var fire1: Boolean = false
    var fire2: Boolean = false
    var btnA: Boolean = false
    var btnB: Boolean = false
    var btnC: Boolean = false
    var btnD: Boolean = false
    var btnMenu: Boolean = false
    var btnUp: Boolean = false
    var btnDown: Boolean = false
    var btnLeft: Boolean = false
    var btnRight: Boolean = false

    //-------------------------------------------------------------------------------------------
    //
    /// Implements ControllerListener
    override fun axisMoved(controller: Controller?, axisCode: Int, value: Float): Boolean {
Log.e(tag, "axisMoved:------------"+controller?.name+" : "+axisCode+" : "+value)

        val dOffset = 0.3f
        val mOffset = 0.3f

        /// Direccion
        if(axisCode == Ouya.AXIS_LEFT_X) {
            dxPad = when {
                value > +dOffset -> Direccion.DERECHA
                value < -dOffset -> Direccion.IZQUIERDA
                else -> Direccion.NONE
            }
        }
        if(axisCode == Ouya.AXIS_LEFT_Y) {
            dyPad = when {
                value > +dOffset -> Direccion.ATRAS
                value < -dOffset -> Direccion.ADELANTE
                else -> Direccion.NONE
            }
        }
        /// Mirada
        if(axisCode == Ouya.AXIS_RIGHT_X) {
            mxPad = when {
                value > +mOffset -> Mirada.DERECHA
                value < -mOffset -> Mirada.IZQUIERDA
                else -> Mirada.NONE
            }
        }
        if(axisCode == Ouya.AXIS_RIGHT_Y) {
            myPad = when {
                value > +mOffset -> Mirada.ARRIBA
                value < -mOffset -> Mirada.ABAJO
                else -> Mirada.NONE
            }
        }
        return false
    }
    override fun buttonUp(controller: Controller?, buttonCode: Int): Boolean {
        Log.e(PlayerSystem.tag, "buttonUp:0----------------"+controller?.name+" : "+buttonCode)
        when(buttonCode) {
            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_R1 -> fire1 = false
            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_R2 -> fire1 = false
            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_R3 -> fire1 = false

            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_L1 -> fire2 = false
            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_L2 -> fire2 = false
            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_L3 -> fire2 = false

            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_DPAD_RIGHT -> btnRight = false
            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_DPAD_LEFT -> btnLeft = false
            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_DPAD_UP -> btnUp = false
            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_DPAD_DOWN -> btnDown = false

            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_MENU -> btnMenu = false

            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_A -> btnA = false
            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_U -> btnB = false
            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_O -> btnC = false
            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_Y -> btnD = false

            else -> Log.e(PlayerSystem.tag, "buttonUp:----------------"+controller?.name+" : "+buttonCode)
        }
        //else if(buttonCode == com.badlogic.gdx.controllers.mappings.Xbox.A)
        return false
    }
    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        Log.e(PlayerSystem.tag, "buttonDown:0---------------"+controller?.name+" : "+buttonCode)
        when(buttonCode) {
            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_R1 -> fire1 = true
            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_R2 -> fire1 = true
            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_R3 -> fire1 = true

            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_L1 -> fire2 = true
            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_L2 -> fire2 = true
            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_L3 -> fire2 = true

            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_DPAD_RIGHT -> btnRight = true
            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_DPAD_LEFT -> btnLeft = true
            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_DPAD_UP -> btnUp = true
            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_DPAD_DOWN -> btnDown = true

            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_MENU -> btnMenu = true

            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_A -> btnA = true
            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_U -> btnB = true
            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_O -> btnC = true
            com.badlogic.gdx.controllers.mappings.Ouya.BUTTON_Y -> btnD = true
            else -> Log.e(PlayerSystem.tag, "buttonDown:---------------"+controller?.name+" : "+buttonCode)
        }
        return false
    }
    ////
    override fun connected(controller: Controller?) {
        Log.e(PlayerSystem.tag, "connected:------------"+controller?.name)
    }
    override fun disconnected(controller: Controller?) {
        Log.e(PlayerSystem.tag, "disconnected:------------"+controller?.name)
    }
    ////
    override fun accelerometerMoved(controller: Controller?, accelerometerCode: Int, value: Vector3?): Boolean {
        Log.e(PlayerSystem.tag, "accelerometerMoved:------------"+controller?.name+" : "+accelerometerCode+" : "+value)
        return false
    }
    override fun ySliderMoved(controller: Controller?, sliderCode: Int, value: Boolean): Boolean {
        Log.e(PlayerSystem.tag, "ySliderMoved:------------"+controller?.name+" : "+sliderCode+" : "+value)
        return false
    }
    override fun xSliderMoved(controller: Controller?, sliderCode: Int, value: Boolean): Boolean {
        Log.e(PlayerSystem.tag, "xSliderMoved:------------"+controller?.name+" : "+sliderCode+" : "+value)
        return false
    }
    override fun povMoved(controller: Controller?, povCode: Int, value: PovDirection?): Boolean {
        Log.e(PlayerSystem.tag, "povMoved:------------"+controller?.name+" : "+povCode+" : "+value)
        return false
    }
    //////////

}