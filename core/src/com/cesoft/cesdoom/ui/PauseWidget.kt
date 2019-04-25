package com.cesoft.cesdoom.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.controllers.Controllers
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
import com.cesoft.cesdoom.input.InputMapper
import com.cesoft.cesdoom.input.Inputs


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class PauseWidget(private val game: CesDoom, stage: Stage, assets: Assets) : Actor() {

	companion object {
		private val tag: String = PauseWidget::class.java.simpleName
	}

	private val mapper = game.playerInput.mapper
	private var window: Window
	private var btnRestart: TextButton
	private var btnMenu: TextButton
	private var btnQuit: TextButton

	//______________________________________________________________________________________________
	init {
		super.setStage(stage)
		//
		val ws = Window.WindowStyle()
		ws.titleFont = BitmapFont()//Necessary, stupid but necessary
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
		Controllers.addListener(game.playerInput)
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
	private fun setListeners() {
		super.addListener(object : InputListener() {
			override fun keyDown(event: InputEvent?, keycode: Int): Boolean {
				if(keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
					pauseOnOf()
					return true
				}
				return false
			}
		})
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
		if(window.stage == null)
			goIn()
		else
			goBack()
	}
	private fun goIn() {
		game.pauseGame()
		stage.addActor(window)
		Gdx.input.isCursorCatched = false
		Status.paused = true
	}
	private fun goBack() {
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


	/// PROCESS INPUT ------------------------------------------------------------------------------
	private var currentFocus = ButtonFocus.NONE
	private enum class ButtonFocus {
		NONE, RESTART, MENU, QUIT
	}
	fun processInput(mapper: InputMapper) {
		when {
			mapper.isButtonPressed(Inputs.Action.BACK) -> goBack()
			mapper.isButtonPressed(Inputs.Action.START) -> goRestart()
			mapper.isButtonPressed(Inputs.Action.EXIT) -> goQuit()
		}
		updateFocusSelection()
		updateFocusColor()
		if(mapper.isButtonPressed(Inputs.Action.FIRE)) {
			processSelectedButton()
		}
	}
	private fun updateFocusSelection() {
		val down = mapper.isGoingDown()
		val up = mapper.isGoingUp()
		if(up) {
			when(currentFocus) {
				ButtonFocus.NONE -> currentFocus = ButtonFocus.RESTART
				ButtonFocus.MENU-> currentFocus = ButtonFocus.RESTART
				ButtonFocus.QUIT -> currentFocus = ButtonFocus.MENU
				else -> Unit
			}
		}
		else if(down) {
			when(currentFocus) {
				ButtonFocus.NONE -> currentFocus = ButtonFocus.RESTART
				ButtonFocus.RESTART-> currentFocus = ButtonFocus.MENU
				ButtonFocus.MENU -> currentFocus = ButtonFocus.QUIT
				else -> Unit
			}
		}
	}
	private fun updateFocusColor() {
		if(btnRestart.color.a != 0f) {
			btnRestart.color = Styles.colorNormal1
			btnMenu.color = Styles.colorNormal1
			btnQuit.color = Styles.colorNormal1
		}
		when(currentFocus) {
			ButtonFocus.NONE -> Unit
			ButtonFocus.RESTART -> btnRestart.color = Styles.colorSelected1
			ButtonFocus.MENU -> btnMenu.color = Styles.colorSelected1
			ButtonFocus.QUIT -> btnQuit.color = Styles.colorSelected1
		}
	}
	private fun processSelectedButton() {
		when(currentFocus) {
			ButtonFocus.NONE -> Unit
			ButtonFocus.RESTART -> goRestart()
			ButtonFocus.MENU -> goMenu()
			ButtonFocus.QUIT -> goQuit()
		}
	}

}