package com.cesoft.cesgame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.cesoft.cesgame.CesGame
import com.cesoft.cesgame.GameWorld
import com.cesoft.cesgame.Settings
import com.cesoft.cesgame.UI.GameUI


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameScreen(game: CesGame) : Screen {
	private var gameUI: GameUI = GameUI(game)
	private var gameWorld: GameWorld

	init {
		gameWorld = GameWorld(gameUI)
		Settings.paused = false
		Gdx.input.inputProcessor = gameUI.stage
		Gdx.input.isCursorCatched = true
	}

	override fun render(delta: Float) {
		gameUI.update(delta)
		gameWorld.render(delta)
		gameUI.render()
	}

	override fun resize(width: Int, height: Int) {
		gameUI.resize(width, height)
		gameWorld.resize(width, height)
	}

	override fun dispose() {
		gameWorld.dispose()
		gameUI.dispose()
	}

	override fun show() {}
	override fun pause() {}
	override fun resume() {}
	override fun hide() {}
}
