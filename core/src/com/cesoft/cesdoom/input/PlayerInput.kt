package com.cesoft.cesdoom.input

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.PovDirection
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.systems.PlayerSystem
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class PlayerInput(val inputMapper: InputMapper) : ControllerListener {
	companion object {
		val tag: String = PlayerInput::class.java.simpleName
	}

    private var controller: Controller? = null

    //----------------------------------------------------------------------------------------------
    override fun connected(controller: Controller?) {
        this.controller = controller
        Log.e(PlayerSystem.tag, "connected:----------*****************************--------------"+controller?.name)
    }
    override fun disconnected(controller: Controller?) {
        this.controller = null
        Log.e(PlayerSystem.tag, "disconnected:---------*************************---------------"+controller?.name)
    }
    ///
    override fun axisMoved(controller: Controller?, axisCode: Int, value: Float): Boolean {
        Log.e(tag, "axisMoved:------------"+controller?.name+" : $axisCode : $value")
        inputMapper.axisMoved(axisCode, value)
        return false
    }
    ///
    override fun buttonUp(controller: Controller?, buttonCode: Int): Boolean {
        Log.e(PlayerSystem.tag, "buttonUp:----------------"+controller?.name+" : "+buttonCode)
        inputMapper.buttonUp(buttonCode)
        return false
    }
    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        Log.e(PlayerSystem.tag, "buttonDown:----------------"+controller?.name+" : "+buttonCode)
        inputMapper.buttonDown(buttonCode)
        return false
    }
    ///
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

}