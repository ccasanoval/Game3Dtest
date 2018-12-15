package com.cesoft.cesdoom.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.FitViewport
import com.cesoft.cesdoom.Assets
import com.cesoft.cesdoom.CesDoom

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class MainMenuScreen(private val game: CesDoom) : Screen {
	private var stage: Stage = Stage(FitViewport(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT))
	private var backgroundImage: Image = Image(Texture(Gdx.files.internal("data/background.png")))
	private var titleImage: Image = Image(Texture(Gdx.files.internal("data/title.png")))
	private var playButton: TextButton = TextButton(game.assets.getString(Assets.JUGAR), game.assets.skin)
	private var quitButton: TextButton = TextButton(game.assets.getString(Assets.SALIR), game.assets.skin)
	private var aboutButton: TextButton = TextButton(game.assets.getString(Assets.SOBRE), game.assets.skin)

	init {
		configureWidgers()
		setListeners()
		Gdx.input.inputProcessor = stage
	}

	//______________________________________________________________________________________________
	private fun configureWidgers() {
		backgroundImage.setSize(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT)
		backgroundImage.setColor(1f, 1f, 1f, 0f)
		backgroundImage.addAction(Actions.fadeIn(0.65f))
		//
		titleImage.setColor(1f, 1f, 1f, 0f)
		titleImage.addAction(SequenceAction(Actions.delay(0.65f), Actions.fadeIn(0.75f)))
		titleImage.setPosition(
				(CesDoom.VIRTUAL_WIDTH - titleImage.width) / 2,
				(CesDoom.VIRTUAL_HEIGHT - titleImage.height) / 2 +80)
		//
		playButton.setSize(280f, 90f)
		playButton.label.setFontScale(2f)
		playButton.setPosition(CesDoom.VIRTUAL_WIDTH / 2 - playButton.width / 2, CesDoom.VIRTUAL_HEIGHT / 2 - 130)
		playButton.setColor(1f, 1f, 1f, 0f)
		playButton.addAction(SequenceAction(Actions.delay(1.20f), Actions.fadeIn(0.75f)))
		//
		aboutButton.setSize(280f, 90f)
		aboutButton.label.setFontScale(1.5f)
		aboutButton.setPosition(CesDoom.VIRTUAL_WIDTH / 2 - playButton.width / 2, CesDoom.VIRTUAL_HEIGHT / 2 - 200)
		aboutButton.setColor(1f, 1f, 1f, 0f)
		aboutButton.addAction(SequenceAction(Actions.delay(1.50f), Actions.fadeIn(0.75f)))
		//
		quitButton.setSize(280f, 90f)
		quitButton.label.setFontScale(2f)
		quitButton.setPosition(CesDoom.VIRTUAL_WIDTH / 2 - playButton.width / 2, CesDoom.VIRTUAL_HEIGHT / 2 - 270)
		quitButton.setColor(1f, 1f, 1f, 0f)
		quitButton.addAction(SequenceAction(Actions.delay(1.80f), Actions.fadeIn(0.75f)))
		//
		stage.addActor(backgroundImage)
		stage.addActor(titleImage)
		stage.addActor(playButton)
		stage.addActor(quitButton)
		stage.addActor(aboutButton)
	}

	private fun setListeners() {
		playButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				//game.setScreen(GameScreen(game.gameUI, assets))
				//TODO: loading bar...
				game.setScreen(LoadingScreen(game))
			}
		})
		quitButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				Gdx.app.exit()
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