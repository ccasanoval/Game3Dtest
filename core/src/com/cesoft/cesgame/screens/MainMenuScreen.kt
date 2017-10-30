package com.cesoft.cesgame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.FitViewport
import com.cesoft.cesgame.Assets
import com.cesoft.cesgame.CesGame

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class MainMenuScreen(private val game: CesGame, private val assets: Assets) : Screen {
	private var stage: Stage = Stage(FitViewport(CesGame.VIRTUAL_WIDTH, CesGame.VIRTUAL_HEIGHT))
	private var backgroundImage: Image = Image(Texture(Gdx.files.internal("data/backgroundMN.png")))
	private var titleImage: Image = Image(Texture(Gdx.files.internal("data/title.png")))
	private var playButton: TextButton = TextButton(assets.getString(Assets.JUGAR), assets.skin)
	private var quitButton: TextButton = TextButton(assets.getString(Assets.SALIR), assets.skin)

	init {
		configureWidgers()
		setListeners()
		Gdx.input.inputProcessor = stage
	}

	//______________________________________________________________________________________________
	private fun configureWidgers() {
		backgroundImage.setSize(CesGame.VIRTUAL_WIDTH, CesGame.VIRTUAL_HEIGHT)
		titleImage.setPosition(
				(CesGame.VIRTUAL_WIDTH - titleImage.width) / 2,
				(CesGame.VIRTUAL_HEIGHT - titleImage.height) / 2 +50)
		//
		playButton.setSize(280f, 90f)
		playButton.label.setFontScale(2f)
		playButton.setPosition(CesGame.VIRTUAL_WIDTH / 2 - playButton.width / 2, CesGame.VIRTUAL_HEIGHT / 2 - 120)
		//
		quitButton.setSize(280f, 90f)
		quitButton.label.setFontScale(2f)
		quitButton.setPosition(CesGame.VIRTUAL_WIDTH / 2 - playButton.width / 2, CesGame.VIRTUAL_HEIGHT / 2 - 200)
		//
		stage.addActor(backgroundImage)
		stage.addActor(titleImage)
		stage.addActor(playButton)
		stage.addActor(quitButton)
	}

	private fun setListeners() {
		playButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				game.setScreen(GameScreen(game.gameUI, assets))
			}
		})
		quitButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				Gdx.app.exit()
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