package com.cesoft.cesdoom

import com.badlogic.gdx.Application
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.cesoft.cesdoom.UI.GameUI
import com.cesoft.cesdoom.screens.LoadingScreen
import com.cesoft.cesdoom.screens.MainMenuScreen
import com.cesoft.cesdoom.util.Log

////////////////////////////////////////////////////////////////////////////////////////////////////
// DESKTOP DEPLOY : gradlew desktop:dist --> cesgame2\desktop\build\libs
// DESKTOP RUN    : desktop:run
//
//https://xoppa.github.io/blog/using-the-libgdx-3d-physics-bullet-wrapper-part1/
//class CesDoom(private val camera: Camera?) : ApplicationAdapter() {
class CesDoom(debugMode: Boolean) : ApplicationAdapter() {

	private var screen: Screen? = null
	lateinit var assets: Assets
	lateinit var gameUI: GameUI

	init {
	    Log.debugMode = debugMode
	}

	//______________________________________________________________________________________________
	override fun create() {
		isMobile = Gdx.app.type == Application.ApplicationType.Android

		assets = Assets()
		gameUI = GameUI(this, assets)
		Settings.load()
		Gdx.input.isCatchBackKey = true
		setScreen(MainMenuScreen(this, assets, null))//, camera))
	}

	//______________________________________________________________________________________________
	override fun render() {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
		screen!!.render(Gdx.graphics.deltaTime)
	}

	//______________________________________________________________________________________________
	override fun resize(width: Int, height: Int) {
		screen!!.resize(width, height)
	}

	//______________________________________________________________________________________________
	private fun delScreen()
	{
		Log.e("CesDoom", ":delScreen:--------------------------------------------------------")
		screen?.let {
			it.hide()
			it.dispose()
		}
	}
	//______________________________________________________________________________________________
	fun setScreen(screen: Screen) {
		this.screen = screen
		screen.show()
		screen.resize(Gdx.graphics.width, Gdx.graphics.height)
	}
	//______________________________________________________________________________________________
	fun reset() {
		Log.e("CesDoom", "reset:--------------------------------------------------------")
		delScreen()
		setScreen(LoadingScreen(this))
		//<android:hardwareAccelerated="false">
	}
	//______________________________________________________________________________________________
	fun reset2Menu() {
		delScreen()
		setScreen(MainMenuScreen(this, assets, null))//, camera))
	}

	//______________________________________________________________________________________________
	override fun dispose() {
		Log.e("CesDoom", "dispose:--------------------------------------------------------")
		Settings.save()
		delScreen()
		gameUI.dispose()
		assets.dispose()
	}

	//______________________________________________________________________________________________
	companion object {
		val VIRTUAL_WIDTH = 1024f
		val VIRTUAL_HEIGHT = 576f

		var isMobile = false
			private set
	}
}
