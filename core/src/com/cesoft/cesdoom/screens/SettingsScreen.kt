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
import com.badlogic.gdx.utils.Align.center
import javax.swing.text.StyleConstants.setAlignment
import com.badlogic.gdx.scenes.scene2d.ui.Skin





////////////////////////////////////////////////////////////////////////////////////////////////////
// https://www.gamedevelopment.blog/full-libgdx-game-tutorial-menu-control/
//TODO: Scroll
//TODO: Vibracion cuando te muerden SI/NO
class SettingsScreen(internal val game: CesDoom, private val assets: Assets) : Screen, InputProcessor {

    private var stage = Stage(FitViewport(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT))
    private var backgroundImage = Image(Texture(Gdx.files.internal("data/background.png")))
    private var backButton = TextButton(assets.getString(Assets.ATRAS), assets.skin)

    private val win = Window("SettingsScreen", assets.skin, "special")
    private val titleLabel = Label(assets.getString(Assets.CONFIG), assets.skin)

    private val soundButton = TextButton(assets.getString(Assets.CONFIG_SOUND_EFFECTS_ONOF), assets.skin, "toggle")
    private val soundVolumeLabel = Label(assets.getString(Assets.CONFIG_SOUND_EFFECTS_VOLUME), assets.skin)
    private val soundVolumeSlider = Slider( 0f, 1f, 0.1f,false, assets.skin)

    private val musicButton = TextButton(assets.getString(Assets.CONFIG_MUSIC_ONOF), assets.skin, "toggle")
    private val musicVolumeLabel = Label(assets.getString(Assets.CONFIG_MUSIC_VOLUME), assets.skin)
    private val musicVolumeSlider = Slider( 0f, 1f, 0.1f,false, assets.skin)

    private val painVibrationButton = TextButton(assets.getString(Assets.CONFIG_VIBRATION_ONOF), assets.skin, "toggle")


    init {
        configure()
        setListeners()
        //Gdx.input.inputProcessor = this
        Gdx.input.inputProcessor = stage
    }

    //______________________________________________________________________________________________
    private fun configure() {

        backgroundImage.setSize(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT)

        backButton.setSize(175f, 85f)
        backButton.setPosition(CesDoom.VIRTUAL_WIDTH - backButton.width - 5, 5f)

        // Inside Window
        val xWin = 50f
        val yWin = 100f
        win.setSize(CesDoom.VIRTUAL_WIDTH-100, CesDoom.VIRTUAL_HEIGHT-100)
        win.setPosition(xWin, yWin)

        // Title
        var cy = 10f + yWin
        titleLabel.setColor(.9f, .9f, .9f, 1f)
        titleLabel.setFontScale(2f)
        titleLabel.setSize(500f, 70f)
        titleLabel.setPosition(xWin+10f, win.height - cy)
        cy += titleLabel.height + 10f

        // Sound On/Off
        soundButton.isChecked = Settings.isSoundEnabled
        soundButton.setSize(350f, 80f)
        soundButton.setPosition(xWin, win.height - cy)
        cy += soundButton.height + 0f -55f
        //Sound Effects Volume
        soundVolumeLabel.setSize(500f, 30f)
        soundVolumeLabel.setPosition(xWin+10f, win.height - cy)
        cy += soundVolumeLabel.height + 0f
        soundVolumeSlider.value = Settings.soundVolume
        soundVolumeSlider.setSize(550f, 30f)
        soundVolumeSlider.setPosition(xWin+10f, win.height - cy)
        cy += soundVolumeSlider.height + 80f

        // Music On/Off
        musicButton.isChecked = Settings.isMusicEnabled
        musicButton.setSize(250f, 80f)
        musicButton.setPosition(xWin, win.height - cy)
        cy += musicButton.height + 0f -55f
        //Music Volume
        musicVolumeLabel.setSize(500f, 30f)
        musicVolumeLabel.setPosition(xWin+10f, win.height - cy)
        cy += musicVolumeLabel.height + 0f
        musicVolumeSlider.value = Settings.musicVolume
        musicVolumeSlider.setSize(550f, 30f)
        musicVolumeSlider.setPosition(xWin+10f, win.height - cy)



        val scrollTable = Table()
        scrollTable.add(titleLabel).width(500f).height(70f)
        scrollTable.row()
        scrollTable.add(soundButton).size(350f, 80f)
        scrollTable.row()
        scrollTable.add(soundVolumeLabel).size(500f, 30f)
        scrollTable.row()
        scrollTable.add(soundVolumeSlider).size(550f, 30f)
        scrollTable.row()
        scrollTable.add(musicButton).size(250f, 80f)
        scrollTable.row()
        scrollTable.add(musicVolumeLabel).size(500f, 30f)
        scrollTable.row()
        scrollTable.add(musicVolumeSlider).size(550f, 30f)
        scrollTable.row()
        scrollTable.add(painVibrationButton).size(650f, 80f)
        scrollTable.row()
        //
        /*scrollTable.add(Label(assets.getString(Assets.CONFIG_SOUND_EFFECTS_VOLUME), assets.skin))
        scrollTable.row()
        scrollTable.add(Label(assets.getString(Assets.CONFIG_SOUND_EFFECTS_VOLUME), assets.skin))
        scrollTable.row()
        scrollTable.add(Label(assets.getString(Assets.CONFIG_SOUND_EFFECTS_VOLUME), assets.skin))
        scrollTable.row()
        scrollTable.add(Label(assets.getString(Assets.CONFIG_SOUND_EFFECTS_VOLUME), assets.skin))
        scrollTable.row()
        scrollTable.add(Label("xxxxxxxxxx0xxxxxxxxxxxxxx", assets.skin))
        scrollTable.row()
        scrollTable.add(Label("yyyyyyyyyyyyyyyyyyyyyyyyyy", assets.skin))
        scrollTable.row()
        scrollTable.add(Label("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz", assets.skin))
        scrollTable.row()
        scrollTable.add(Label("xxxxxxxxxxx1xxxxxxxxxxxxx", assets.skin))
        scrollTable.row()
        scrollTable.add(Label("yyyyyyyyyyyyyyyyyyyyyyyyyy", assets.skin))
        scrollTable.row()
        scrollTable.add(Label("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz", assets.skin))
        scrollTable.row()
        scrollTable.add(Label("xxxxxxxxxxx2xxxxxxxxxxxxx", assets.skin))
        scrollTable.row()
        scrollTable.add(Label("yyyyyyyyyyyyyyyyyyyyyyyyyy", assets.skin))
        scrollTable.row()
        scrollTable.add(Label("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz", assets.skin))
        scrollTable.row()
        scrollTable.add(Label("xxxxxxxxxxxx3xxxxxxxxxxxx", assets.skin))
        scrollTable.row()
        scrollTable.add(Label("yyyyyyyyyyyyyyyyyyyyyyyyyy", assets.skin))
        scrollTable.row()
        scrollTable.add(Label("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz", assets.skin))
        scrollTable.row()
        scrollTable.add(Label("xxxxxxxxxxxxx4xxxxxxxxxxx", assets.skin))
        scrollTable.row()
        scrollTable.add(Label("yyyyyyyyyyyyyyyyyyyyyyyyyy", assets.skin))
        scrollTable.row()
        scrollTable.add(Label("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz", assets.skin))
        scrollTable.row()
        scrollTable.add(Label("xxxxxxxxxxxxx5xxxxxxxxxxx", assets.skin))
        scrollTable.row()
        scrollTable.add(Label("yyyyyyyyyyyyyyyyyyyyyyyyyy", assets.skin))
        scrollTable.row()
        scrollTable.add(Label("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz", assets.skin))
        scrollTable.row()
        scrollTable.add(Label("xxxxxxxxxxxxx5xxxxxxxxxxx", assets.skin))
        scrollTable.row()
        scrollTable.add(Label("yyyyyyyyyyyyyyyyyyyyyyyyyy", assets.skin))
        scrollTable.row()
        scrollTable.add(Label("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz", assets.skin))
        scrollTable.row()*/

        val scroller = ScrollPane(scrollTable)
        val table = Table()
        table.setFillParent(true)
        table.add(scroller).fill().expand()



        stage.addActor(backgroundImage)
        stage.addActor(backButton)
        stage.addActor(win)
        win.addActor(table)
        //stage.addActor(table)


/*
        backgroundImage.setSize(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT)

        backButton.setSize(175f, 85f)
        backButton.setPosition(CesDoom.VIRTUAL_WIDTH - backButton.width - 5, 5f)

        // Inside Window
        val xWin = 50f
        val yWin = 100f
        win.setSize(CesDoom.VIRTUAL_WIDTH-100, CesDoom.VIRTUAL_HEIGHT-100)
        win.setPosition(xWin, yWin)
        win.zIndex = 10
        //win.touchable = Touchable.disabled

        // Title
        var cy = 10f + yWin
        titleLabel.setColor(.9f, .9f, .9f, 1f)
        titleLabel.setFontScale(2f)
        titleLabel.setSize(500f, 70f)
        titleLabel.setPosition(xWin+10f, win.height - cy)
        cy += titleLabel.height + 10f

        // Sound On/Off
        soundButton.isChecked = Settings.isSoundEnabled
        soundButton.setSize(350f, 80f)
        soundButton.setPosition(xWin, win.height - cy)
        cy += soundButton.height + 0f -55f
        //Sound Effects Volume
        soundVolumeLabel.setSize(500f, 30f)
        soundVolumeLabel.setPosition(xWin+10f, win.height - cy)
        cy += soundVolumeLabel.height + 0f
        soundVolumeSlider.value = Settings.soundVolume
        soundVolumeSlider.setSize(550f, 30f)
        soundVolumeSlider.setPosition(xWin+10f, win.height - cy)
        cy += soundVolumeSlider.height + 80f

        // Music On/Off
        musicButton.isChecked = Settings.isMusicEnabled
        musicButton.setSize(250f, 80f)
        musicButton.setPosition(xWin, win.height - cy)
        cy += musicButton.height + 0f -55f
        //Music Volume
        musicVolumeLabel.setSize(500f, 30f)
        musicVolumeLabel.setPosition(xWin+10f, win.height - cy)
        cy += musicVolumeLabel.height + 0f
        musicVolumeSlider.value = Settings.musicVolume
        musicVolumeSlider.setSize(550f, 30f)
        musicVolumeSlider.setPosition(xWin+10f, win.height - cy)

        win.addActor(titleLabel)
        win.addActor(soundButton)
        win.addActor(soundVolumeLabel)
        win.addActor(soundVolumeSlider)
        win.addActor(musicButton)
        win.addActor(musicVolumeLabel)
        win.addActor(musicVolumeSlider)

		stage.addActor(backgroundImage)
        stage.addActor(backButton)
        stage.addActor(win)
        //stage.isDebugAll = true
        */
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

        soundVolumeSlider.addListener {
            Settings.soundVolume = soundVolumeSlider.value
            return@addListener false
        }
        soundButton.addListener {
            Settings.isSoundEnabled = soundButton.isChecked
            return@addListener false
        }

        musicVolumeSlider.addListener {
            Settings.musicVolume = musicVolumeSlider.value
            return@addListener false
        }
        musicButton.addListener {
            Settings.isMusicEnabled = musicButton.isChecked
            return@addListener false
        }
    }

    //______________________________________________________________________________________________
    /// Implements: Screen
    override fun render(delta: Float) {
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
}
