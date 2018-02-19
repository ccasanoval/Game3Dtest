package com.cesoft.cesgame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.cesoft.cesgame.Assets
import com.cesoft.cesgame.GameWorld
import com.cesoft.cesgame.Settings
import com.cesoft.cesgame.UI.GameUI


/**
 * Created by ccasanova on 03/12/2017
 */
////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameVrScreen(private var gameUI: GameUI, assets: Assets): Screen {//, CardBoardAndroidApplication, CardBoardApplicationListener {
	//private var gameUI: GameUI = GameUI(game, assets)
	private var gameWorld: GameWorld = GameWorld(gameUI, assets)

	init {
		Settings.paused = false
		Gdx.input.inputProcessor = gameUI.stage
		Gdx.input.isCursorCatched = true
	}

	override fun render(delta: Float) {
		gameUI.update(delta)
		gameWorld.render(delta)
		gameUI.render()
	}

	override fun resize(width: Int, height: Int) {
		gameUI.resize(width, height)
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
