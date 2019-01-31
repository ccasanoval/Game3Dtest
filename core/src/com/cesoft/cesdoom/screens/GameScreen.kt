package com.cesoft.cesdoom.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.GameWorld
import com.cesoft.cesdoom.Status
import com.cesoft.cesdoom.assets.Sounds
import com.cesoft.cesdoom.systems.RenderSystem
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameScreen : Screen {
	private var gameWorld: GameWorld = GameWorld(CesDoom.instance)

	companion object {
		val tag: String = GameScreen::class.java.simpleName
	}
	init {
		Status.paused = false
		Gdx.input.inputProcessor = CesDoom.instance.gameUI.stage
		Gdx.input.isCursorCatched = true
		Sounds.playMusic()
	}

	val render: RenderSystem
		get() = gameWorld.renderSystem

	override fun render(delta: Float) {
		CesDoom.instance.gameUI.update(delta)
		gameWorld.render(delta)
		CesDoom.instance.gameUI.render()
	}

	override fun resize(width: Int, height: Int) {
		CesDoom.instance.gameUI.resize(width, height)
		gameWorld.resize(width, height)
	}

	override fun dispose() {
		Sounds.stopMusic()
		gameWorld.dispose()
		Log.e("GameScreen", "dispose------------------------------------------------")
	}

	override fun show() {}
	override fun pause() {
		Log.e("GameScreen", "pause------------------------------------------------")
		gameWorld.pause()
	}
	override fun resume() {
		gameWorld.resume()
	}
	override fun hide() {}
}
