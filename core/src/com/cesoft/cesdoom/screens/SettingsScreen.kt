package com.cesoft.cesdoom.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.FitViewport
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.Settings

//Preferences preferences = Gdx.app.getPreferences(PreferencesSample.class.getName());
//preferences.putString("playerName", getOption("Player profile"));
//preferences.putInteger("difficulty", MathUtils.clamp(Integer.parseInt(getOption("Difficulty")), 0, 10));
//preferences.putFloat("effectsVolume", MathUtils.clamp(Float.parseFloat(getOption("Effects volume")), 0.0f, 100.0f));
//preferences.putBoolean("showTips", Boolean.parseBoolean(getOption("Show tips (true/false)")));
//preferences.flush();


//});
//private val parent: Box2DTutorial? = null
//private val stage: Stage? = null
//// our new fields
//private val titleLabel: Label? = null
//private val volumeMusicLabel: Label? = null
//private val volumeSoundLabel: Label? = null
//private val musicOnOffLabel: Label? = null
//private val soundOnOffLabel: Label? = null

//titleLabel = new Label( "Preferences", skin );
//volumeMusicLabel = new Label( null, skin );
//volumeSoundLabel = new Label( null, skin );
//musicOnOffLabel = new Label( null, skin );
//soundOnOffLabel = new Label( null, skin );
//
//table.add(titleLabel);
//table.row();
//table.add(volumeMusicLabel);
//table.add(volumeMusicSlider);
//table.row();
//table.add(musicOnOffLabel);
//table.add(musicCheckbox);
//table.row();
//table.add(volumeSoundLabel);
//table.add(soundMusicSlider);
//table.row();
//table.add(soundOnOffLabel);
//table.add(soundEffectsCheckbox);
//table.row();
//table.add(backButton);
//https://www.gamedevelopment.blog/full-libgdx-game-tutorial-menu-control/

////////////////////////////////////////////////////////////////////////////////////////////////////
// https://www.gamedevelopment.blog/full-libgdx-game-tutorial-menu-control/
class SettingsScreen(internal val game: CesDoom) : Screen, InputProcessor {

    private var stage: Stage = Stage(FitViewport(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT))
    private var backgroundImage: Image = Image(Texture(Gdx.files.internal("data/background.png")))
    private var backButton: TextButton = TextButton(game.assets.getString(Assets.ATRAS), game.assets.skin)
    private val soundButton = TextButton(game.assets.getString(Assets.CONFIG_SOUND), game.assets.skin, "toggle")
    private val tituloLabel: Label = Label(game.assets.getString(Assets.CONFIG), game.assets.skin)
    private val win = Window("ventana", game.assets.skin, "special")


    private val volumeSlider = Slider( 0f, 1f, 0.1f,false, game.assets.skin)
    private val soundCheckbox = CheckBox(null, game.assets.skin)



    init {
        configureWidgers()
        setListeners()
        Gdx.input.inputProcessor = this
    }

    //______________________________________________________________________________________________
    private fun configureWidgers() {
        volumeSlider.value = Settings.getMusicVolume()
        soundCheckbox.isChecked = Settings.isSoundEnabled

        backgroundImage.setSize(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT)

        backButton.setSize(175f, 85f)
        backButton.setPosition(CesDoom.VIRTUAL_WIDTH - backButton.width - 5, 5f)

        win.setSize(CesDoom.VIRTUAL_WIDTH-100, CesDoom.VIRTUAL_HEIGHT-100)
        win.setPosition(50f,100f)
        win.zIndex = 10
        //win.touchable = Touchable.disabled

        // Inside Window
        tituloLabel.setColor(.9f, .9f, .9f, 1f)
        tituloLabel.setFontScale(2f)

        soundButton.setSize(300f, 80f)
        soundButton.setPosition(500f, win.height-5f)

        win.addActor(tituloLabel)
        win.addActor(soundButton)
        win.addActor(soundCheckbox)
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
        soundCheckbox.addListener {
            Settings.isSoundEnabled = soundCheckbox.isChecked
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
