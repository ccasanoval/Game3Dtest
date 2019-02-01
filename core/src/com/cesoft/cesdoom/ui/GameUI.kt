package com.cesoft.cesdoom.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.viewport.FitViewport
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.util.Log


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameUI {

	var stage = Stage(FitViewport(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT))
	var healthWidget = HealthWidget()
		private set
	private var scoreWidget: ScoreWidget = ScoreWidget()
	private var messageWidget: MessageWidget = MessageWidget()
	private var pauseWidget: PauseWidget = PauseWidget(stage)
	private var crosshairWidget: CrosshairWidget = CrosshairWidget()
	private var fpsLabel: Label = Label("", CesDoom.instance.assets.skin)
	var gameOverWidget: GameOverWidget = GameOverWidget(stage)
		private set
	var gameWinWidget: GameWinWidget = GameWinWidget(stage)
		private set

	init {
		configureWidgets()
	}

	private fun configureWidgets() {
		//TODO: ammoWidget -> Muestra la municion disponible ¿recargar?

		fpsLabel.setPosition(0f, 10f)

		stage.addActor(healthWidget)
		stage.addActor(scoreWidget)
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
