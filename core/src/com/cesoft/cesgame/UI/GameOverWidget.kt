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
class GameOverWidget(private val game: CesGame, stage: Stage, private val assets: Assets) : Actor() {
	private var image: Image = Image(Texture(Gdx.files.internal("data/gameOver.png")))
	private var btnJugar: TextButton
	private var btnSalir: TextButton

	init {
		super.setStage(stage)
		btnJugar = TextButton(assets.getString(Assets.JUGAR), assets.skin)
		btnSalir = TextButton(assets.getString(Assets.SALIR), assets.skin)
		//
		setListeners()
	}

	//______________________________________________________________________________________________
	private fun setListeners() {
		btnJugar.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				game.setScreen(GameScreen(game.gameUI, assets))
			}
		})

		btnSalir.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				Gdx.app.exit()
			}
		})
	}

	//______________________________________________________________________________________________
	override fun setPosition(x: Float, y: Float) {
		super.setPosition(0f, 0f)
		image.setPosition(x, y+50)
		val x0 = (CesGame.VIRTUAL_WIDTH - btnJugar.width - btnSalir.width - 10)/2
		btnJugar.setPosition(x0, y-50)
		btnSalir.setPosition(btnJugar.x+btnJugar.width+10, y-50)
	}

	//______________________________________________________________________________________________
	override fun setSize(width: Float, height: Float) {
		super.setSize(CesGame.VIRTUAL_WIDTH, CesGame.VIRTUAL_HEIGHT)
		image.setSize(width, height)
		btnJugar.setSize(width/1.75f, height/2.5f)
		btnSalir.setSize(width/1.75f, height/2.5f)
	}

	//______________________________________________________________________________________________
	fun gameOver() {
		stage.addActor(image)
		stage.addActor(btnJugar)
		//stage.addActor(leaderB)
		stage.addActor(btnSalir)
		stage.unfocus(stage.keyboardFocus)
		Gdx.input.isCursorCatched = false
		Settings.sendScore(PlayerComponent.score)
	}
}
