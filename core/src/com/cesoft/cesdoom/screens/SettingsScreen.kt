package com.cesoft.cesdoom.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.viewport.FitViewport
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.Settings
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.cesoft.cesdoom.input.Inputs
import com.cesoft.cesdoom.ui.Styles
import de.golfgl.gdx.controllers.ControllerMenuStage


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class SettingsScreen(internal val game: CesDoom, private val assets: Assets) : Screen, InputProcessor {

    private val mapper = game.playerInput.mapper
    private var stage = ControllerMenuStage(FitViewport(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT))
    private var backgroundImage = Image(Texture(Gdx.files.internal("data/background.png")))
    private var backButton = TextButton(assets.getString(Assets.ATRAS), assets.skin)
    private val soundButton = TextButton(assets.getString(Assets.CONFIG_SOUND_EFFECTS_ONOF), assets.skin)
    private val otherButton = TextButton(assets.getString(Assets.CONFIG_OTHERS), assets.skin)
    private val win = Window("SettingsScreen", assets.skin, Styles.windowStyle)
    private val titleLabel = Label(assets.getString(Assets.CONFIG), assets.skin)

    init {
        configure()
        setListeners()
    }

    //______________________________________________________________________________________________
    private fun configure() {
        backgroundImage.setSize(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT)

        /// Window
        win.setSize(CesDoom.VIRTUAL_WIDTH-80, CesDoom.VIRTUAL_HEIGHT-80)
        win.setPosition(50f, 100f)
        win.touchable = Touchable.disabled

        /// Title
        titleLabel.setColor(.9f, .9f, .9f, 1f)
        titleLabel.setFontScale(2f)
        titleLabel.setSize(500f, 70f)
        titleLabel.setPosition(100f, CesDoom.VIRTUAL_HEIGHT - titleLabel.height - 10f)

        /// Buttons
        backButton.setSize(175f, 85f)
        backButton.setPosition(CesDoom.VIRTUAL_WIDTH - backButton.width -10f, 5f)
        soundButton.setSize(350f, 80f)
        soundButton.setPosition(CesDoom.VIRTUAL_WIDTH/2 - soundButton.width/2, 2.5f*CesDoom.VIRTUAL_HEIGHT/4)
        otherButton.setSize(450f, 80f)
        otherButton.setPosition(CesDoom.VIRTUAL_WIDTH/2 - otherButton.width/2, 1.5f*CesDoom.VIRTUAL_HEIGHT/4)

        stage.addActor(backgroundImage)
        stage.addActor(win)
        stage.addActor(titleLabel)
        stage.addActor(backButton)
        stage.addActor(soundButton)
        stage.addActor(otherButton)

        stage.addFocusableActor(backButton)
        stage.addFocusableActor(soundButton)
        stage.addFocusableActor(otherButton)

        stage.escapeActor = backButton
    }

    //______________________________________________________________________________________________
    private fun setListeners() {
        Gdx.input.inputProcessor = stage
        backButton.addListener {
            goBack()
            return@addListener false
        }
        soundButton.addListener {
            goSound()
            return@addListener false
        }
        otherButton.addListener {
            goOther()
            return@addListener false
        }
    }

    //______________________________________________________________________________________________
    private fun goBack() {
        Settings.savePrefs()
        game.setScreen(MainMenuScreen(game, assets))
    }
    private fun goSound() {
        game.setScreen(SoundSettingsScreen(game, assets))
    }
    private fun goOther() {
        game.setScreen(OthersSettingsScreen(game, assets))
    }

    //______________________________________________________________________________________________
    /// Implements: Screen
    private var inputDelay = 0f
    override fun render(delta: Float) {
        inputDelay+=delta
        if(inputDelay > Settings.GAMEPAD_INPUT_DELAY) {
            inputDelay = 0f
            processInput()
        }
        stage.act(delta)
        stage.draw()
    }
    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }
    override fun dispose() {
        stage.dispose()
    }
    override fun show() = Unit
    override fun pause() = Unit
    override fun resume() = Unit
    override fun hide() = Unit

    //______________________________________________________________________________________________
    /// Implements: InputProcessor
    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.BACK) goBack()
        return false
    }
    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = stage.touchUp(screenX, screenY, pointer, button)
    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int) = stage.touchCancelled(screenX, screenY, pointer, button)

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean = stage.mouseMoved(screenX, screenY)
    override fun keyTyped(character: Char): Boolean = stage.keyTyped(character)
    override fun scrolled(amountX: Float, amountY: Float): Boolean = stage.scrolled(amountX, amountY)
    override fun keyUp(keycode: Int): Boolean = stage.keyUp(keycode)
    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = stage.touchDragged(screenX, screenY, pointer)
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = stage.touchDown(screenX, screenY, pointer, button)


    /// PROCESS INPUT ------------------------------------------------------------------------------
    private var currentFocus = ButtonFocus.NONE
    private enum class ButtonFocus {
        NONE, BACK, SOUND, OTHER
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
                ButtonFocus.NONE -> currentFocus = ButtonFocus.SOUND
                ButtonFocus.SOUND -> currentFocus = ButtonFocus.OTHER
                ButtonFocus.OTHER -> currentFocus = ButtonFocus.BACK
                else -> Unit
            }
        }
        else if(backwards) {
            when(currentFocus) {
                ButtonFocus.NONE -> currentFocus = ButtonFocus.BACK
                ButtonFocus.BACK -> currentFocus = ButtonFocus.OTHER
                ButtonFocus.OTHER -> currentFocus = ButtonFocus.SOUND
                else -> Unit
            }
        }
    }
    private fun updateFocusColor() {
        if(backButton.color.a != 0f) {
            backButton.color = Styles.colorNormal1
            soundButton.color = Styles.colorNormal1
            otherButton.color = Styles.colorNormal1
        }
        when (currentFocus) {
            ButtonFocus.NONE -> Unit
            ButtonFocus.SOUND -> soundButton.color = Styles.colorSelected1
            ButtonFocus.OTHER -> otherButton.color = Styles.colorSelected1
            ButtonFocus.BACK -> backButton.color = Styles.colorSelected1
        }
    }
    private fun processSelectedButton() {
        when (currentFocus) {
            ButtonFocus.NONE -> Unit
            ButtonFocus.SOUND -> goSound()
            ButtonFocus.OTHER -> goOther()
            ButtonFocus.BACK -> goBack()
        }
    }
}
