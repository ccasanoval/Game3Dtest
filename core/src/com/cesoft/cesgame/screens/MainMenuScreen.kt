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
class MainMenuScreen(private var game: CesGame) : Screen {
	private var stage: Stage = Stage(FitViewport(CesGame.VIRTUAL_WIDTH, CesGame.VIRTUAL_HEIGHT))
	private var backgroundImage: Image = Image(Texture(Gdx.files.internal("data/backgroundMN.png")))
	private var titleImage: Image = Image(Texture(Gdx.files.internal("data/title.png")))
	private var playButton: TextButton
	//private var leaderboardsButton: TextButton
	private var quitButton: TextButton

	init {
		val assets = Assets()
		playButton = TextButton(CesGame.JUGAR, assets.skin)
		//leaderboardsButton = TextButton(CesGame.PUNTOS, assets.skin)
		quitButton = TextButton(CesGame.SALIR, assets.skin)
		assets.dispose()

		configureWidgers()
		setListeners()
		Gdx.input.inputProcessor = stage
	}


	private fun configureWidgers() {
		backgroundImage.setSize(CesGame.VIRTUAL_WIDTH, CesGame.VIRTUAL_HEIGHT)
		//titleImage.setSize(620f, 200f)
		titleImage.setPosition(CesGame.VIRTUAL_WIDTH / 2 - titleImage.width / 2, CesGame.VIRTUAL_HEIGHT / 2)
		playButton.setSize(280f, 90f)
		playButton.label.setFontScale(2f)
		playButton.setPosition(CesGame.VIRTUAL_WIDTH / 2 - playButton.width / 2, CesGame.VIRTUAL_HEIGHT / 2 - 120)
//		leaderboardsButton.setSize(220f, 70f)
//		leaderboardsButton.label.setFontScale(2f)
//		leaderboardsButton.setPosition(CesGame.VIRTUAL_WIDTH / 2 - playButton.width / 2, CesGame.VIRTUAL_HEIGHT / 2 - 200)
		quitButton.setSize(280f, 90f)
		quitButton.label.setFontScale(2f)
		quitButton.setPosition(CesGame.VIRTUAL_WIDTH / 2 - playButton.width / 2, CesGame.VIRTUAL_HEIGHT / 2 - 200)

		stage.addActor(backgroundImage)
		stage.addActor(titleImage)
		stage.addActor(playButton)
		//stage.addActor(leaderboardsButton)
		stage.addActor(quitButton)
	}

	private fun setListeners() {
		playButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				game.setScreen(GameScreen(game))
			}
		})
//		leaderboardsButton.addListener(object : ClickListener() {
//			override fun clicked(event: InputEvent?, x: Float, y: Float) {
//				game.setScreen(LeaderboardsScreen(game))
//			}
//		})
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