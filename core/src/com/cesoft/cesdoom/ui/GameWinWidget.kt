package com.cesoft.cesdoom.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.Status
import com.cesoft.cesdoom.input.GamepadUI
import de.golfgl.gdx.controllers.ControllerMenuStage


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameWinWidget(
		private val game: CesDoom,
		private val stage: ControllerMenuStage,
		assets: Assets) {

	companion object {
		private val tag: String = GameWinWidget::class.java.simpleName
	}

	private val image: Image = Image(Texture(Gdx.files.internal("data/gameWin.png")))
	private val btnRestart = TextButton(assets.getString(Assets.NEXT_LEVEL), assets.skin)
	private val btnMenu = TextButton(assets.getString(Assets.MENU), assets.skin)
	private val btnQuit = TextButton(assets.getString(Assets.SALIR), assets.skin)
	private val gamepadUI = GamepadUI(game.playerInput.mapper)

	init {
		configureWidgets()
		setListeners()
	}

	private fun configureWidgets() {
		btnRestart.label.setFontScale(2f)
		btnMenu.label.setFontScale(2f)
		btnQuit.label.setFontScale(2f)

		val ratio = 125f / 319f
		val width = CesDoom.VIRTUAL_WIDTH*2f/4f
		image.setSize(width, ratio * width)
		btnRestart.setSize(375f, 90f)
		btnMenu.setSize(300f, 90f)
		btnQuit.setSize(300f, 90f)

		var y = CesDoom.VIRTUAL_HEIGHT * 0.55f
		image.setPosition((CesDoom.VIRTUAL_WIDTH-image.width)/2, y)
		y -= image.height/3 +2
		btnRestart.setPosition((CesDoom.VIRTUAL_WIDTH-btnRestart.width)/2, y)
		y -= btnRestart.height + 2
		btnMenu.setPosition((CesDoom.VIRTUAL_WIDTH-btnMenu.width)/2, y)
		y -= btnMenu.height + 2
		btnQuit.setPosition((CesDoom.VIRTUAL_WIDTH-btnQuit.width)/2, y)
	}

	private fun setListeners() {
		btnRestart.addListener(object : ClickListener() {
			override fun clicked(inputEvent: InputEvent?, x: Float, y: Float) {
				goRestart()
			}
		})
		btnMenu.addListener(object : ClickListener() {
			override fun clicked(inputEvent: InputEvent?, x: Float, y: Float) {
				goMenu()
			}
		})
		btnQuit.addListener(object : ClickListener() {
			override fun clicked(inputEvent: InputEvent?, x: Float, y: Float) {
				goQuit()
			}
		})

		gamepadUI.setButtons(btnRestart, btnMenu, btnQuit)
		gamepadUI.setFunctions({ goRestart() }, { goMenu() }, { goQuit() })
		gamepadUI.setFunctionBack { goBack() }
	}
	fun processInput(delta: Float) {
		gamepadUI.processInput(delta)
	}
	//______________________________________________________________________________________________
	private fun goRestart() {
        game.nextLevel()
		game.reset(false)
		goBack()
	}
	private fun goMenu() {
		game.reset2Menu()
		goBack()
	}
	private fun goQuit() {
		Gdx.app.exit()
	}

	private fun goBack() {
		hideControls()
		Gdx.input.isCursorCatched = true
		Status.paused = false
		Status.gameWin = false
		Status.gameOver = false
	}

	fun show() = showControls()
	private fun showControls() {
		stage.addActor(image)
		stage.addActor(btnQuit)
		stage.addActor(btnRestart)
		stage.addActor(btnMenu)

		stage.addFocusableActor(btnMenu)
		stage.addFocusableActor(btnRestart)
		stage.addFocusableActor(btnQuit)

		stage.focusedActor = btnRestart
		//stage.escapeActor = btnQuit
	}
	private fun hideControls() {
		image.remove()
		btnMenu.remove()
		btnQuit.remove()
		btnRestart.remove()
	}
}