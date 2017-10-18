package com.cesoft.cesgame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.FitViewport
import com.cesoft.cesgame.Assets
import com.cesoft.cesgame.CesGame
import com.cesoft.cesgame.Settings

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class LeaderboardsScreen(internal var game: CesGame, private val assets: Assets) : Screen {
	private var stage: Stage = Stage(FitViewport(CesGame.VIRTUAL_WIDTH, CesGame.VIRTUAL_HEIGHT))
	private var backgroundImage: Image = Image(Texture(Gdx.files.internal("data/backgroundMN.png")))
	private var backButton: TextButton
	private var label: Array<Label>
	private var loaded: Boolean = false

	init {
		backButton = TextButton("Back", assets.skin)


		label = arrayOf(Label("", assets.skin))
		label[0] = Label("Loading scores from online leaderborads...", assets.skin)
		label[0].setFontScale(3f)
		label[0].setPosition(15f, CesGame.VIRTUAL_HEIGHT - label[0].height - 25)
		Settings.load(label)
		//        for (int i = 0; i < label.length; i++) label[i] = new Label(i + 1 + ") " + Settings.highscores[i], Assets.skin);


		configureWidgers()
		setListeners()
		Gdx.input.inputProcessor = stage
	}


	private fun configureWidgers() {
		backgroundImage.setSize(CesGame.VIRTUAL_WIDTH, CesGame.VIRTUAL_HEIGHT)
		backButton.setSize(128f, 64f)
		backButton.setPosition(CesGame.VIRTUAL_WIDTH - backButton.width - 5, 5f)

		stage.addActor(backgroundImage)
		stage.addActor(backButton)


		stage.addActor(label[0])
	}

	private fun setListeners() {
		backButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				game.setScreen(MainMenuScreen(game, assets))
			}
		})
	}

	override fun render(delta: Float) {
		stage.act(delta)
		updateLeaderboard()
		stage.draw()
	}

	fun updateLeaderboard() {
		if( ! loaded) {
			loaded = true
			var y = 0
			for(i in label.indices) {
				//if(label[i]==null)continue
				label[i].setFontScale(3f)
				label[i].setPosition(15f, CesGame.VIRTUAL_HEIGHT - label[i].height - 25 - y)
				y += 96
				stage.addActor(label[i])
			}
		}
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