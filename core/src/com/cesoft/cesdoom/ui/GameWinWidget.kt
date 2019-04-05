package com.cesoft.cesdoom.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.Status
import com.cesoft.cesdoom.components.PlayerComponent
import com.cesoft.cesdoom.input.InputMapper
import com.cesoft.cesdoom.input.Inputs


////////////////////////////////////////////////////////////////////////////////////////////////////
// TODO: MVP !!
class GameWinWidget(private val game: CesDoom, stage: Stage, private val assets: Assets) : Actor() {
	private val mapper = game.playerInput.mapper
	private val image: Image = Image(Texture(Gdx.files.internal("data/gameWin.png")))
	private val window: Window
	private var btnRestart: TextButton
	private val btnMenu: TextButton
	private val btnQuit: TextButton

	init {
		super.setStage(stage)
		//
		val ws = Window.WindowStyle()
		ws.titleFont = BitmapFont()
		ws.titleFontColor = Color.BLUE
		window = Window("", ws)
		//
		btnRestart = TextButton(assets.getString(Assets.NEXT_LEVEL), assets.skin)
		btnRestart.label.setFontScale(2f)
		btnMenu = TextButton(assets.getString(Assets.MENU), assets.skin)
		btnMenu.label.setFontScale(2f)
		btnQuit = TextButton(assets.getString(Assets.SALIR), assets.skin)
		btnQuit.label.setFontScale(2f)
		//
		configureWidgets()
		setListeners()
		Controllers.addListener(game.playerInput)//TODO: remove!!!!!!!!!!!
		//
		setSize(CesDoom.VIRTUAL_WIDTH-100, CesDoom.VIRTUAL_HEIGHT-120)
		setPosition((CesDoom.VIRTUAL_WIDTH - width)/2, (CesDoom.VIRTUAL_HEIGHT - height)/2)
	}

	private fun configureWidgets() {
		val ratio = 125f / 319f
		val width = CesDoom.VIRTUAL_WIDTH*2f/4f
		window.add<Image>(image).width(width).height(ratio * width)
		window.row().pad(.5f)

		window.add<TextButton>(btnRestart).width(375f).height(90f)
		window.row().pad(0f)

		window.add<TextButton>(btnMenu).width(300f).height(90f)
		window.row().pad(0f)
		window.add<TextButton>(btnQuit).width(300f).height(90f)
	}


	//______________________________________________________________________________________________
	private fun setListeners() {
		btnRestart.addListener(object : ClickListener() {
			override fun clicked(inputEvent: InputEvent?, x: Float, y: Float) {
				game.reset(false)
				exit()
			}
		})
		btnMenu.addListener(object : ClickListener() {
			override fun clicked(inputEvent: InputEvent?, x: Float, y: Float) {
				game.reset2Menu()
				exit()
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
		val oldLevel = game.nextLevel()
		game.playServices?.unlockAchievement(oldLevel)

		if(game.isNextOrReload()) {
			btnRestart.setText(assets.getString(Assets.NEXT_LEVEL))
			image.drawable = SpriteDrawable(Sprite(Texture(Gdx.files.internal("data/gameWin.png"))))
		}
		else {
			btnRestart.setText(assets.getString(Assets.RECARGAR))
			image.drawable = SpriteDrawable(Sprite(Texture(Gdx.files.internal("data/gameWinOver.png"))))
			game.playServices?.submitScore(PlayerComponent.score)
		}

		stage.addActor(window)
		Gdx.input.isCursorCatched = false
		Status.paused = true
		Status.gameWin = true
	}
	//______________________________________________________________________________________________
	private fun exit() {
		window.remove()
		Gdx.input.isCursorCatched = true
		Status.paused = false
		Status.gameWin = false
		Status.gameOver = false
	}

	/// Extends Actor
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
	/*private var delay = 0f
	override fun act(delta: Float) {
		super.act(delta)
		delay += delta
		if(delay < .250)return
		com.cesoft.cesdoom.util.Log.e("GameWinWidget", "act----------------------------------------")
		delay = 0f
		when {
			mapper.isButtonPressed(Inputs.Action.START) -> goRestart()
			mapper.isButtonPressed(Inputs.Action.BACK) -> goMenu()
			mapper.isButtonPressed(Inputs.Action.EXIT) -> goQuit()
		}
	}*/
	private fun goRestart() {
		exit()
		game.reset()
	}
	private fun goMenu() {
		exit()
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
			mapper.isButtonPressed(Inputs.Action.BACK) -> goMenu()
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
