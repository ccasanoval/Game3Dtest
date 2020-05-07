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
import com.cesoft.cesdoom.util.Log
import de.golfgl.gdx.controllers.ControllerMenuStage


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class SoundSettingsScreen(private val game: CesDoom, private val assets: Assets)
    : Screen, InputProcessor {

    private val mapper = game.playerInput.mapper
    private var stage = ControllerMenuStage(FitViewport(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT))
    private var backgroundImage = Image(Texture(Gdx.files.internal("data/background.png")))
    private var backButton = TextButton(assets.getString(Assets.ATRAS), assets.skin)
    private var mainButton = TextButton(assets.getString(Assets.MAIN), assets.skin)

    private val win = Window("SettingsScreen", assets.skin, Styles.windowStyle)
    private val titleLabel = Label(assets.getString(Assets.CONFIG), assets.skin)

    private val soundButton = TextButton(assets.getString(Assets.CONFIG_SOUND_EFFECTS_ONOF), assets.skin, "toggle")
    private val soundLabel = Label(assets.getString(Assets.CONFIG_SOUND_EFFECTS_VOLUME), assets.skin)
    private val soundSlider = Slider( 0f, 1f, 0.1f,false, assets.skin)

    private val musicButton = TextButton(assets.getString(Assets.CONFIG_MUSIC_ONOF), assets.skin, "toggle")
    private val musicLabel = Label(assets.getString(Assets.CONFIG_MUSIC_VOLUME), assets.skin)
    private val musicSlider = Slider( 0f, 1f, 0.1f,false, assets.skin)

    init {
        configure()
        setListeners()
    }

    //______________________________________________________________________________________________
    private fun configure() {
        /// Background image
        backgroundImage.setSize(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT)
        stage.addActor(backgroundImage)

        /// Window
        win.setSize(CesDoom.VIRTUAL_WIDTH-80, CesDoom.VIRTUAL_HEIGHT-80)
        win.setPosition(50f, 100f)
        win.touchable = Touchable.disabled
        stage.addActor(win)

        var y = CesDoom.VIRTUAL_HEIGHT - 5f

        /// Title
        titleLabel.setColor(.9f, .9f, .9f, 1f)
        titleLabel.setFontScale(2f)
        titleLabel.setSize(500f, 70f)
        titleLabel.setPosition(100f, y - titleLabel.height - 5f)
        stage.addActor(titleLabel)
        y -= titleLabel.height*2.5f

        /// Sound On/Off ---------------------------------------------------------------------------
        soundButton.isChecked = Settings.isSoundEnabled
        soundButton.setSize(350f, 80f)
        soundButton.setPosition(CesDoom.VIRTUAL_WIDTH/2 - soundButton.width/2, y)
        stage.addActor(soundButton)
        stage.addFocusableActor(soundButton)
        y -= soundButton.height/2f
        /// Sound Effects Volume -------------------------------------------------------------------
        soundLabel.setSize(500f, 30f)
        soundLabel.setPosition(CesDoom.VIRTUAL_WIDTH/2 - soundLabel.width/2, y)
        stage.addActor(soundLabel)
        y -= soundLabel.height
        //
        soundSlider.value = Settings.soundVolume
        soundSlider.setSize(550f, 30f)
        soundSlider.setPosition(CesDoom.VIRTUAL_WIDTH/2 - soundSlider.width/2, y)
        stage.addActor(soundSlider)
        stage.addFocusableActor(soundSlider)
        y -= soundSlider.height*4f

        /// Music On/Off ---------------------------------------------------------------------------
        musicButton.isChecked = Settings.isMusicEnabled
        musicButton.setSize(250f, 80f)
        musicButton.setPosition(CesDoom.VIRTUAL_WIDTH/2 - musicButton.width/2, y)
        stage.addActor(musicButton)
        stage.addFocusableActor(musicButton)
        y -= musicButton.height/2f
        /// Music Volume ---------------------------------------------------------------------------
        musicLabel.setSize(500f, 30f)
        musicLabel.setPosition(CesDoom.VIRTUAL_WIDTH/2 - soundLabel.width/2, y)
        stage.addActor(musicLabel)
        y -= musicLabel.height
        //
        musicSlider.value = Settings.musicVolume
        musicSlider.setSize(550f, 30f)
        musicSlider.setPosition(CesDoom.VIRTUAL_WIDTH/2 - musicSlider.width/2, y)
        stage.addActor(musicSlider)
        stage.addFocusableActor(musicSlider)
        y -= musicSlider.height

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
    private fun goSound(invert: Boolean = true) {
        if(invert)soundButton.isChecked = !soundButton.isChecked
        Settings.isSoundEnabled = soundButton.isChecked
    }
    private fun goMusic(invert: Boolean = true) {
        if(invert)musicButton.isChecked = !musicButton.isChecked
        Settings.isMusicEnabled = musicButton.isChecked
    }
    private fun goSoundLevel() {
        if(soundSlider.value == 1f)
            soundSlider.value = 0f
        else
            soundSlider.value += 0.1f
        Settings.soundVolume = soundSlider.value
        if(soundSlider.value == 0f) {
            soundButton.isChecked = false
            Settings.isSoundEnabled = false
        }
        else {
            soundButton.isChecked = true
            Settings.isSoundEnabled = true
        }
	}
    private fun goMusicLevel() {
		if(musicSlider.value == 1f)
			musicSlider.value = 0f
		else
			musicSlider.value += 0.1f
        Settings.musicVolume = musicSlider.value
        if(musicSlider.value == 0f) {
            musicButton.isChecked = false
            Settings.isMusicEnabled = false
        }
        else {
            musicButton.isChecked = true
            Settings.isMusicEnabled = true
        }
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
        soundSlider.addListener {
            Settings.soundVolume = soundSlider.value
            if(soundSlider.value == 0f) {
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
            goSound(false)
            return@addListener false
        }
        //
        musicSlider.addListener {
            Settings.musicVolume = musicSlider.value
            if(musicSlider.value == 0f) {
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
            goMusic(false)
            return@addListener false
        }
    }

    //______________________________________________________________________________________________
    /// Implements: Screen
    private var inputDelay = 0f
    override fun render(delta: Float) {
        inputDelay+=delta
        if(inputDelay > .125f) {
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
        NONE, BACK, MAIN, SOUND, SOUND_LEVEL, MUSIC, MUSIC_LEVEL
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
                ButtonFocus.SOUND -> currentFocus = ButtonFocus.SOUND_LEVEL
                ButtonFocus.SOUND_LEVEL -> currentFocus = ButtonFocus.MUSIC
                ButtonFocus.MUSIC -> currentFocus = ButtonFocus.MUSIC_LEVEL
                ButtonFocus.MUSIC_LEVEL -> currentFocus = ButtonFocus.MAIN
                ButtonFocus.MAIN -> currentFocus = ButtonFocus.BACK
                else -> Unit
            }
        }
        else if(backwards) {
            when(currentFocus) {
                ButtonFocus.NONE -> currentFocus = ButtonFocus.BACK
                ButtonFocus.BACK -> currentFocus = ButtonFocus.MAIN
                ButtonFocus.MAIN -> currentFocus = ButtonFocus.MUSIC_LEVEL
                ButtonFocus.MUSIC_LEVEL -> currentFocus = ButtonFocus.MUSIC
                ButtonFocus.MUSIC -> currentFocus = ButtonFocus.SOUND_LEVEL
                ButtonFocus.SOUND_LEVEL -> currentFocus = ButtonFocus.SOUND
                else -> Unit
            }
        }
    }
    private fun updateFocusColor() {
        if(backButton.color.a != 0f) {
            backButton.color = Styles.colorNormal1
            mainButton.color = Styles.colorNormal1
            soundButton.color = Styles.colorNormal1
            soundSlider.color = Styles.colorNormal1
            musicButton.color = Styles.colorNormal1
            musicSlider.color = Styles.colorNormal1
        }
        when (currentFocus) {
            ButtonFocus.NONE -> Unit
            ButtonFocus.SOUND -> soundButton.color = Styles.colorSelected1
            ButtonFocus.SOUND_LEVEL -> soundSlider.color = Styles.colorSelected1
            ButtonFocus.MUSIC -> musicButton.color = Styles.colorSelected1
            ButtonFocus.MUSIC_LEVEL -> musicSlider.color = Styles.colorSelected1
            ButtonFocus.MAIN -> mainButton.color = Styles.colorSelected1
            ButtonFocus.BACK -> backButton.color = Styles.colorSelected1
        }
    }
    private fun processSelectedButton() {
        when (currentFocus) {
            ButtonFocus.NONE -> Unit
            ButtonFocus.SOUND -> goSound()
            ButtonFocus.SOUND_LEVEL -> goSoundLevel()
            ButtonFocus.MUSIC -> goMusic()
            ButtonFocus.MUSIC_LEVEL -> goMusicLevel()
            ButtonFocus.MAIN -> goMain()
            ButtonFocus.BACK -> goBack()
        }
    }

    companion object {
        private val tag: String = SoundSettingsScreen::class.simpleName!!
    }
}
