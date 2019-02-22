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
class PlayerInput(val inputMapper: InputMapper, private val inputProcessor: InputProcessor) : ControllerListener {
	companion object {
		val tag: String = PlayerInput::class.java.simpleName
	}

    var controller: Controller? = null
	//var ctrlMappings: CtrMappings? = null
    //var mappedController: MappedController? = null

    //----------------------------------------------------------------------------------------------
    override fun connected(controller: Controller?) {
        this.controller = controller
		//this.ctrlMappings = CtrMappings(inputProcessor)
        //this.mappedController = MappedController(controller, ctrlMappings)//TODO: make non null?
        Log.e(PlayerSystem.tag, "connected:----------*****************************--------------"+controller?.name)
    }
    override fun disconnected(controller: Controller?) {
        this.controller = null
        //this.mappedController = null
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

/*
    //----------------------------------------------------------------------------------------------
    //
    enum class Direccion { NONE, ATRAS, ADELANTE, IZQUIERDA, DERECHA }
    enum class Mirada { NONE, ARRIBA, ABAJO, IZQUIERDA, DERECHA }
    var dxPad: Direccion = Direccion.NONE
    var dyPad: Direccion = Direccion.NONE
    var mxPad: Mirada = Mirada.NONE
    var myPad: Mirada = Mirada.NONE
    //var fire1: Boolean = false
    //var fire2: Boolean = false
    var btnA: Boolean = false
    var btnB: Boolean = false
    var btnX: Boolean = false
    var btnY: Boolean = false
    var btnStart: Boolean = false
	var btnBack: Boolean = false
	var btnGuide: Boolean = false
    var btnUp: Boolean = false
    var btnDown: Boolean = false
    var btnLeft: Boolean = false
    var btnRight: Boolean = false
	var btnBumper: Boolean = false

    //-------------------------------------------------------------------------------------------
    //
    /// Implements ControllerListener
    override fun axisMoved(controller: Controller?, axisCode: Int, value: Float): Boolean {
Log.e(tag, "axisMoved:------------"+controller?.name+" : $axisCode : "
		+when(axisCode) {
			Xbox.L_STICK_VERTICAL_AXIS -> "L_STICK_VERTICAL_AXIS"
			Xbox.R_STICK_VERTICAL_AXIS -> "R_STICK_VERTICAL_AXIS"
			Xbox.R_STICK_HORIZONTAL_AXIS -> "R_STICK_HORIZONTAL_AXIS"
			Xbox.L_STICK_HORIZONTAL_AXIS -> "L_STICK_HORIZONTAL_AXIS"
			else -> "?"
		}
		+" : "+value)
		//+"  isXboxController="+Xbox.isXboxController(controller))

        val dOffset = 0.3f
        val mOffset = 0.3f

        /// Direccion TODO: https://github.com/MrStahlfelge/gdx-controllerutils
		//XBOX 360 For Windows => Xbox.
		//  => Ouya
        if(axisCode == 1) {//Xbox.R_STICK_HORIZONTAL_AXIS) {
            dxPad = when {
                value > +dOffset -> Direccion.DERECHA
                value < -dOffset -> Direccion.IZQUIERDA
                else -> Direccion.NONE
            }
        }
        if(axisCode == 0) {//Xbox.R_STICK_VERTICAL_AXIS) {
            dyPad = when {
                value > +dOffset -> Direccion.ATRAS
                value < -dOffset -> Direccion.ADELANTE
                else -> Direccion.NONE
            }
        }
        /// Mirada
        if(axisCode == 3) {//Xbox.L_STICK_HORIZONTAL_AXIS) {
            mxPad = when {
                value > +mOffset -> Mirada.DERECHA
                value < -mOffset -> Mirada.IZQUIERDA
                else -> Mirada.NONE
            }
        }
        if(axisCode == 2) {//Xbox.L_STICK_VERTICAL_AXIS) {
            myPad = when {
                value > +mOffset -> Mirada.ARRIBA
                value < -mOffset -> Mirada.ABAJO
                else -> Mirada.NONE
            }
        }
		Log.e(tag, "axisMoved:------------myPad=$myPad  mxPad=$mxPad /  dyPad=$dyPad  dxPad=$dxPad")

        return false
    }
    override fun buttonUp(controller: Controller?, buttonCode: Int): Boolean {
        Log.e(PlayerSystem.tag, "buttonUp:0----------------"+controller?.name+" : "+buttonCode)
        when(buttonCode) {
            0 -> btnA = false
			1 -> btnB = false
			2 -> btnX = false
			3 -> btnY = false
			4 -> btnBumper=false//izquierda
			5 -> btnBumper=false//derecha
			6 -> btnGuide=false//select
			7 -> btnStart=false//start
            else -> Log.e(PlayerSystem.tag, "buttonUp:----------------"+controller?.name+" : "+buttonCode)
        }
        //else if(buttonCode == com.badlogic.gdx.controllers.mappings.Xbox.A)
        return false
    }
    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        Log.e(PlayerSystem.tag, "buttonDown:0---------------"+controller?.name+" : "+buttonCode)
        when(buttonCode) {
            0 -> btnA = true
			1 -> btnB = true
			2 -> btnX = true
			3 -> btnY = true
			4 -> btnBumper=true//izquierda
			5 -> btnBumper=true//derecha
			6 -> btnGuide=true//select
			7 -> btnStart=true//start
            else -> Log.e(PlayerSystem.tag, "buttonDown:---------------"+controller?.name+" : "+buttonCode)
        }
        return false
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
*/
}