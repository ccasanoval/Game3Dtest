package com.cesoft.cesdoom.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.GameWorld
import com.cesoft.cesdoom.Status
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.assets.Sounds
import com.cesoft.cesdoom.components.PlayerComponent
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
		//processInput()
		gameUI.update(delta)
		gameWorld.render(delta)
		gameUI.render()
	}
	/*private fun processInput() {
		if(input.mapper.isButtonPressed(Inputs.Action.BACK))Log.e(tag, "BACK------------------------------------------------------------")
		if(input.mapper.isButtonPressed(Inputs.Action.LOOK_X))Log.e(tag, "LOOK_X------------------------------------------------------------")
		if(input.mapper.isButtonPressed(Inputs.Action.LOOK_Y))Log.e(tag, "LOOK_Y------------------------------------------------------------")
		if(input.mapper.isButtonPressed(Inputs.Action.MOVE_X))Log.e(tag, "MOVE_X------------------------------------------------------------")
		if(input.mapper.isButtonPressed(Inputs.Action.MOVE_Y))Log.e(tag, "MOVE_Y------------------------------------------------------------")
		if(input.mapper.isButtonPressed(Inputs.Action.FIRE))Log.e(tag, "FIRE------------------------------------------------------------")

		when {
			Status.gameOver -> gameUI.gameOverWidget.processInput(input.mapper)
			Status.gameWin -> gameUI.gameWinWidget.processInput(input.mapper)
			Status.mainMenu -> Unit//MainMenuScreen ya procesa sus entradas
			Status.paused -> gameUI.pauseWidget.processInput(input.mapper)
			else -> {
				//if(input.mapper.isButtonPressed(Inputs.Action.BACK))
					//gameUI.pause()//Abrir pauseWidget
			}
		}
	}*/

	override fun resize(width: Int, height: Int) {
		gameUI.resize(width, height)
		gameWorld.resize(width, height)
	}

	override fun dispose() {
		gameWorld.dispose()
		if(PlayerComponent.currentLevel == 0)
			Sounds.stopMusic()
	}

	override fun show() {
		Gdx.input.isCursorCatched = true
	}
	override fun hide() {
		Gdx.input.isCursorCatched = false
	}
	override fun pause() {
		gameWorld.pause()
	}
	override fun resume() {
		gameWorld.resume()
	}
}
