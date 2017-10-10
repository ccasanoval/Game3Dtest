package com.cesoft.cesgame.UI

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
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
	//private var closeDialog: TextButton
	private var restartButton: TextButton
	private var quitButton: TextButton

	//______________________________________________________________________________________________
	init {
		super.setStage(stage)
		val assets = Assets()
		val ws = Window.WindowStyle()
		ws.titleFont = BitmapFont()
		ws.titleFontColor = Color.BLUE
		window = Window("", ws)
		//
		//closeDialog = TextButton("X", assets.skin)
		restartButton = TextButton("Recargar", assets.skin)
		quitButton = TextButton("Salir", assets.skin)
		restartButton.label.setFontScale(2f)
		quitButton.label.setFontScale(2f)
		assets.dispose()
		//
		configureWidgets()
		setListeners()
	}

	//______________________________________________________________________________________________
	private fun configureWidgets() {
		//window.titleTable.add<TextButton>(closeDialog).width(64f).height(64f).pad(10f)//.height(window.padTop+10f)
		window.add<TextButton>(restartButton).width(170f).height(60f)
		window.row().pad(20f)
		window.add<TextButton>(quitButton).width(170f).height(60f)
	}

	//______________________________________________________________________________________________
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

		restartButton.addListener(object : ClickListener() {
			override fun clicked(inputEvent: InputEvent?, x: Float, y: Float) {
				game.delScreen()
				game.setScreen(GameScreen(game))
			}
		})
		quitButton.addListener(object : ClickListener() {
			override fun clicked(inputEvent: InputEvent?, x: Float, y: Float) {
				Gdx.app.exit()
			}
		})
//		closeDialog.addListener(object : ClickListener() {
//			override fun clicked(inputEvent: InputEvent?, x: Float, y: Float) {
//				handleUpdates()
//			}
//		})
	}

	//______________________________________________________________________________________________
	private fun handleUpdates() {
		if(window.stage == null) {
			Gdx.app.error("CESGAME", "-----------------1----------------- OUT 1 ------------------------------")
			//System.err.println("----------------------2------------ OUT 1 ------------------------------")
			stage.addActor(window)
			Gdx.input.isCursorCatched = false
			Settings.paused = true
		}
		else {
			//System.err.println("---------------------1------------- OUT 2 ------------------------------")
			Gdx.app.error("CESGAME", "-----------------2----------------- OUT 2 ------------------------------")
			window.remove()
			Gdx.input.isCursorCatched = true
			Settings.paused = false
		}
	}

	//______________________________________________________________________________________________
	override fun setPosition(x: Float, y: Float) {
		super.setPosition(x, y)
		window.setPosition(
			CesGame.VIRTUAL_WIDTH / 2 - window.width / 2,
			CesGame.VIRTUAL_HEIGHT / 2 - window.height / 2)
	}

	//______________________________________________________________________________________________
	override fun setSize(width: Float, height: Float) {
		super.setSize(width, height)
		//window.setSize(width * 3f, height * 3)
		window.setSize(180f, 180f)
	}
}