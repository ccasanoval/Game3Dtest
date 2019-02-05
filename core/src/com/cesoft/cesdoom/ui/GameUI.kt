package com.cesoft.cesdoom.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.viewport.FitViewport
import com.cesoft.cesdoom.CesDoom


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameUI {

	var stage = Stage(FitViewport(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT))
	var healthWidget = HealthWidget()
		private set
	private var scoreWidget = ScoreWidget()
	private var ammoWidget = AmmoWidget()
	private var messageWidget = MessageWidget()
	private var pauseWidget = PauseWidget(stage)
	private var crosshairWidget = CrosshairWidget()
	private var fpsLabel = Label("", CesDoom.instance.assets.skin)
	var gameOverWidget = GameOverWidget(stage)
		private set
	var gameWinWidget = GameWinWidget(stage)
		private set

	init {
		configureWidgets()
	}

	private fun configureWidgets() {
		//TODO: ammoWidget -> Muestra la municion disponible ¿recargar?

		fpsLabel.setPosition(0f, 10f)

		stage.addActor(healthWidget)
		stage.addActor(scoreWidget)
		stage.addActor(ammoWidget)
		stage.addActor(messageWidget)
		stage.addActor(crosshairWidget)
		stage.keyboardFocus = pauseWidget
		stage.addActor(fpsLabel)
		if(CesDoom.isMobile)
			ControllerWidget().addToStage(stage)
	}

	fun update(delta: Float) {
		fpsLabel.setText("FPS: " + Gdx.graphics.framesPerSecond)
		stage.act(delta)
	}

	fun render() {
		stage.draw()
	}

	fun resize(width: Int, height: Int) {
		stage.viewport.update(width, height)
	}

	fun dispose() {
		stage.dispose()
	}
}
