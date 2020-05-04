package com.cesoft.cesdoom.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.FitViewport
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.input.Inputs
import com.cesoft.cesdoom.ui.Styles
import de.golfgl.gdx.controllers.ControllerMenuStage


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class AboutScreen(internal val game: CesDoom, private val assets: Assets) : Screen {//, InputProcessor {

	private var stage = ControllerMenuStage(FitViewport(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT))
	private var backgroundImage: Image = Image(Texture(Gdx.files.internal("data/background.png")))
	private var backButton: TextButton = TextButton(assets.getString(Assets.ATRAS), assets.skin)
	private var rateButton: TextButton = TextButton(assets.getString(Assets.PUNTUA), assets.skin)

	private val text: Label = Label(assets.getString(Assets.SOBRE_TXT), assets.skin)
	private val scrollPane = ScrollPane(text, assets.skin)
	private val win = Window("About", assets.skin, Styles.windowStyle)

	private val mapper = game.playerInput.mapper

	init {
		configureWidgets()
		setListeners()
	}

	//______________________________________________________________________________________________
	private fun configureWidgets() {
		backgroundImage.setSize(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT)
		backButton.setSize(175f, 85f)
		backButton.setPosition(CesDoom.VIRTUAL_WIDTH - backButton.width - 5, 5f)
		rateButton.setSize(350f, 85f)
		rateButton.setPosition(backButton.x - rateButton.width -5, 5f)

		text.setWrap(true)
		text.setFontScale(1.5f)
		//text.color = Styles.colorNormal1

		scrollPane.setScrollingDisabled(true, false)
		scrollPane.setSize(CesDoom.VIRTUAL_WIDTH-250, CesDoom.VIRTUAL_HEIGHT-250)
		scrollPane.setPosition(150f, 150f)
		scrollPane.zIndex = 2

		win.setSize(CesDoom.VIRTUAL_WIDTH-100, CesDoom.VIRTUAL_HEIGHT-100)
		win.setPosition(75f,100f)
		win.zIndex = 10
		//win.touchable = Touchable.disabled

		stage.addActor(backgroundImage)
		stage.addActor(backButton)
		stage.addActor(rateButton)
		stage.addActor(win)
		stage.addActor(scrollPane)

		stage.addFocusableActor(backButton)
		stage.addFocusableActor(rateButton)
		stage.escapeActor = backButton
		stage.focusedActor = backButton
		Gdx.input.inputProcessor = stage
	}

	//______________________________________________________________________________________________
	private fun goBack() { game.setScreen(MainMenuScreen(game, assets)) }
	private fun goRate() { game.playServices?.rateGame() }
	private fun setListeners() {
		backButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				goBack()
			}
		})
		rateButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				goRate()
			}
		})
	}

	//______________________________________________________________________________________________
	private var inputDelay = 0f//TODO:Use a base class so not to repeat ys
	override fun render(delta: Float) {
		inputDelay+=delta
		if(inputDelay > .150f) {
			inputDelay = 0f
			processInput()
		}
		stage.act(delta)
		stage.draw()
	}

	//______________________________________________________________________________________________
	override fun resize(width: Int, height: Int) {
		stage.viewport.update(width, height)
	}
	//______________________________________________________________________________________________
	override fun dispose() {
		stage.dispose()
	}

	override fun show() = Unit
	override fun pause() = Unit
	override fun resume() = Unit
	override fun hide() = Unit


	//______________________________________________________________________________________________
	/// Implements: InputProcessor
//	override fun keyDown(keycode: Int): Boolean {
//		if (keycode == Input.Keys.BACK) goBack()
//		return false
//	}
//	override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = stage.touchUp(screenX, screenY, pointer, button)
//	override fun mouseMoved(screenX: Int, screenY: Int): Boolean = stage.mouseMoved(screenX, screenY)
//	override fun keyTyped(character: Char): Boolean = stage.keyTyped(character)
//	override fun scrolled(amount: Int): Boolean = stage.scrolled(amount)
//	override fun keyUp(keycode: Int): Boolean = stage.keyUp(keycode)
//	override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = stage.touchDragged(screenX, screenY, pointer)
//	override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = stage.touchDown(screenX, screenY, pointer, button)


	/// PROCESS INPUT ------------------------------------------------------------------------------
	private var currentFocus = ButtonFocus.NONE
	private enum class ButtonFocus {
		NONE, BACK, RATE
	}
	private fun processInput() {
		if(mapper.isButtonPressed(Inputs.Action.Start)
				|| mapper.isButtonPressed(Inputs.Action.Exit)
				|| mapper.isButtonPressed(Inputs.Action.Back)) {
			currentFocus = ButtonFocus.BACK
			goBack()
		}
		updateFocusSelection()
		updateFocusColor()
		if(mapper.isButtonPressed(Inputs.Action.Fire)) {
			processSelectedButton()
		}
	}
	private fun updateFocusSelection() {
		val backwards = mapper.isGoingBackwards() || mapper.isGoingUp()
		val forward = mapper.isGoingForward() || mapper.isGoingDown()
		if(forward) {
			when(currentFocus) {
				ButtonFocus.NONE -> currentFocus = ButtonFocus.RATE
				ButtonFocus.RATE -> currentFocus = ButtonFocus.BACK
				else -> Unit
			}
		}
		else if(backwards) {
			when(currentFocus) {
				ButtonFocus.NONE -> currentFocus = ButtonFocus.BACK
				ButtonFocus.BACK -> currentFocus = ButtonFocus.RATE
				else -> Unit
			}
		}
	}
	private fun updateFocusColor() {
		if(backButton.color.a != 0f) {
			backButton.color = Styles.colorNormal1
			rateButton.color = Styles.colorNormal1
		}
		when (currentFocus) {
			ButtonFocus.NONE -> Unit
			ButtonFocus.RATE -> rateButton.color = Styles.colorSelected1
			ButtonFocus.BACK -> backButton.color = Styles.colorSelected1
		}
	}
	private fun processSelectedButton() {
		when (currentFocus) {
			ButtonFocus.NONE -> Unit
			ButtonFocus.RATE -> goRate()
			ButtonFocus.BACK -> goBack()
		}
	}
}
