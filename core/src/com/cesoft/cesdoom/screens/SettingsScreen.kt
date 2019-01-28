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


////////////////////////////////////////////////////////////////////////////////////////////////////
// https://www.gamedevelopment.blog/full-libgdx-game-tutorial-menu-control/
class SettingsScreen(internal val game: CesDoom) : Screen, InputProcessor {

    private var stage = Stage(FitViewport(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT))
    private var backgroundImage = Image(Texture(Gdx.files.internal("data/background.png")))
    private var backButton = TextButton(game.assets.getString(Assets.ATRAS), game.assets.skin)

    private val win = Window("SettingsScreen", game.assets.skin, "special")
    private val titleLabel = Label(game.assets.getString(Assets.CONFIG), game.assets.skin)
    private val soundButton = TextButton(game.assets.getString(Assets.CONFIG_SOUND_ONOF), game.assets.skin, "toggle")
    private val volumeLabel = Label(game.assets.getString(Assets.CONFIG_SOUND_VOLUME), game.assets.skin)
    private val volumeSlider = Slider( 0f, 1f, 0.1f,false, game.assets.skin)
    //private val titleSound = Label(game.assets.getString(Assets.CONFIG_SOUND_ONOF), game.assets.skin)
    //private val soundCheckbox = CheckBox(null, game.assets.skin)



    init {
        configureWidgers()
        setListeners()
        Gdx.input.inputProcessor = this
    }

    //______________________________________________________________________________________________
    private fun configureWidgers() {

        backgroundImage.setSize(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT)

        backButton.setSize(175f, 85f)
        backButton.setPosition(CesDoom.VIRTUAL_WIDTH - backButton.width - 5, 5f)

        // Inside Window
        val xWin = 50f
        val yWin = 100f
        win.setSize(CesDoom.VIRTUAL_WIDTH-100, CesDoom.VIRTUAL_HEIGHT-100)
        win.setPosition(xWin, yWin)
        win.zIndex = 10

        var cy = 10f
        titleLabel.setColor(.9f, .9f, .9f, 1f)
        titleLabel.setFontScale(2f)
        titleLabel.setSize(500f, 80f)
        titleLabel.setPosition(xWin+10f, win.height - cy - yWin)

        cy += titleLabel.height + 10f
        soundButton.isChecked = Settings.isSoundEnabled
        soundButton.setSize(400f, 80f)
        soundButton.setPosition(xWin, win.height - cy - yWin)

        cy += soundButton.height + 5f
        volumeLabel.setSize(500f, 60f)
        volumeLabel.setPosition(xWin+10f, win.height - cy - yWin)
        cy += volumeLabel.height + 0f
        volumeSlider.value = Settings.getMusicVolume()
        volumeSlider.setSize(550f, 80f)
        volumeSlider.setPosition(xWin+10f, win.height - cy - yWin)

        /*soundCheckbox.isChecked = Settings.isSoundEnabled
        soundCheckbox.setSize(350f, 80f)
        soundCheckbox.setPosition(20f, 120f)*/

        win.addActor(titleLabel)
        win.addActor(soundButton)
        win.addActor(volumeLabel)
        win.addActor(volumeSlider)

		stage.addActor(backgroundImage)
        stage.addActor(backButton)
        stage.addActor(win)
    }

    //______________________________________________________________________________________________
    private fun goBack() = game.setScreen(MainMenuScreen(game))
    private fun setListeners() {

        backButton.addListener {
            goBack()
            return@addListener false
        }
//        backButton.addListener(object : ClickListener() {
//            override fun clicked(event: InputEvent?, x: Float, y: Float) {
//                goBack()
//            }
//        })

        volumeSlider.addListener {
            Settings.setMusicVolume(volumeSlider.value)
            return@addListener false
        }
        soundButton.addListener {
            Settings.isSoundEnabled = soundButton.isChecked
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

    override fun show() {}
    override fun pause() {}
    override fun resume() {}
    override fun hide() {}

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
