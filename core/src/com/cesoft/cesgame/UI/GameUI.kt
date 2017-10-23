package com.cesoft.cesgame.UI

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.viewport.FitViewport
import com.cesoft.cesgame.Assets
import com.cesoft.cesgame.CesGame


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameUI(game: CesGame, assets: Assets) {

	var stage: Stage = Stage(FitViewport(CesGame.VIRTUAL_WIDTH, CesGame.VIRTUAL_HEIGHT))
	var healthWidget: HealthWidget = HealthWidget(assets)
		private set
	private var scoreWidget: ScoreWidget = ScoreWidget(assets)
	private var pauseWidget: PauseWidget = PauseWidget(game, stage, assets)
	private var crosshairWidget: CrosshairWidget = CrosshairWidget()
	private var fpsLabel: Label
	var gameOverWidget: GameOverWidget = GameOverWidget(game, stage, assets)
		private set

	init {
		fpsLabel = Label("", assets.skin)
		configureWidgets()
	}

	private fun configureWidgets() {
		healthWidget.setSize(225f, 60f)
		healthWidget.setPosition(CesGame.VIRTUAL_WIDTH/2 - healthWidget.width/2, 0f)

		//TODO: ammoWidget -> Muestra la municion disponible ¿recargar?
		scoreWidget.setSize(225f, 25f)
		scoreWidget.setPosition(0f, CesGame.VIRTUAL_HEIGHT - scoreWidget.height)

		pauseWidget.setSize(500f, 250f)
		pauseWidget.setPosition(CesGame.VIRTUAL_WIDTH - pauseWidget.width, CesGame.VIRTUAL_HEIGHT - pauseWidget.height)

		gameOverWidget.setSize(500f, 210f)
		gameOverWidget.setPosition((CesGame.VIRTUAL_WIDTH - 500)/2, (CesGame.VIRTUAL_HEIGHT-210)/2)

		crosshairWidget.setPosition(CesGame.VIRTUAL_WIDTH / 2 - 16, CesGame.VIRTUAL_HEIGHT / 2 - 16)
		crosshairWidget.setSize(32f, 32f)

		fpsLabel.setPosition(0f, 10f)

		stage.addActor(healthWidget)
		stage.addActor(scoreWidget)
		stage.addActor(crosshairWidget)
		stage.keyboardFocus = pauseWidget
		stage.addActor(fpsLabel)
		if(CesGame.isMobile)
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
