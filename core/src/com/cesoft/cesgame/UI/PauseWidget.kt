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


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class PauseWidget(private val game: CesGame, stage: Stage, assets: Assets) : Actor() {

	private var window: Window
	//private var closeDialog: TextButton
	private var btnRestart: TextButton
	private var btnQuit: TextButton

	//______________________________________________________________________________________________
	init {
		super.setStage(stage)
		val ws = Window.WindowStyle()
		ws.titleFont = BitmapFont()
		ws.titleFontColor = Color.BLUE
		window = Window("", ws)
		//
		//closeDialog = TextButton("X", assets.skin)
		btnRestart = TextButton(assets.getString(Assets.RECARGAR), assets.skin)
		btnQuit = TextButton(Assets.SALIR, assets.skin)
		btnRestart.label.setFontScale(2f)
		btnQuit.label.setFontScale(2f)
		//
		configureWidgets()
		setListeners()
	}

	//______________________________________________________________________________________________
	private fun configureWidgets() {
		//window.titleTable.add<TextButton>(closeDialog).width(64f).height(64f).pad(10f)//.height(window.padTop+10f)
		window.add<TextButton>(btnRestart).width(370f).height(95f)
		window.row().pad(20f)
		window.add<TextButton>(btnQuit).width(300f).height(95f)
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

		btnRestart.addListener(object : ClickListener() {
			override fun clicked(inputEvent: InputEvent?, x: Float, y: Float) {
				game.reset()
				reanudar()
			}
		})
		btnQuit.addListener(object : ClickListener() {
			override fun clicked(inputEvent: InputEvent?, x: Float, y: Float) {
				//TODO: dispose ???
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
		if(window.stage == null)
			pausar()
		else
			reanudar()
	}
	private fun pausar()
	{
		Gdx.app.error("CESGAME", "-----------------1----------------- PAUSADO ------------------------------")
		//System.err.println("----------------------2------------ REANUDADO ------------------------------")
		stage.addActor(window)
		Gdx.input.isCursorCatched = false
		Settings.paused = true
	}
	private fun reanudar()
	{
		Gdx.app.error("CESGAME", "-----------------2----------------- REANUDADO ------------------------------")
		window.remove()
		Gdx.input.isCursorCatched = true
		Settings.paused = false
	}

	//______________________________________________________________________________________________
	override fun setPosition(x: Float, y: Float) {
		super.setPosition(x, y)
		window.setPosition(
			(CesGame.VIRTUAL_WIDTH - window.width) / 2,
			(CesGame.VIRTUAL_HEIGHT - window.height) / 2)
	}

	//______________________________________________________________________________________________
	override fun setSize(width: Float, height: Float) {
		super.setSize(width, height)
		window.setSize(width, height)
	}
}