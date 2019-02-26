package com.cesoft.cesdoom.input

import com.badlogic.gdx.Input


////////////////////////////////////////////////////////////////////////////////////////////////////
// TODO: Screen to enter config and save to settings
object InputMapperFactory {
/*
    private fun addKeyboard(im: InputMapper) {
        im.addMapKey(Input.Keys.ENTER, Inputs.START)
        im.addMapKey(Input.Keys.DEL, Inputs.BACK)
        im.addMapKey(Input.Keys.X, Inputs.BACK)
        im.addMapKey(Input.Keys.CONTROL_LEFT, Inputs.FIRE)
        im.addMapKey(Input.Keys.CONTROL_RIGHT, Inputs.FIRE)
        im.addMapKey(Input.Keys.SPACE, Inputs.JUMP)
        im.addMapKey(Input.Keys.LEFT, Inputs.LOOK_X, false)
        im.addMapKey(Input.Keys.RIGHT, Inputs.LOOK_X, true)
        im.addMapKey(Input.Keys.DOWN, Inputs.LOOK_Y, false)
        im.addMapKey(Input.Keys.UP, Inputs.LOOK_Y, true)
        im.addMapKey(Input.Keys.A, Inputs.LOOK_X, false)
        im.addMapKey(Input.Keys.D, Inputs.LOOK_X, true)
        im.addMapKey(Input.Keys.S, Inputs.LOOK_Y, false)
        im.addMapKey(Input.Keys.W, Inputs.LOOK_Y, true)
    }

    //______________________________________________________________________________________________
    fun getCes(offsetX: Float=.6f, offsetY: Float=.6f) : InputMapper {
        val CES_START = 108
        val CES_R_STICK = 107
        val CES_L_STICK = 106
        val CES_R_FIRE = 103
        val CES_L_FIRE = 102
        val CES_A = 96
        val CES_B = 97
        val CES_X = 99
        val CES_Y = 100
        val CES_AXIS_R_H = 4
        val CES_AXIS_R_V = 5
        val CES_AXIS_L_H = 0
        val CES_AXIS_L_V = 1

        val im = InputMapper()
        //im.addMap(CES_SELECT, Inputs.MENU)
        im.addMap(CES_START, Inputs.START)
        im.addMap(CES_A, Inputs.START)
        im.addMap(CES_B, Inputs.BACK)
        im.addMap(CES_X, Inputs.BACK)
        im.addMap(CES_Y, Inputs.JUMP)

        im.addMap(CES_R_FIRE, Inputs.FIRE)
        im.addMap(CES_L_FIRE, Inputs.FIRE)
        im.addMap(CES_R_STICK, Inputs.FIRE)
        im.addMap(CES_L_STICK, Inputs.FIRE)

        im.addMap(CES_AXIS_R_H, Inputs.LOOK_X, offsetX)
        im.addMap(CES_AXIS_R_V, Inputs.LOOK_Y, offsetY)

        im.addMap(CES_AXIS_L_H, Inputs.MOVE_X, offsetX)
        im.addMap(CES_AXIS_L_V, Inputs.MOVE_Y, offsetY)

        addKeyboard(im)
        return im
    }


    //______________________________________________________________________________________________
    fun getWin(offsetX: Float=.6f, offsetY: Float=.6f) : InputMapper {
        val WIN_BTN_A = 0
        val WIN_BTN_B = 1
        val WIN_BTN_X = 2
        val WIN_BTN_Y = 3
        val WIN_BTN_FIRE1 = 4
        val WIN_BTN_FIRE2 = 5
        //val WIN_BTN_SELECT = 6
        val WIN_BTN_START = 7
        val WIN_AXIS_L_H = 1
        val WIN_AXIS_L_V = 0
        val WIN_AXIS_R_H = 3
        val WIN_AXIS_R_V = 2

        val im = InputMapper()
        //im.addMap(WIN_BTN_SELECT, Inputs.MENU)
        im.addMap(WIN_BTN_START, Inputs.START)
        im.addMap(WIN_BTN_A, Inputs.START)
        im.addMap(WIN_BTN_B, Inputs.BACK)
        im.addMap(WIN_BTN_X, Inputs.BACK)
        im.addMap(WIN_BTN_Y, Inputs.JUMP)

        im.addMap(WIN_BTN_FIRE1, Inputs.FIRE)
        im.addMap(WIN_BTN_FIRE2, Inputs.FIRE)

        im.addMap(WIN_AXIS_R_H, Inputs.LOOK_X, offsetX)
        im.addMap(WIN_AXIS_R_V, Inputs.LOOK_Y, offsetY)

        im.addMap(WIN_AXIS_L_H, Inputs.MOVE_X, offsetX)
        im.addMap(WIN_AXIS_L_V, Inputs.MOVE_Y, offsetY)

        addKeyboard(im)
        return im
    }


    //______________________________________________________________________________________________
    fun getAnd(offsetX: Float=.6f, offsetY: Float=.6f) : InputMapper {
        val KEYCODE_BUTTON_A      =96
        val KEYCODE_BUTTON_B      =97
        val KEYCODE_BUTTON_X      =99
        val KEYCODE_BUTTON_Y      =100
        val KEYCODE_BUTTON_R1     =103
        val KEYCODE_BUTTON_R2     =105
        val KEYCODE_BUTTON_SELECT =109
        val KEYCODE_BUTTON_START  =108
        val AXIS_X =0     // -1.0 (left) to 1.0 (right)
        val AXIS_Y =1     // -1.0 (up) to 1.0 (down).
        val KEYCODE_BUTTON_THUMBL=106
        val AXIS_RZ=14    // -1.0 (counter-clockwise) to 1.0 (clockwise)
        val AXIS_Z =11    // -1.0 (high) to 1.0 (low)
        val KEYCODE_BUTTON_THUMBR=107

        val im = InputMapper()
        //im.addMap(KEYCODE_BUTTON_SELECT, Inputs.MENU)
        im.addMap(KEYCODE_BUTTON_START, Inputs.START)
        im.addMap(KEYCODE_BUTTON_A, Inputs.START)
        im.addMap(KEYCODE_BUTTON_B, Inputs.BACK)
        im.addMap(KEYCODE_BUTTON_X, Inputs.BACK)
        im.addMap(KEYCODE_BUTTON_Y, Inputs.JUMP)

        im.addMap(KEYCODE_BUTTON_R1, Inputs.FIRE)
        im.addMap(KEYCODE_BUTTON_R2, Inputs.FIRE)
        im.addMap(KEYCODE_BUTTON_THUMBL, Inputs.FIRE)
        im.addMap(KEYCODE_BUTTON_THUMBR, Inputs.FIRE)

        im.addMap(AXIS_RZ, Inputs.LOOK_X, offsetX)
        im.addMap(AXIS_Z, Inputs.LOOK_Y, offsetY)

        im.addMap(AXIS_X, Inputs.MOVE_X, offsetX)
        im.addMap(AXIS_Y, Inputs.MOVE_Y, offsetY)

        addKeyboard(im)
        return im
    }
*/

}