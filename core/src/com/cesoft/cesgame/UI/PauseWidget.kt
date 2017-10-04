package com.cesoft.cesgame.UI

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.cesoft.cesgame.Assets
import com.cesoft.cesgame.CesGame
import com.cesoft.cesgame.Settings
import com.cesoft.cesgame.screens.GameScreen


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class PauseWidget(private val game: CesGame, stage: Stage) : Actor() {
	private var window: Window
	private var closeDialog: TextButton
	private var restartButton: TextButton
	private var quitButton: TextButton

	//private var stage1: Stage = stage

	init {
		super.setStage(stage)
		val assets = Assets()
		window = Window("Pause", assets.skin)
		closeDialog = TextButton("X", assets.skin)
		restartButton = TextButton("Restart", assets.skin)
		quitButton = TextButton("Quit", assets.skin)
		//
		configureWidgets()
		setListeners()
	}


	private fun configureWidgets() {
		window.titleTable.add<TextButton>(closeDialog).height(window.padTop)
		window.add<TextButton>(restartButton)
		window.add<TextButton>(quitButton)
	}

	private fun setListeners() {
		super.addListener(object : InputListener() {
			override fun keyDown(event: InputEvent?, keycode: Int): Boolean {
				if(keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
					handleUpdates()
					return true
				}
				return false
			}
		})
		closeDialog.addListener(object : ClickListener() {
			override fun clicked(inputEvent: InputEvent?, x: Float, y: Float) {
				handleUpdates()
			}
		})
		restartButton.addListener(object : ClickListener() {
			override fun clicked(inputEvent: InputEvent?, x: Float, y: Float) {
				game.setScreen(GameScreen(game))
			}
		})
		quitButton.addListener(object : ClickListener() {
			override fun clicked(inputEvent: InputEvent?, x: Float, y: Float) {
				Gdx.app.exit()
			}
		})
	}

	private fun handleUpdates() {
		if(window.stage == null) {
			Gdx.app.error("CESGAME", "-----------------1----------------- OUT 1 ------------------------------")
			System.err.println("----------------------2------------ OUT 1 ------------------------------")
			stage.addActor(window)
			Gdx.input.isCursorCatched = false
			Settings.paused = true
		}
		else {
			System.err.println("---------------------1------------- OUT 2 ------------------------------")
			Gdx.app.error("CESGAME", "-----------------2----------------- OUT 2 ------------------------------")
			window.remove()
			Gdx.input.isCursorCatched = true
			Settings.paused = false
		}
	}

	override fun setPosition(x: Float, y: Float) {
		super.setPosition(x, y)
		window.setPosition(
				CesGame.VIRTUAL_WIDTH / 2 - window.width / 2,
				CesGame.VIRTUAL_HEIGHT / 2 - window.height / 2)
	}

	override fun setSize(width: Float, height: Float) {
		super.setSize(width, height)
		window.setSize(width * 2, height * 2)
	}
}