package com.cesoft.cesdoom.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.Status
import com.cesoft.cesdoom.assets.Assets
import de.golfgl.gdx.controllers.ControllerMenuStage


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class PauseWidget(val game: CesDoom, val stage: ControllerMenuStage) {

	companion object {
		private val tag: String = PauseWidget::class.java.simpleName
	}

	private lateinit var btnRestart: TextButton
	private lateinit var btnMenu: TextButton
	private lateinit var btnQuit: TextButton

	fun iniPauseControls(assets: Assets) {
		btnRestart = TextButton(assets.getString(Assets.RECARGAR), assets.skin)
		btnMenu = TextButton(assets.getString(Assets.MENU), assets.skin)
		btnQuit = TextButton(assets.getString(Assets.SALIR), assets.skin)
		btnRestart.label.setFontScale(2f)
		btnMenu.label.setFontScale(2f)
		btnQuit.label.setFontScale(2f)

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

		showControls()
	}
	//______________________________________________________________________________________________
	fun goRestart() {
		goBack()
		game.reset()
	}
	fun goMenu() {
		goBack()
		game.reset2Menu()
	}
	fun goQuit() {
		Gdx.app.exit()
	}

	//______________________________________________________________________________________________
	fun pauseOnOf() {
		if(Status.gameOver || Status.gameWin)
			return
		if(btnMenu.stage == null)
			goIn()
		else
			goBack()
	}
	private fun goIn() {
		game.pauseGame()
		showControls()
		Gdx.input.isCursorCatched = false
		Status.paused = true
	}
	private fun goBack() {
		hidePauseControls()
		Gdx.input.isCursorCatched = true
		Status.paused = false
	}
	private fun showControls() {
		var x = 20f
		btnMenu.setSize(350f,90f)
		btnMenu.setPosition((CesDoom.VIRTUAL_WIDTH-btnMenu.width)/2, CesDoom.VIRTUAL_HEIGHT/2 -x)
		x += btnMenu.height + 10
		btnRestart.setSize(350f,90f)
		btnRestart.setPosition((CesDoom.VIRTUAL_WIDTH-btnRestart.width)/2, CesDoom.VIRTUAL_HEIGHT/2 -x)
		x += btnRestart.height + 10
		btnQuit.setSize(350f,90f)
		btnQuit.setPosition((CesDoom.VIRTUAL_WIDTH-btnQuit.width)/2, CesDoom.VIRTUAL_HEIGHT/2 -x)

		stage.addActor(btnQuit)
		stage.addActor(btnRestart)
		stage.addActor(btnMenu)

		stage.addFocusableActor(btnMenu)
		stage.addFocusableActor(btnRestart)
		stage.addFocusableActor(btnQuit)

		stage.focusedActor = btnMenu
		stage.escapeActor = btnQuit
	}
	fun hidePauseControls() {
		btnMenu.remove()
		btnQuit.remove()
		btnRestart.remove()
	}
}