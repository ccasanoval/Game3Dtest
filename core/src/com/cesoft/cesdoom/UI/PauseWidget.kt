package com.cesoft.cesdoom.UI

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
import com.cesoft.cesdoom.Assets
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.Settings


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
		val ws = Window.WindowStyle()
		ws.titleFont = BitmapFont()
		ws.titleFontColor = Color.BLUE
		window = Window("", ws)
		//
		btnRestart = TextButton(assets.getString(Assets.RECARGAR), assets.skin)
		btnMenu = TextButton(assets.getString(Assets.MENU), assets.skin)
		btnQuit = TextButton(Assets.SALIR, assets.skin)
		btnRestart.label.setFontScale(2f)
		btnMenu.label.setFontScale(2f)
		btnQuit.label.setFontScale(2f)
		//
		configureWidgets()
		setListeners()
	}

	//______________________________________________________________________________________________
	private fun configureWidgets() {
		//window.titleTable.add<TextButton>(closeDialog).width(64f).height(64f).pad(10f)//.height(window.padTop+10f)
		window.add<TextButton>(btnRestart).width(370f).height(90f)
		window.row().pad(5f)
		window.add<TextButton>(btnMenu).width(300f).height(90f)
		window.row().pad(5f)
		window.add<TextButton>(btnQuit).width(300f).height(90f)
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
		btnMenu.addListener(object : ClickListener() {
			override fun clicked(inputEvent: InputEvent?, x: Float, y: Float) {
				reanudar()
				game.reset2Menu()
			}
		})
		btnQuit.addListener(object : ClickListener() {
			override fun clicked(inputEvent: InputEvent?, x: Float, y: Float) {
				Gdx.app.exit()
			}
		})
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
		stage.addActor(window)
		Gdx.input.isCursorCatched = false
		Settings.paused = true
	}
	private fun reanudar()
	{
		window.remove()
		Gdx.input.isCursorCatched = true
		Settings.paused = false
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