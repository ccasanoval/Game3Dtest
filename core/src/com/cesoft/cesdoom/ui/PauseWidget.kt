package com.cesoft.cesdoom.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.Status
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.input.Inputs
import com.cesoft.cesdoom.util.Log
import de.golfgl.gdx.controllers.ControllerMenuStage


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class PauseWidget(
		private val game: CesDoom,
		private val stage: ControllerMenuStage,
		assets: Assets) {

	companion object {
		private val tag: String = PauseWidget::class.java.simpleName
	}

	private val btnRestart = TextButton(assets.getString(Assets.RECARGAR), assets.skin)
	private val btnMenu = TextButton(assets.getString(Assets.MENU), assets.skin)
	private val btnQuit = TextButton(assets.getString(Assets.SALIR), assets.skin)

	init {
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

		//TODO: Delelete?
		showControls()
		hideControls()
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
		hideControls()
		Gdx.input.isCursorCatched = true
		Status.paused = false
	}
	private fun showControls() {
		var y = CesDoom.VIRTUAL_HEIGHT * 0.65f
		btnMenu.setSize(350f,90f)
		btnMenu.setPosition((CesDoom.VIRTUAL_WIDTH-btnMenu.width)/2, y)
		y -= btnMenu.height + 10
		btnRestart.setSize(350f,90f)
		btnRestart.setPosition((CesDoom.VIRTUAL_WIDTH-btnRestart.width)/2, y)
		y -= btnRestart.height + 10
		btnQuit.setSize(350f,90f)
		btnQuit.setPosition((CesDoom.VIRTUAL_WIDTH-btnQuit.width)/2, y)

		stage.addActor(btnQuit)
		stage.addActor(btnRestart)
		stage.addActor(btnMenu)

		stage.addFocusableActor(btnMenu)
		stage.addFocusableActor(btnRestart)
		stage.addFocusableActor(btnQuit)

		stage.focusedActor = btnMenu
		//stage.escapeActor = btnQuit
	}
	private fun hideControls() {
		btnMenu.remove()
		btnQuit.remove()
		btnRestart.remove()
	}

//TODO: Una sola clase... modifica simplemente los botones en cada caso!!!!
	/// PROCESS INPUT ------------------------------------------------------------------------------
	private val mapper = game.playerInput.mapper
	private var currentFocus = ButtonFocus.NONE
	private enum class ButtonFocus {
		NONE, QUIT, RESTART, MENU
	}
	fun processInput() {
		if(btnMenu.stage == null)return
		if(mapper.isButtonPressed(Inputs.Action.START)
				|| mapper.isButtonPressed(Inputs.Action.EXIT)
				|| mapper.isButtonPressed(Inputs.Action.BACK)) {
			currentFocus = ButtonFocus.MENU
			goBack()
		}
		updateFocusSelection()
		updateFocusColor()
		if(mapper.isButtonPressed(Inputs.Action.FIRE)) {
			processSelectedButton()
		}
	}
	private fun updateFocusSelection() {
		val backwards = mapper.isGoingBackwards() || mapper.isGoingUp()
		val forward = mapper.isGoingForward() || mapper.isGoingDown()
		if(forward) {
			when(currentFocus) {
				ButtonFocus.NONE -> currentFocus = ButtonFocus.MENU
				ButtonFocus.MENU -> currentFocus = ButtonFocus.RESTART
				ButtonFocus.RESTART -> currentFocus = ButtonFocus.QUIT
				else -> Unit
			}
		}
		else if(backwards) {
			when(currentFocus) {
				ButtonFocus.NONE -> currentFocus = ButtonFocus.QUIT
				ButtonFocus.QUIT -> currentFocus = ButtonFocus.RESTART
				ButtonFocus.RESTART -> currentFocus = ButtonFocus.MENU
				else -> Unit
			}
		}
	}
	private fun updateFocusColor() {
		if(btnMenu.color.a != 0f) {
			btnMenu.color = Styles.colorNormal1
			btnRestart.color = Styles.colorNormal1
			btnQuit.color = Styles.colorNormal1
		}
		when (currentFocus) {
			ButtonFocus.NONE -> Unit
			ButtonFocus.MENU -> btnMenu.color = Styles.colorSelected1
			ButtonFocus.RESTART -> btnRestart.color = Styles.colorSelected1
			ButtonFocus.QUIT -> btnQuit.color = Styles.colorSelected1
		}
	}
	private fun processSelectedButton() {
		when (currentFocus) {
			ButtonFocus.NONE -> Unit
			ButtonFocus.MENU -> goMenu()
			ButtonFocus.RESTART -> goRestart()
			ButtonFocus.QUIT -> goQuit()
		}
	}
}