package com.cesoft.cesdoom.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.viewport.FitViewport
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.Settings
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
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

    private val win = Window("SettingsScreen", assets.skin, Styles.windowStyle)
    private val titleLabel = Label(assets.getString(Assets.CONFIG), assets.skin)

    private val soundButton = TextButton(assets.getString(Assets.CONFIG_SOUND_EFFECTS_ONOF), assets.skin, "toggle")
    private val soundVolumeLabel = Label(assets.getString(Assets.CONFIG_SOUND_EFFECTS_VOLUME), assets.skin)
    private val soundVolumeSlider = Slider( 0f, 1f, 0.1f,false, assets.skin)

    private val musicButton = TextButton(assets.getString(Assets.CONFIG_MUSIC_ONOF), assets.skin, "toggle")
    private val musicVolumeLabel = Label(assets.getString(Assets.CONFIG_MUSIC_VOLUME), assets.skin)
    private val musicVolumeSlider = Slider( 0f, 1f, 0.1f,false, assets.skin)

    private val painVibrationButton = TextButton(assets.getString(Assets.CONFIG_VIBRATION_ONOF), assets.skin, "toggle")
    private val gpgsSignInButton = TextButton(assets.getString(Assets.CONFIG_GPGS_ONOF), assets.skin, "toggle")
    private val gamepadButton = TextButton("GAME PAD", assets.skin)

    private val lblSeparator = Label("", assets.skin)


    init {
        configure()
        setListeners()
        //Gdx.input.inputProcessor = this
        //Controllers.addListener(game.playerInput)
    }

    //______________________________________________________________________________________________
    private fun configure() {

        backgroundImage.setSize(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT)

        backButton.setSize(175f, 85f)
        backButton.setPosition(CesDoom.VIRTUAL_WIDTH - backButton.width - 5, 5f)

        /// Inside Window
        val xWin = 50f
        val yWin = 100f
        win.setSize(CesDoom.VIRTUAL_WIDTH-100, CesDoom.VIRTUAL_HEIGHT-100)
        win.setPosition(xWin, yWin)

        /// Title
        titleLabel.setColor(.9f, .9f, .9f, 1f)
        titleLabel.setFontScale(2f)
        titleLabel.setSize(500f, 70f)

        /// Sound On/Off
        soundButton.isChecked = Settings.isSoundEnabled
        soundButton.setSize(350f, 80f)

        /// Sound Effects Volume
        soundVolumeLabel.setSize(500f, 30f)
        soundVolumeSlider.value = Settings.soundVolume
        soundVolumeSlider.setSize(550f, 30f)

        /// Music On/Off
        musicButton.isChecked = Settings.isMusicEnabled
        musicButton.setSize(250f, 80f)

        /// Music Volume
        musicVolumeLabel.setSize(500f, 30f)
        musicVolumeSlider.value = Settings.musicVolume
        musicVolumeSlider.setSize(550f, 30f)

        /// Pain Vibration On/Off
        painVibrationButton.isChecked = Settings.isVibrationEnabled
        painVibrationButton.setSize(600f, 80f)

        /// Google Play Game Services On/Off
        gpgsSignInButton.isChecked = Settings.isGPGSEnabled
        gpgsSignInButton.setSize(500f, 80f)

        /// Gamepad settings
        gamepadButton.setSize(300f, 80f)


        val scrollTable = Table()
        scrollTable.width = win.width-5
        scrollTable.add(titleLabel).width(500f).height(90f).align(Align.left)
        scrollTable.row()
        //
        scrollTable.add(soundButton).size(370f, 80f)
        scrollTable.row()
        scrollTable.add(soundVolumeLabel).size(500f, 30f).align(Align.topLeft)
        scrollTable.row()
        scrollTable.add(soundVolumeSlider).size(550f, 30f).align(Align.topLeft)
        scrollTable.row()
        scrollTable.add(lblSeparator).size(50f, 50f).row()
        //
        scrollTable.add(musicButton).size(250f, 80f)
        scrollTable.row()
        scrollTable.add(musicVolumeLabel).size(500f, 30f).align(Align.topLeft)
        scrollTable.row()
        scrollTable.add(musicVolumeSlider).size(550f, 30f).align(Align.topLeft)
        scrollTable.row()
        scrollTable.add(lblSeparator).size(50f, 50f).row()
        //
        scrollTable.add(painVibrationButton).size(390f, 80f)
        scrollTable.row()
        //
        scrollTable.add(gpgsSignInButton).size(390f, 80f)
        scrollTable.row()
        //
        scrollTable.add(gamepadButton).size(300f, 80f)
        scrollTable.row()
        //
        scrollTable.add(lblSeparator).size(50f, 50f).row()

        val scroller = ScrollPane(scrollTable)
        val table = Table()
        table.setFillParent(true)
        table.add(scroller).fill().expand()
        win.addActor(table)

        stage.addActor(backgroundImage)
        stage.addActor(backButton)
        stage.addActor(win)

        stage.addFocusableActor(scrollTable)

//TODO: test ControllerScrollablePane, ControllerSlider

//        stage.addFocusableActor(soundButton)
//        stage.addFocusableActor(soundVolumeSlider)
//        stage.addFocusableActor(musicButton)
//        stage.addFocusableActor(musicVolumeSlider)
//        stage.addFocusableActor(painVibrationButton)
//        stage.addFocusableActor(gpgsSignInButton)
//        stage.addFocusableActor(gamepadButton)
        stage.addFocusableActor(backButton)
        //stage.escapeActor = backButton
        stage.focusedActor = backButton
        Gdx.input.inputProcessor = stage
    }

    //______________________________________________________________________________________________
    private fun goBack() {
        Settings.savePrefs()
        game.setScreen(MainMenuScreen(game, assets))
    }
    //______________________________________________________________________________________________
    private fun setListeners() {

        backButton.addListener {
            goBack()
            return@addListener false
        }
        //
        soundVolumeSlider.addListener {
            Settings.soundVolume = soundVolumeSlider.value
            if(soundVolumeSlider.value == 0f) {
                soundButton.isChecked = false
                Settings.isSoundEnabled = false
            }
            else {
                soundButton.isChecked = true
                Settings.isSoundEnabled = true
            }
            return@addListener false
        }
        soundButton.addListener {
            Settings.isSoundEnabled = soundButton.isChecked
            return@addListener false
        }
        //
        musicVolumeSlider.addListener {
            Settings.musicVolume = musicVolumeSlider.value
            if(musicVolumeSlider.value == 0f) {
                musicButton.isChecked = false
                Settings.isMusicEnabled = false
            }
            else {
                musicButton.isChecked = true
                Settings.isMusicEnabled = true
            }
            return@addListener false
        }
        musicButton.addListener {
            Settings.isMusicEnabled = musicButton.isChecked
            return@addListener false
        }
        //
        painVibrationButton.addListener {
            Settings.isVibrationEnabled = painVibrationButton.isChecked
            return@addListener false
        }
        //
        gpgsSignInButton.addListener {
            Settings.isGPGSEnabled = gpgsSignInButton.isChecked
            return@addListener false
        }
        //
        gamepadButton.addListener {
            game.setScreen(SettingsGamePadScreen(game, assets))
            return@addListener false
        }
        //
    }

    //______________________________________________________________________________________________
    /// Implements: Screen
    private var inputDelay = 0f
    override fun render(delta: Float) {
        inputDelay+=delta
        if(inputDelay > .250f) {
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
    override fun mouseMoved(screenX: Int, screenY: Int): Boolean = stage.mouseMoved(screenX, screenY)
    override fun keyTyped(character: Char): Boolean = stage.keyTyped(character)
    override fun scrolled(amount: Int): Boolean = stage.scrolled(amount)
    override fun keyUp(keycode: Int): Boolean = stage.keyUp(keycode)
    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = stage.touchDragged(screenX, screenY, pointer)
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = stage.touchDown(screenX, screenY, pointer, button)


    /// PROCESS INPUT ------------------------------------------------------------------------------
    private var currentFocus = ButtonFocus.NONE
    private enum class ButtonFocus {
        NONE, BACK
    }
    private fun processInput() {
        if(mapper.isButtonPressed(Inputs.Action.START)
                || mapper.isButtonPressed(Inputs.Action.EXIT)
                || mapper.isButtonPressed(Inputs.Action.BACK)) {
            currentFocus = ButtonFocus.BACK
            goBack()
        }
        updateFocusSelection()
        updateFocusColor()
        if(mapper.isButtonPressed(Inputs.Action.FIRE)) {
            processSelectedButton()
        }
    }
    private fun updateFocusSelection() {
        val backwards = mapper.isGoingBackwards()
        val forward = mapper.isGoingForward()
        if(forward || backwards) {
            currentFocus = ButtonFocus.BACK
        }
    }
    private fun updateFocusColor() {
        if(backButton.color.a != 0f) {
            backButton.color = Styles.colorNormal1
        }
        when (currentFocus) {
            ButtonFocus.NONE -> Unit
            ButtonFocus.BACK -> backButton.color = Styles.colorSelected1
        }
    }
    private fun processSelectedButton() {
        when (currentFocus) {
            ButtonFocus.NONE -> Unit
            ButtonFocus.BACK -> goBack()
        }
    }
}
