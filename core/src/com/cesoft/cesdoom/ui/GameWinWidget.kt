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
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.Status


////////////////////////////////////////////////////////////////////////////////////////////////////
// TODO: Usar game over modificado ???!!!
class GameWinWidget(stage: Stage) : Actor() {
	private var image: Image = Image(Texture(Gdx.files.internal("data/gameWin.png")))
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
		btnRestart = TextButton(CesDoom.instance.assets.getString(Assets.RECARGAR), CesDoom.instance.assets.skin)
		btnRestart.label.setFontScale(2f)
		btnMenu = TextButton(CesDoom.instance.assets.getString(Assets.MENU), CesDoom.instance.assets.skin)
		btnMenu.label.setFontScale(2f)
		btnQuit = TextButton(Assets.SALIR, CesDoom.instance.assets.skin)
		btnQuit.label.setFontScale(2f)
		//
		configureWidgets()
		setListeners()
		//
		setSize(CesDoom.VIRTUAL_WIDTH-100, CesDoom.VIRTUAL_HEIGHT-120)
		setPosition((CesDoom.VIRTUAL_WIDTH - width)/2, (CesDoom.VIRTUAL_HEIGHT - height)/2)
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
				CesDoom.instance.reset()
				reanudar()
			}
		})
		btnMenu.addListener(object : ClickListener() {
			override fun clicked(inputEvent: InputEvent?, x: Float, y: Float) {
				reanudar()
				CesDoom.instance.reset2Menu()
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
		Status.paused = true
		Status.gameWin = true
	}
	private fun reanudar()
	{
		window.remove()
		Gdx.input.isCursorCatched = true
		Status.paused = false
		Status.gameWin = false
		Status.gameOver = false
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
	fun show() {
		pausar()
	}
}
