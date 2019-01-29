package com.cesoft.cesdoom

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
object Status {

	var paused: Boolean = false
	var gameOver: Boolean = false
	var gameWin: Boolean = false
	var mainMenu: Boolean = false
}
