package com.cesoft.cesdoom.UI

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.cesoft.cesdoom.Assets
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.Settings
import com.cesoft.cesdoom.components.PlayerComponent


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameOverWidget(private val game: CesDoom, stage: Stage, assets: Assets) : Actor() {
	private var image: Image = Image(Texture(Gdx.files.internal("data/gameOver.png")))
	private var window: Window
	private var btnRestart: TextButton
	private var btnMenu: TextButton
	private var btnQuit: TextButton

	init {
		super.setStage(stage)
		//
		val ws = Window.WindowStyle()
		ws.titleFont = BitmapFont()
		ws.titleFontColor = Color.BLUE
		window = Window("", ws)
		//
		btnRestart = TextButton(assets.getString(Assets.RECARGAR), assets.skin)
		btnRestart.label.setFontScale(2f)
		btnMenu = TextButton(assets.getString(Assets.MENU), assets.skin)
		btnMenu.label.setFontScale(2f)
		btnQuit = TextButton(Assets.SALIR, assets.skin)
		btnQuit.label.setFontScale(2f)
		//
		configureWidgets()
		setListeners()
	}

	private fun configureWidgets() {
		//window.add<Image>(image).width(319f).height(125f)
		val ratio = 125f / 319f
		val width = CesDoom.VIRTUAL_WIDTH*2f/4f
		window.add<Image>(image).width(width).height(ratio * width)
		window.row().pad(.5f)
		window.add<TextButton>(btnRestart).width(350f).height(90f)
		window.row().pad(1f)
		window.add<TextButton>(btnMenu).width(300f).height(90f)
		window.row().pad(1f)
		window.add<TextButton>(btnQuit).width(300f).height(90f)
	}


	//______________________________________________________________________________________________
	private fun setListeners() {
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
	private fun pausar()
	{
		stage.addActor(window)
		Gdx.input.isCursorCatched = false
		Settings.paused = true
		Settings.gameOver = true
	}
	private fun reanudar()
	{
		window.remove()
		Gdx.input.isCursorCatched = true
		Settings.paused = false
		Settings.gameOver = false
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
		super.setSize(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT)
		window.setSize(width, height)
	}

	//______________________________________________________________________________________________
	fun gameOver() {
		pausar()
	}
}
