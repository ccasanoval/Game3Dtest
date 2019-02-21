package com.cesoft.cesdoom.util

import com.badlogic.gdx.Gdx

object Log {
    var debugMode: Boolean = true
    fun e(tag: String, msg: String) {
        /*when (Gdx.app.type) {
            ApplicationType.Android -> Unit
            ApplicationType.Desktop -> Unit
            ApplicationType.HeadlessDesktop -> Unit
            ApplicationType.iOS -> Unit
            ApplicationType.WebGL -> Unit
            ApplicationType.Applet -> Unit
        }*/
        if(debugMode || true) {//TODO:CES:  ---------------------------------------------------------------------------------
            //System.err.println("$tag: $msg")
            //System.err.flush()
            Gdx.app.error(tag, msg)
        }

        /*Gdx.app.log("MyTag", "my informative message");
        Gdx.app.error("MyTag", "my error message", exception);
        Gdx.app.debug("MyTag", "my debug message");*/
    }
}