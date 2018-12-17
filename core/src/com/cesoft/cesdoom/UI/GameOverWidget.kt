package com.cesoft.cesdoom.UI

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.cesoft.cesdoom.Assets
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.Settings
import com.cesoft.cesdoom.components.PlayerComponent


////////////////////////////////////////////////////////////////////////////////////////////////////
// TODO: Cambiar estilo botones : imagenes?
class GameOverWidget(private val game: CesDoom, stage: Stage) : Actor() {
	private var image: Image = Image(Texture(Gdx.files.internal("data/gameOver.png")))
	private var btnJugar: TextButton
	private var btnSalir: TextButton

	init {
		super.setStage(stage)
		btnJugar = TextButton(game.assets.getString(Assets.JUGAR), game.assets.skin)
		btnSalir = TextButton(game.assets.getString(Assets.SALIR), game.assets.skin)
		setListeners()
	}

	//______________________________________________________________________________________________
	private fun setListeners() {
		btnJugar.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				game.reset()
				this@GameOverWidget.clear()
				//TODO: borrar botones
			}
		})
		btnSalir.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				Gdx.app.exit()
			}
		})
	}

	/*private fun reanudar() {
		window.remove()
		Gdx.input.isCursorCatched = true
		Settings.paused = false
	}*/

	//______________________________________________________________________________________________
	override fun setPosition(x: Float, y: Float) {
		super.setPosition(0f, 0f)
		image.setPosition(x, y+50)
		val x0 = (CesDoom.VIRTUAL_WIDTH - btnJugar.width - btnSalir.width - 10)/2
		btnJugar.setPosition(x0, y-50)
		btnSalir.setPosition(btnJugar.x+btnJugar.width+10, y-50)
	}

	//______________________________________________________________________________________________
	override fun setSize(width: Float, height: Float) {
		super.setSize(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT)
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
