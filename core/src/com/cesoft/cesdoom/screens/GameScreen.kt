package com.cesoft.cesdoom.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.controllers.Controllers
import com.cesoft.cesdoom.GameWorld
import com.cesoft.cesdoom.Status
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.assets.Sounds
import com.cesoft.cesdoom.components.PlayerComponent
import com.cesoft.cesdoom.input.Inputs
import com.cesoft.cesdoom.input.PlayerInput
import com.cesoft.cesdoom.ui.GameUI


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameScreen(private val playerInput: PlayerInput, private val gameUI: GameUI, assets: Assets) : Screen {
	private var gameWorld = GameWorld(gameUI.gameWinWidget, gameUI.gameOverWidget, playerInput.mapper, assets)

	companion object {
		val tag: String = GameScreen::class.java.simpleName
	}
	init {
		Status.paused = false
		Gdx.input.inputProcessor = gameUI.stage
		Controllers.addListener(playerInput)
		Sounds.playMusic()
	}

	override fun render(delta: Float) {
		processInput()
		gameUI.update(delta)
		gameWorld.render(delta)
		gameUI.render()
	}
	private fun processInput() {
		/*when {
			playerInput.mapper.isButtonPressed(Inputs.Action.BACK) -> pause()
			playerInput.mapper.isButtonPressed(Inputs.Action.EXIT) -> pause()
		}*/
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

	override fun show() {
		Gdx.input.isCursorCatched = true
	}
	override fun hide() {
		Gdx.input.isCursorCatched = false
	}
	override fun pause() {
		//Log.e("GameScreen", "pause------------------------------------------------")
		gameWorld.pause()
	}
	override fun resume() {
		gameWorld.resume()
	}
}
