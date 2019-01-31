package com.cesoft.cesdoom.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.GameWorld
import com.cesoft.cesdoom.Status
import com.cesoft.cesdoom.ui.GameUI


/**
 * Created by ccasanova on 03/12/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameVrScreen(private var game: CesDoom): Screen {//, CardBoardAndroidApplication, CardBoardApplicationListener {
	//private var gameUI: GameUI = GameUI(game, assets)
	private var gameWorld: GameWorld = GameWorld(game)

	init {
		Status.paused = false
		Gdx.input.inputProcessor = CesDoom.instance.gameUI.stage
		Gdx.input.isCursorCatched = true
	}

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
		gameWorld.dispose()
	}

	override fun show() {}
	override fun pause() {}
	override fun resume() {}
	override fun hide() {}
}
