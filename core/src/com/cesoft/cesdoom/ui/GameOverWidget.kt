package com.cesoft.cesdoom.ui

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
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.Status
import com.cesoft.cesdoom.components.PlayerComponent
import com.cesoft.cesdoom.input.Inputs
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameOverWidget(private val game: CesDoom, stage: Stage, assets: Assets) : Actor() {
	private val mapper = game.playerInput.mapper
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
		btnQuit = TextButton(assets.getString(Assets.SALIR), assets.skin)
		btnQuit.label.setFontScale(2f)
		//
		configureWidgets()
		setListeners()
		//
		setSize(CesDoom.VIRTUAL_WIDTH-100, CesDoom.VIRTUAL_HEIGHT-120)
		setPosition((CesDoom.VIRTUAL_WIDTH - width)/2, (CesDoom.VIRTUAL_HEIGHT - height)/2)

	}

	private fun configureWidgets() {
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
				exit()
                game.reset()
			}
		})
		btnMenu.addListener(object : ClickListener() {
			override fun clicked(inputEvent: InputEvent?, x: Float, y: Float) {
				exit()
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
	fun show() {
		stage.addActor(window)
		Gdx.input.isCursorCatched = false
		Status.paused = true
		Status.gameOver = true
		game.playServices?.submitScore(PlayerComponent.score)
	}
	private fun exit() {
		window.remove()
		Gdx.input.isCursorCatched = true
		Status.paused = false
		Status.gameOver = false
		Status.gameWin = false
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
	private var delay = 0f
	override fun act(delta: Float) {
		super.act(delta)
		delay += delta
		if(delay < .250)return
		Log.e("GameOverWidget", "act----------------------------------------")
		delay = 0f
		when {
			mapper.isButtonPressed(Inputs.Action.START) -> restart()
			mapper.isButtonPressed(Inputs.Action.BACK) -> toMenu()
			mapper.isButtonPressed(Inputs.Action.EXIT) -> exitApp()
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
}
