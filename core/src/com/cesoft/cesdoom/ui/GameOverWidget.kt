package com.cesoft.cesdoom.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.Status
import com.cesoft.cesdoom.components.PlayerComponent
import com.cesoft.cesdoom.input.InputMapper
import com.cesoft.cesdoom.input.Inputs
import de.golfgl.gdx.controllers.ControllerMenuStage


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameOverWidget(
		private val game: CesDoom,
		private val stage: ControllerMenuStage,
		assets: Assets) {//}: Actor() {

	companion object {
		private val tag: String = GameOverWidget::class.java.simpleName
	}

	private val mapper = game.playerInput.mapper
	private var image: Image = Image(Texture(Gdx.files.internal("data/gameOver.png")))
	private var btnRestart = TextButton(assets.getString(Assets.RECARGAR), assets.skin)
	private var btnMenu = TextButton(assets.getString(Assets.MENU), assets.skin)
	private var btnQuit = TextButton(assets.getString(Assets.SALIR), assets.skin)

	init {
		configureWidgets()
		setListeners()
	}

	private fun configureWidgets() {
		btnRestart.label.setFontScale(2f)
		btnMenu.label.setFontScale(2f)
		btnQuit.label.setFontScale(2f)

		Controllers.addListener(game.playerInput)//TODO: remove!!!!!!!!!!!

		val ratio = 125f / 319f
		val width = CesDoom.VIRTUAL_WIDTH*2f/4f
		image.setSize(width, ratio * width)
		btnRestart.setSize(350f, 90f)
		btnMenu.setSize(300f, 90f)
		btnQuit.setSize(300f, 90f)

		var y = CesDoom.VIRTUAL_HEIGHT * 0.55f
		image.setPosition((CesDoom.VIRTUAL_WIDTH-image.width)/2, y)
		y -= image.height/3 +2
		btnMenu.setPosition((CesDoom.VIRTUAL_WIDTH-btnMenu.width)/2, y)
		y -= btnMenu.height + 2
		btnRestart.setPosition((CesDoom.VIRTUAL_WIDTH-btnRestart.width)/2, y)
		y -= btnRestart.height + 2
		btnQuit.setPosition((CesDoom.VIRTUAL_WIDTH-btnQuit.width)/2, y)
	}

	//______________________________________________________________________________________________
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
	}

	fun show() {
		game.playServices?.submitScore(PlayerComponent.score)
		Status.paused = true
		Status.gameOver = true
		Gdx.input.isCursorCatched = false
		showControls()
	}
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

	private fun clean() {
		Status.paused = false
		Status.gameOver = false
		Status.gameWin = false
		hideControls()
	}
	private fun hideControls() {
		image.remove()
		btnMenu.remove()
		btnQuit.remove()
		btnRestart.remove()
		Gdx.input.isCursorCatched = true
	}

	private fun goRestart() {
		clean()
		game.reset()
	}
	private fun goMenu() {
		clean()
		game.reset2Menu()
	}
	private fun goQuit() {
		Gdx.app.exit()
	}


	/// PROCESS INPUT ------------------------------------------------------------------------------
	private var currentFocus = ButtonFocus.NONE
	private enum class ButtonFocus {
		NONE, RESTART, MENU, QUIT
	}
	fun processInput(mapper: InputMapper) {
		when {
			mapper.isButtonPressed(Inputs.Action.START) -> goRestart()
			mapper.isButtonPressed(Inputs.Action.BACK) -> goMenu()
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
		//Log.e("updateFocus", "-----------------------------------$currentFocus")
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
		when(currentFocus) {//TODO
			ButtonFocus.NONE -> Unit
//			ButtonFocus.RESTART -> goRestart()
//			ButtonFocus.MENU -> goMenu()
//			ButtonFocus.QUIT -> goQuit()
		}
	}
}
