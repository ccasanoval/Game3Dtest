package com.cesoft.cesdoom.input

import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.cesoft.cesdoom.ui.Styles
import com.cesoft.cesdoom.util.Log

class GamepadUI(private val mapper: InputMapper) {
    private var currentFocus = ButtonFocus.NONE
    private enum class ButtonFocus {
        NONE, RESTART, MENU, QUIT
    }
    private var btnRestart : TextButton? = null
    private var btnMenu : TextButton? = null
    private var btnQuit : TextButton? = null
    fun setButtons(btnRestart : TextButton?, btnMenu : TextButton?, btnQuit: TextButton?) {
        this.btnMenu = btnMenu
        this.btnRestart = btnRestart
        this.btnQuit = btnQuit
    }
    private var funRestart: (() -> Unit)? = null
    private var funMenu: (() -> Unit)? = null
    private var funQuit: (() -> Unit)? = null
    fun setFunctions(funRestart: (() -> Unit)?, funMenu: (() -> Unit)?, funQuit: (() -> Unit)?) {
        this.funRestart = funRestart
        this.funMenu = funMenu
        this.funQuit = funQuit
    }
    private var funBack: (() -> Unit)? = null
    fun setFunctionBack(funBack: (() -> Unit)?) {
        this.funBack = funBack
    }

    private var inputDelay = 0f
    fun processInput(delta: Float) {
        if(btnMenu?.stage == null)return
        inputDelay+=delta
        if(inputDelay < .150f)return
        inputDelay = 0f

        if(mapper.isButtonPressed(Inputs.Action.Start)
                || mapper.isButtonPressed(Inputs.Action.Exit)
                || mapper.isButtonPressed(Inputs.Action.Back)) {
            currentFocus = ButtonFocus.MENU
            funBack
        }
        updateFocusSelection()
        updateFocusColor()
        if(mapper.isButtonPressed(Inputs.Action.Fire)) {
            processSelectedButton()
        }
    }
    private fun updateFocusSelection() {
        val backwards = mapper.isGoingBackwards() || mapper.isGoingUp()
        val forward = mapper.isGoingForward() || mapper.isGoingDown()
        if(forward) {
            when(currentFocus) {
                ButtonFocus.NONE -> currentFocus = ButtonFocus.RESTART
                ButtonFocus.RESTART -> currentFocus = ButtonFocus.MENU
                ButtonFocus.MENU -> currentFocus = ButtonFocus.QUIT
                else -> Unit
            }
        }
        else if(backwards) {
            when(currentFocus) {
                ButtonFocus.NONE -> currentFocus = ButtonFocus.QUIT
                ButtonFocus.QUIT -> currentFocus = ButtonFocus.MENU
                ButtonFocus.MENU -> currentFocus = ButtonFocus.RESTART
                else -> Unit
            }
        }
    }
    private fun updateFocusColor() {
        if(btnMenu?.color?.a != 0f) {
            btnMenu?.color = Styles.colorNormal1
            btnRestart?.color = Styles.colorNormal1
            btnQuit?.color = Styles.colorNormal1
        }
        when(currentFocus) {
            ButtonFocus.NONE -> Unit
            ButtonFocus.RESTART -> btnRestart?.color = Styles.colorSelected1
            ButtonFocus.MENU -> btnMenu?.color = Styles.colorSelected1
            ButtonFocus.QUIT -> btnQuit?.color = Styles.colorSelected1
        }
    }
    private fun processSelectedButton() {
        Log.e("aaa", "processSelectedButton-----------------------------------$currentFocus-----------")
        when(currentFocus) {
            ButtonFocus.NONE -> Unit
            ButtonFocus.RESTART -> funRestart?.invoke()
            ButtonFocus.MENU -> funMenu?.invoke()
            ButtonFocus.QUIT -> funQuit?.invoke()
        }
    }
}