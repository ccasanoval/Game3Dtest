package com.cesoft.cesdoom.input

import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.PovDirection
import com.badlogic.gdx.math.Vector3
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class PlayerInput(val mapper: InputMapper) : ControllerListener {
	companion object {
		val tag: String = PlayerInput::class.java.simpleName
	}

    private var controller: Controller? = null

    //TODO: mejora esto de android TV
    var center
        get() = mapper.center
        set(value) { mapper.center = value }
    var left
        get() = mapper.left
        set(value) { mapper.left = value }
    var right
        get() = mapper.right
        set(value) { mapper.right = value }
    var up
        get() = mapper.up
        set(value) { mapper.up = value }
    var down
        get() = mapper.down
        set(value) { mapper.down = value }

    //----------------------------------------------------------------------------------------------
    override fun connected(controller: Controller?) {
        this.controller = controller
        //Log.e(tag, "connected:----------*****************************--------------"+controller?.name)
    }
    override fun disconnected(controller: Controller?) {
        this.controller = null
        //Log.e(tag, "disconnected:---------*************************---------------"+controller?.name)
    }
    ///
    override fun axisMoved(controller: Controller?, axisCode: Int, value: Float): Boolean {
        Log.e(tag, "axisMoved:------------"+controller?.name+" : $axisCode : $value")
        mapper.axisMoved(axisCode, value)
        return false
    }
    ///
    override fun buttonUp(controller: Controller?, buttonCode: Int): Boolean {
        //Log.e(tag, "buttonUp:----------------"+controller?.name+" : "+buttonCode)
        mapper.buttonUp(buttonCode)
        return false
    }
    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        Log.e(tag, "buttonDown:----------------"+controller?.name+" : "+buttonCode)
        mapper.buttonDown(buttonCode)
        return false
    }
    ///
    override fun povMoved(controller: Controller?, povCode: Int, value: PovDirection?): Boolean {
        //Log.e(tag, "povMoved:------------"+controller?.name+" : "+povCode+" : "+value)
        mapper.povMoved(povCode, value)
        return false
    }
    ///
    override fun accelerometerMoved(controller: Controller?, accelerometerCode: Int, value: Vector3?): Boolean {
        //Log.e(tag, "accelerometerMoved:------------"+controller?.name+" : "+accelerometerCode+" : "+value)
        return false
    }
    override fun ySliderMoved(controller: Controller?, sliderCode: Int, value: Boolean): Boolean {
        //Log.e(tag, "ySliderMoved:------------"+controller?.name+" : "+sliderCode+" : "+value)
        return false
    }
    override fun xSliderMoved(controller: Controller?, sliderCode: Int, value: Boolean): Boolean {
        //Log.e(tag, "xSliderMoved:------------"+controller?.name+" : "+sliderCode+" : "+value)
        return false
    }

}