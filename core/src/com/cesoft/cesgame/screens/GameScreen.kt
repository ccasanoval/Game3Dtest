package com.cesoft.cesgame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.cesoft.cesgame.GameWorld
import com.cesoft.cesgame.Settings
import com.cesoft.cesgame.UI.GameUI


////////////////////////////////////////////////////////////////////////////////////////////////////
// TODO: peta al recargar esto
class GameScreen(private var gameUI: GameUI) : Screen {
	//private var gameUI: GameUI = GameUI(game, assets)
	private var gameWorld: GameWorld = GameWorld(gameUI)

	init {
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
		System.err.println("GameScreen:dispose:----------------------------------------")
	}

	override fun show() {}
	override fun pause() {}
	override fun resume() {}
	override fun hide() {}
}
