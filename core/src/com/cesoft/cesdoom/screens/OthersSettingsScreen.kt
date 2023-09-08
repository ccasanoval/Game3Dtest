package com.cesoft.cesdoom.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
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
class OthersSettingsScreen(private val game: CesDoom, private val assets: Assets)
    : Screen, InputProcessor {

    private val mapper = game.playerInput.mapper
    private var stage = ControllerMenuStage(FitViewport(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT))
    private var backgroundImage = Image(Texture(Gdx.files.internal("data/background.png")))
    private var backButton = TextButton(assets.getString(Assets.ATRAS), assets.skin)
	private var mainButton = TextButton(assets.getString(Assets.MAIN), assets.skin)

	private val win = Window("SettingsScreen", assets.skin, Styles.windowStyle)
    private val titleLabel = Label(assets.getString(Assets.CONFIG), assets.skin)

    private val painVibrationButton = TextButton(assets.getString(Assets.CONFIG_VIBRATION_ONOF), assets.skin, "toggle")
    private val gpgsSignInButton = TextButton(assets.getString(Assets.CONFIG_GPGS_ONOF), assets.skin, "toggle")
    private val gamepadButton = TextButton("GAME PAD", assets.skin)

    init {
        configure()
        setListeners()
        //Gdx.input.inputProcessor = this
        //Controllers.addListener(game.playerInput)
    }

    //______________________________________________________________________________________________
    private fun configure() {
        /// Background image
        backgroundImage.setSize(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT)
        stage.addActor(backgroundImage)

        /// Window
        win.setSize(CesDoom.VIRTUAL_WIDTH-80, CesDoom.VIRTUAL_HEIGHT-80)
        win.setPosition(50f, 100f)
        stage.addActor(win)

        var y = CesDoom.VIRTUAL_HEIGHT - 5f

		/// Title
		titleLabel.setColor(.9f, .9f, .9f, 1f)
		titleLabel.setFontScale(2f)
		titleLabel.setSize(500f, 70f)
		titleLabel.setPosition(100f, y - titleLabel.height - 5f)
		stage.addActor(titleLabel)
		y -= titleLabel.height*2.5f

        /// Pain Vibration On/Off
        painVibrationButton.isChecked = Settings.isVibrationEnabled
        painVibrationButton.setSize(600f, 80f)
		painVibrationButton.setPosition(CesDoom.VIRTUAL_WIDTH/2 - painVibrationButton.width/2, y)
		stage.addActor(painVibrationButton)
		stage.addFocusableActor(painVibrationButton)
		y -= painVibrationButton.height

        /// Google Play Game Services On/Off
        gpgsSignInButton.isChecked = Settings.isGPGSEnabled
        gpgsSignInButton.setSize(500f, 80f)
		gpgsSignInButton.setPosition(CesDoom.VIRTUAL_WIDTH/2 - gpgsSignInButton.width/2, y)
		stage.addActor(gpgsSignInButton)
		stage.addFocusableActor(gpgsSignInButton)
		y -= gpgsSignInButton.height

        /// Gamepad settings
        gamepadButton.setSize(300f, 80f)
		gamepadButton.setPosition(CesDoom.VIRTUAL_WIDTH/2 - gamepadButton.width/2, y)
		stage.addActor(gamepadButton)
		stage.addFocusableActor(gamepadButton)
		y -= gamepadButton.height


		/// Back button ----------------------------------------------------------------------------
		backButton.setSize(175f, 85f)
		backButton.setPosition(CesDoom.VIRTUAL_WIDTH - backButton.width - 10f, 5f)
		stage.addActor(backButton)
		stage.addFocusableActor(backButton)

		/// Main Menu button ----------------------------------------------------------------------------
		mainButton.setSize(175f, 85f)
		mainButton.setPosition(CesDoom.VIRTUAL_WIDTH - 2*backButton.width - 20f, 5f)
		stage.addActor(mainButton)
		stage.addFocusableActor(mainButton)

		stage.escapeActor = mainButton
    }

    //______________________________________________________________________________________________
    private fun goBack() {
        Settings.savePrefs()
        game.setScreen(SettingsScreen(game, assets))
    }
	private fun goMain() {
		Settings.savePrefs()
		game.setScreen(MainMenuScreen(game, assets))
	}
    private fun goVibrate(invert: Boolean = true) {
        if(invert)painVibrationButton.isChecked = !(painVibrationButton.isChecked)
        Settings.isVibrationEnabled = painVibrationButton.isChecked
    }
    private fun goGoogleGames(invert: Boolean = true) {
        if(invert)gpgsSignInButton.isChecked = !gpgsSignInButton.isChecked
        Settings.isGPGSEnabled = gpgsSignInButton.isChecked
    }
    private fun goGamepad() {
        game.setScreen(SettingsGamePadScreen(game, assets))
    }
    //______________________________________________________________________________________________
    private fun setListeners() {
        Gdx.input.inputProcessor = stage

        backButton.addListener {
            goBack()
            return@addListener false
        }
		mainButton.addListener {
			goMain()
			return@addListener false
		}
        //
        painVibrationButton.addListener {
            goVibrate(false)
            return@addListener false
        }
        //
        gpgsSignInButton.addListener {
            goGoogleGames(false)
            return@addListener false
        }
        //
        gamepadButton.addListener {
            goGamepad()
            return@addListener false
        }
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
        NONE, BACK, MAIN, VIBRATE, GOOGLE_GAMES, GAMEPAD
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
                ButtonFocus.NONE -> currentFocus = ButtonFocus.VIBRATE
                ButtonFocus.VIBRATE -> currentFocus = ButtonFocus.GOOGLE_GAMES
                ButtonFocus.GOOGLE_GAMES -> currentFocus = ButtonFocus.GAMEPAD
                ButtonFocus.GAMEPAD -> currentFocus = ButtonFocus.MAIN
                ButtonFocus.MAIN -> currentFocus = ButtonFocus.BACK
                else -> Unit
            }
        }
        else if(backwards) {
            when(currentFocus) {
                ButtonFocus.NONE -> currentFocus = ButtonFocus.BACK
                ButtonFocus.BACK -> currentFocus = ButtonFocus.MAIN
                ButtonFocus.MAIN -> currentFocus = ButtonFocus.GAMEPAD
                ButtonFocus.GAMEPAD -> currentFocus = ButtonFocus.GOOGLE_GAMES
                ButtonFocus.GOOGLE_GAMES -> currentFocus = ButtonFocus.VIBRATE
                else -> Unit
            }
        }
    }
    private fun updateFocusColor() {
        if(backButton.color.a != 0f) {
            backButton.color = Styles.colorNormal1
            mainButton.color = Styles.colorNormal1
            painVibrationButton.color = Styles.colorNormal1
            gpgsSignInButton.color = Styles.colorNormal1
            gamepadButton.color = Styles.colorNormal1
        }
        when (currentFocus) {
            ButtonFocus.NONE -> Unit
            ButtonFocus.VIBRATE -> painVibrationButton.color = Styles.colorSelected1
            ButtonFocus.GOOGLE_GAMES -> gpgsSignInButton.color = Styles.colorSelected1
            ButtonFocus.GAMEPAD -> gamepadButton.color = Styles.colorSelected1
            ButtonFocus.MAIN -> mainButton.color = Styles.colorSelected1
            ButtonFocus.BACK -> backButton.color = Styles.colorSelected1
        }
    }
    private fun processSelectedButton() {
        when (currentFocus) {
            ButtonFocus.NONE -> Unit
            ButtonFocus.VIBRATE -> goVibrate()
            ButtonFocus.GOOGLE_GAMES -> goGoogleGames()
            ButtonFocus.GAMEPAD -> goGamepad()
            ButtonFocus.MAIN -> goMain()
            ButtonFocus.BACK -> goBack()
        }
    }

    companion object {
        private val tag: String = OthersSettingsScreen::class.simpleName!!
    }
}
