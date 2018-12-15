package com.cesoft.cesdoom.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.GameWorld
import com.cesoft.cesdoom.Settings
import com.cesoft.cesdoom.systems.RenderSystem


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameScreen(private val game: CesDoom) : Screen {
	private var gameWorld: GameWorld = GameWorld(game)

	companion object {
		val tag: String = GameScreen::class.java.simpleName
	}
	init {
		Settings.paused = false
		Gdx.input.inputProcessor = game.gameUI.stage
		Gdx.input.isCursorCatched = true
	}

	val render: RenderSystem
		get() = gameWorld.renderSystem

	override fun render(delta: Float) {
		game.gameUI.update(delta)
		gameWorld.render(delta)
		game.gameUI.render()
	}

	override fun resize(width: Int, height: Int) {
		game.gameUI.resize(width, height)
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
