package com.cesoft.cesdoom.util

import com.badlogic.gdx.Gdx

object Log {
    var debugMode: Boolean = true
    fun e(tag: String, msg: String) {
        if(debugMode) {
            Gdx.app.error(tag, msg)
        }
    }
}