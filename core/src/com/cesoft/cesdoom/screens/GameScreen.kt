package com.cesoft.cesdoom.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.cesoft.cesdoom.GameWorld
import com.cesoft.cesdoom.Status
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.assets.Sounds
import com.cesoft.cesdoom.components.PlayerComponent
import com.cesoft.cesdoom.systems.RenderSystem
import com.cesoft.cesdoom.ui.GameUI


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameScreen(private val gameUI: GameUI, assets: Assets) : Screen {
	private var gameWorld = GameWorld(gameUI.gameWinWidget, gameUI.gameOverWidget, assets)

	companion object {
		val tag: String = GameScreen::class.java.simpleName
	}
	init {
		Status.paused = false
		Gdx.input.inputProcessor = gameUI.stage
		Gdx.input.isCursorCatched = true
		Sounds.playMusic()
	}

	val render: RenderSystem
		get() = gameWorld.renderSystem

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
		if(PlayerComponent.currentLevel == 0)
			Sounds.stopMusic()
		com.cesoft.cesdoom.util.Log.e("GameScreen", "dispose------------------------------------------------ ${PlayerComponent.currentLevel}")
	}

	override fun show() = Unit
	override fun hide() = Unit
	override fun pause() {
		//Log.e("GameScreen", "pause------------------------------------------------")
		gameWorld.pause()
	}
	override fun resume() {
		gameWorld.resume()
	}
}
