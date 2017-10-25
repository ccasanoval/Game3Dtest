package com.cesoft.cesgame

import com.badlogic.gdx.Application
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.cesoft.cesgame.UI.GameUI
import com.cesoft.cesgame.screens.GameScreen
import com.cesoft.cesgame.screens.MainMenuScreen

////////////////////////////////////////////////////////////////////////////////////////////////////
// DESKTOP DEPLOY : gradlew desktop:dist --> cesgame2\desktop\build\libs
// DESKTOP RUN    : desktop:run
//
//https://xoppa.github.io/blog/using-the-libgdx-3d-physics-bullet-wrapper-part1/
class CesGame : ApplicationAdapter() {

	private var screen: Screen? = null
	private lateinit var assets: Assets
	lateinit var gameUI: GameUI

	//______________________________________________________________________________________________
	override fun create() {
		isMobile = Gdx.app.type == Application.ApplicationType.Android

		assets = Assets()
		gameUI = GameUI(this, assets)
		Settings.load()
		Gdx.input.isCatchBackKey = true
		setScreen(MainMenuScreen(this, assets))
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
	fun delScreen()
	{
		if(this.screen != null) {
			this.screen!!.hide()
			this.screen!!.dispose()
		}
	}
	//______________________________________________________________________________________________
	fun setScreen(screen: Screen) {
		this.screen = screen
		if(this.screen != null) {
			this.screen!!.show()
			this.screen!!.resize(Gdx.graphics.width, Gdx.graphics.height)
		}
	}
	//______________________________________________________________________________________________
	fun reset()
	{
		delScreen()
		setScreen(GameScreen(gameUI))
	}

	//______________________________________________________________________________________________
	override fun dispose() {
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
