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

	//TODO:
	//private var inputDelay = 0f
	/*inputDelay+=delta
	if(inputDelay > .250f) {
		inputDelay = 0f
		processInput()
	}*/
	override fun render(delta: Float) {
		processInput()
		gameUI.update(delta)
		gameWorld.render(delta)
		gameUI.render()
	}
	private fun processInput() {
		when {
			Status.gameOver -> gameUI.gameOverWidget.processInput(input.mapper)
			Status.gameWin -> gameUI.gameWinWidget.processInput(input.mapper)
			Status.mainMenu -> Unit//MainMenuScreen ya procesa sus entradas
			Status.paused -> gameUI.pauseWidget.processInput(input.mapper)
			else -> {
				if(input.mapper.isButtonPressed(Inputs.Action.BACK))
					gameUI.pause()//Abrir pauseWidget
			}
		}
		/*
		gameUI.gameWinWidget.isVisible
		gameUI.gameOverWidget.isVisible

		val down = input.mapper.isGoingDown()
		val up = input.mapper.isGoingUp()
		val backward = input.mapper.isGoingBackwards()
		val forward = input.mapper.isGoingForward()

		//TODO: Send message to go to ... WICH widget is on? Pause, GameOver, GameWin ?
		//TODO:
		//TODO
		when {
			input.mapper.isButtonPressed(Inputs.Action.BACK) -> {
				when {
					Status.gameOver -> { gameUI.gameOverWidget.goMenu() }
					Status.gameWin -> { gameUI.gameWinWidget.goMenu() }
					Status.mainMenu -> { }
					Status.paused -> gameUI.pauseWidget.goMenu()
					else -> gameUI.pause()
				}
				Log.e(tag, "isButtonPressed(Inputs.Action.BACK) ---> gameOVer="+Status.gameOver+" / win="+Status.gameWin+"/menu="+Status.mainMenu+" / pause="+Status.paused)
			}
			input.mapper.isButtonPressed(Inputs.Action.EXIT) -> {
				when {
					Status.gameOver -> { gameUI.gameOverWidget.goQuit() }
					Status.gameWin -> { gameUI.gameWinWidget.goQuit() }
					Status.mainMenu -> {  }
					Status.paused -> gameUI.pauseWidget.goQuit()
				}
			}
			input.mapper.isButtonPressed(Inputs.Action.FIRE) -> {
				when {
					Status.gameOver -> {  }
					Status.gameWin -> {  }
					Status.mainMenu -> {  }
					Status.paused -> {  }
				}
			}
			input.mapper.isButtonPressed(Inputs.Action.START) -> {
				Log.e(tag, "input.mapper.isButtonPressed(Inputs.Action.START)")
			}
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
