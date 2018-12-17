package com.cesoft.cesdoom.UI

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.viewport.FitViewport
import com.cesoft.cesdoom.Assets
import com.cesoft.cesdoom.CesDoom


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameUI(game: CesDoom, assets: Assets) {

	var stage: Stage = Stage(FitViewport(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT))
	var healthWidget: HealthWidget = HealthWidget(assets)
		private set
	private var scoreWidget: ScoreWidget = ScoreWidget(assets)
	private var pauseWidget: PauseWidget = PauseWidget(game, stage, assets)
	private var crosshairWidget: CrosshairWidget = CrosshairWidget()
	private var fpsLabel: Label = Label("", assets.skin)
	var gameOverWidget: GameOverWidget = GameOverWidget(game, stage, assets)
		private set

	init {
		configureWidgets()
	}

	private fun configureWidgets() {
		healthWidget.setSize(225f, 60f)
		healthWidget.setPosition(CesDoom.VIRTUAL_WIDTH/2 - healthWidget.width/2, 0f)

		//TODO: ammoWidget -> Muestra la municion disponible ¿recargar?
		scoreWidget.setSize(225f, 25f)
		scoreWidget.setPosition(0f, CesDoom.VIRTUAL_HEIGHT - scoreWidget.height)

		pauseWidget.setSize(500f, 350f)
		pauseWidget.setPosition(CesDoom.VIRTUAL_WIDTH - pauseWidget.width, CesDoom.VIRTUAL_HEIGHT - pauseWidget.height)

		gameOverWidget.setSize(CesDoom.VIRTUAL_WIDTH-100, CesDoom.VIRTUAL_HEIGHT-120)
		gameOverWidget.setPosition((CesDoom.VIRTUAL_WIDTH - gameOverWidget.width)/2, (CesDoom.VIRTUAL_HEIGHT - gameOverWidget.height)/2)

		crosshairWidget.setPosition(CesDoom.VIRTUAL_WIDTH / 2 - 16, CesDoom.VIRTUAL_HEIGHT / 2 - 16)
		crosshairWidget.setSize(32f, 32f)

		fpsLabel.setPosition(0f, 10f)

		stage.addActor(healthWidget)
		stage.addActor(scoreWidget)
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
