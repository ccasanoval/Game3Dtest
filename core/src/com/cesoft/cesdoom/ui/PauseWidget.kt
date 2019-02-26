package com.cesoft.cesdoom.ui

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
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.Status
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.input.Inputs


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class PauseWidget(private val game: CesDoom, stage: Stage, assets: Assets) : Actor() {

	private var window: Window
	private var btnRestart: TextButton
	private var btnMenu: TextButton
	private var btnQuit: TextButton

	//______________________________________________________________________________________________
	init {
		super.setStage(stage)
		//
		val ws = Window.WindowStyle()
		ws.titleFont = BitmapFont()
		ws.titleFontColor = Color.BLUE
		window = Window("", ws)
		//
		btnRestart = TextButton(assets.getString(Assets.RECARGAR), assets.skin)
		btnMenu = TextButton(assets.getString(Assets.MENU), assets.skin)
		btnQuit = TextButton(assets.getString(Assets.SALIR), assets.skin)
		btnRestart = TextButton(assets.getString(Assets.RECARGAR), assets.skin)
		btnRestart.label.setFontScale(2f)
		btnMenu = TextButton(assets.getString(Assets.MENU), assets.skin)
		btnMenu.label.setFontScale(2f)
		btnQuit = TextButton(assets.getString(Assets.SALIR), assets.skin)
		btnQuit.label.setFontScale(2f)
		//
		configureWidgets()
		setListeners()
		//
		setSize(0.8f*CesDoom.VIRTUAL_WIDTH, 0.8f*CesDoom.VIRTUAL_HEIGHT)
		setPosition(CesDoom.VIRTUAL_WIDTH - width, CesDoom.VIRTUAL_HEIGHT - height)
	}

	//______________________________________________________________________________________________
	private fun configureWidgets() {
		window.add<TextButton>(btnRestart).width(350f).height(90f)
		window.row().pad(1f)
		window.add<TextButton>(btnMenu).width(300f).height(90f)
		window.row().pad(1f)
		window.add<TextButton>(btnQuit).width(300f).height(90f)
	}

	//______________________________________________________________________________________________
	//TODO: hacer que inputmapper conecte con esto
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
				restart()
			}
		})
		btnMenu.addListener(object : ClickListener() {
			override fun clicked(inputEvent: InputEvent?, x: Float, y: Float) {
				toMenu()
			}
		})
		btnQuit.addListener(object : ClickListener() {
			override fun clicked(inputEvent: InputEvent?, x: Float, y: Float) {
				exitApp()
			}
		})
	}
	//______________________________________________________________________________________________
	override fun act(delta: Float) {
		super.act(delta)
		when {
			game.playerInput.mapper.isButtonPressed(Inputs.Action.BACK) -> exit()
			game.playerInput.mapper.isButtonPressed(Inputs.Action.START) -> restart()
			game.playerInput.mapper.isButtonPressed(Inputs.Action.EXIT) -> exitApp()
		}
	}
	private fun restart() {
		exit()
		game.reset()
	}
	private fun toMenu() {
		exit()
		game.reset2Menu()
	}
	private fun exitApp() {
		Gdx.app.exit()
	}

	//______________________________________________________________________________________________
	private fun handleUpdates() {
		if(Status.gameOver || Status.gameWin)
			return
		if(window.stage == null)
			goIn()
		else
			exit()
	}
	private fun goIn() {
		//TODO: pausar enemy system de creaer mas bichos
		game.pauseGame()
		stage.addActor(window)
		Gdx.input.isCursorCatched = false
		Status.paused = true
	}
	private fun exit() {
		window.remove()
		Gdx.input.isCursorCatched = true
		Status.paused = false
	}

	//______________________________________________________________________________________________
	override fun setPosition(x: Float, y: Float) {
		super.setPosition(x, y)
		window.setPosition(
			(CesDoom.VIRTUAL_WIDTH - window.width) / 2,
			(CesDoom.VIRTUAL_HEIGHT - window.height) / 2)
	}

	//______________________________________________________________________________________________
	override fun setSize(width: Float, height: Float) {
		super.setSize(width, height)
		window.setSize(width, height)
	}

}