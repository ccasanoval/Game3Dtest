package com.cesoft.cesgame.UI

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.cesoft.cesgame.Assets
import com.cesoft.cesgame.CesGame
import com.cesoft.cesgame.Settings
import com.cesoft.cesgame.components.PlayerComponent
import com.cesoft.cesgame.screens.GameScreen


////////////////////////////////////////////////////////////////////////////////////////////////////
// TODO: Cambiar estilo botones : imagenes?
class GameOverWidget(private val game: CesGame, stage: Stage) : Actor() {
	private var image: Image = Image(Texture(Gdx.files.internal("data/gameOver.png")))
	private var retryB: TextButton
	//private var leaderB: TextButton
	private var quitB: TextButton

	init {
		super.setStage(stage)
		val assets = Assets()
		retryB = TextButton(CesGame.JUGAR, assets.skin)
		//leaderB = TextButton(CesGame.PUNTOS, assets.skin)
		quitB = TextButton(CesGame.SALIR, assets.skin)
		assets.dispose()
		//
		setListeners()
	}

	//______________________________________________________________________________________________
	private fun setListeners() {
		retryB.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				game.setScreen(GameScreen(game))
			}
		})
//		leaderB.addListener(object : ClickListener() {
//			override fun clicked(event: InputEvent?, x: Float, y: Float) {
//				game.setScreen(LeaderboardsScreen(game))
//			}
//		})
		quitB.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				Gdx.app.exit()
			}
		})
	}

	//______________________________________________________________________________________________
	override fun setPosition(x: Float, y: Float) {
		super.setPosition(0f, 0f)
		image.setPosition(x, y+30)
		val x0 = (CesGame.VIRTUAL_WIDTH - retryB.width - quitB.width - 10)/2
		retryB.setPosition(x0, y-40)
		quitB.setPosition(retryB.x+retryB.width+10, y-40)
		//leaderB.setPosition(x + retryB.width - 25, y - 96)
	}

	//______________________________________________________________________________________________
	override fun setSize(width: Float, height: Float) {
		super.setSize(CesGame.VIRTUAL_WIDTH, CesGame.VIRTUAL_HEIGHT)
		image.setSize(width, height)
		retryB.setSize(width/1.5f, height/2)
		quitB.setSize(width/1.5f, height/2)
		//leaderB.setSize(width / 2.5f, height / 2)
	}

	//______________________________________________________________________________________________
	fun gameOver() {
		stage.addActor(image)
		stage.addActor(retryB)
		//stage.addActor(leaderB)
		stage.addActor(quitB)
		stage.unfocus(stage.keyboardFocus)
		Gdx.input.isCursorCatched = false
		Settings.sendScore(PlayerComponent.score)
	}
}
