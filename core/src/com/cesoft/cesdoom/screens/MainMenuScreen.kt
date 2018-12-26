package com.cesoft.cesdoom.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.FitViewport
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.CesDoom

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class MainMenuScreen(private val game: CesDoom) : Screen {
	private var stage: Stage = Stage(FitViewport(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT))
	private var backgroundImage: Image = Image(Texture(Gdx.files.internal("data/background.png")))//TODO:AssetManager para no repetir carga...
	private var titleImage: Image = Image(Texture(Gdx.files.internal("data/title.png")))
	private var playButton: TextButton = TextButton(game.assets.getString(Assets.JUGAR), game.assets.skin)
	private var quitButton: TextButton = TextButton(game.assets.getString(Assets.SALIR), game.assets.skin)
	private var settingsButton: TextButton = TextButton(game.assets.getString(Assets.CONFIG), game.assets.skin)
	private var aboutButton: TextButton = TextButton(game.assets.getString(Assets.SOBRE), game.assets.skin)

	init {
		configureWidgers()
		setListeners()
		Gdx.input.inputProcessor = stage
	}

	//______________________________________________________________________________________________
	private fun configureWidgers() {
		var y = 110f
        val x = 200f
		var delay = 0.65f
		val fadeIn = 0.75f
		//
		backgroundImage.setSize(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT)
		backgroundImage.setColor(1f, 1f, 1f, 0f)
		backgroundImage.addAction(Actions.fadeIn(0.65f))
		//
		titleImage.setColor(1f, 1f, 1f, 0f)
		titleImage.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		titleImage.setPosition(
				(CesDoom.VIRTUAL_WIDTH - titleImage.width) / 2,
				(CesDoom.VIRTUAL_HEIGHT - titleImage.height) / 2 +y)
		y -= 250f
		delay += 0.55f
		//
		playButton.setSize(280f, 90f)
		playButton.label.setFontScale(2f)
		playButton.setPosition(CesDoom.VIRTUAL_WIDTH / 2 - playButton.width / 2 - x, CesDoom.VIRTUAL_HEIGHT / 2 +y)
		playButton.setColor(1f, 1f, 1f, 0f)
		playButton.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		//
		settingsButton.setSize(280f, 90f)
		settingsButton.label.setFontScale(1.5f)
		settingsButton.setPosition(CesDoom.VIRTUAL_WIDTH / 2 - playButton.width / 2 +x, CesDoom.VIRTUAL_HEIGHT / 2 +y)
		settingsButton.setColor(1f, 1f, 1f, 0f)
		settingsButton.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		delay += 0.30f
		y -= 90f
		//
		quitButton.setSize(280f, 90f)
		quitButton.label.setFontScale(2f)
		quitButton.setPosition(CesDoom.VIRTUAL_WIDTH / 2 - playButton.width / 2 -x, CesDoom.VIRTUAL_HEIGHT / 2 +y)
		quitButton.setColor(1f, 1f, 1f, 0f)
		quitButton.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		//
		aboutButton.setSize(280f, 90f)
		aboutButton.label.setFontScale(1.5f)
		aboutButton.setPosition(CesDoom.VIRTUAL_WIDTH / 2 - playButton.width / 2 +x, CesDoom.VIRTUAL_HEIGHT / 2 +y)
		aboutButton.setColor(1f, 1f, 1f, 0f)
		aboutButton.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		delay += 0.30f
		y -= 90f
		//
		stage.addActor(backgroundImage)
		stage.addActor(titleImage)
		stage.addActor(playButton)
		stage.addActor(settingsButton)
		stage.addActor(aboutButton)
		stage.addActor(quitButton)
	}

	private fun setListeners() {
		playButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				//game.setScreen(LoadingScreen(game))
				game.reset()
			}
		})
		quitButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				Gdx.app.exit()
			}
		})
        settingsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.setScreen(SettingsScreen(game))
            }
        })
		aboutButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				game.setScreen(AboutScreen(game))
			}
		})
	}

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
}