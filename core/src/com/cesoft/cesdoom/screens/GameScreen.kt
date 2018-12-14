package com.cesoft.cesdoom.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.cesoft.cesdoom.Assets
import com.cesoft.cesdoom.GameWorld
import com.cesoft.cesdoom.Settings
import com.cesoft.cesdoom.UI.GameUI
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameScreen(private val gameUI: GameUI, private val assets: Assets) : Screen {
	private var gameWorld: GameWorld = GameWorld(gameUI, assets)

	companion object {
		val tag: String = GameScreen::class.java.simpleName
	}
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
	}

	override fun show() {}
	override fun pause() {}
	override fun resume() {}
	override fun hide() {}
}
