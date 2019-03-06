package com.cesoft.cesdoom.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.controllers.Controllers
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.GameWorld
import com.cesoft.cesdoom.Status
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.assets.Sounds
import com.cesoft.cesdoom.components.PlayerComponent
import com.cesoft.cesdoom.input.Inputs
import com.cesoft.cesdoom.ui.GameUI
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameScreen(game: CesDoom, private val gameUI: GameUI, assets: Assets) : Screen {
	private val input = game.playerInput
	private var gameWorld = GameWorld(gameUI.gameWinWidget, gameUI.gameOverWidget, input.mapper, assets)

	companion object {
		val tag: String = GameScreen::class.java.simpleName
	}
	init {
		Status.paused = false
		Gdx.input.inputProcessor = gameUI.stage
		Controllers.addListener(input)
		Sounds.playMusic()
	}

	override fun render(delta: Float) {
		processInput()
		gameUI.update(delta)
		gameWorld.render(delta)
		gameUI.render()
	}
	private fun processInput() {


		gameUI.gameWinWidget.isVisible
		gameUI.gameOverWidget.isVisible

		val down = input.mapper.isGoingDown()
		val up = input.mapper.isGoingUp()
		val backward = input.mapper.isGoingBackwards()
		val forward = input.mapper.isGoingForward()

		when {
			input.mapper.isButtonPressed(Inputs.Action.BACK) -> {
				//TODO: Send message to go to ... WICH widget is on? Pause, GameOver, GameWin ?
				//gameUI.pause()
				Log.e(tag, "isButtonPressed(Inputs.Action.BACK) ---> gameUI.pause()")
				Log.e(tag, "isButtonPressed(Inputs.Action.BACK) ---> Win=${gameUI.gameWinWidget.isVisible}  Over=${gameUI.gameOverWidget.isVisible}  Pause=${gameUI}")
			}
			input.mapper.isButtonPressed(Inputs.Action.EXIT) -> {
				//TODO: Send message to go to menu
				Log.e(tag, "input.mapper.isButtonPressed(Inputs.Action.EXIT) ---> game.reset2Menu()")
				//game.reset2Menu()
			}
		}
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
