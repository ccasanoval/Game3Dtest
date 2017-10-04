package com.cesoft.cesgame.UI

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.viewport.FitViewport
import com.cesoft.cesgame.Assets
import com.cesoft.cesgame.CesGame
import com.cesoft.cesgame.managers.ControllerWidget


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameUI(game: CesGame) {

	var stage: Stage = Stage(FitViewport(CesGame.VIRTUAL_WIDTH, CesGame.VIRTUAL_HEIGHT))
	var healthWidget: HealthWidget = HealthWidget()
		private set
	//  public OxygenWidget oxygenWidget;
	//  public EnergyWidget energyWidget;
	private var scoreWidget: ScoreWidget = ScoreWidget()
	private var pauseWidget: PauseWidget = PauseWidget(game, stage)
	private var crosshairWidget: CrosshairWidget = CrosshairWidget()
	private var fpsLabel: Label
	private var controllerWidget: ControllerWidget? = null
	var gameOverWidget: GameOverWidget = GameOverWidget(game, stage)
		private set

	init {
		//
		val assets = Assets()
		fpsLabel = Label("", assets.skin)
		assets.dispose()
		//
		configureWidgets()
	}

	private fun configureWidgets() {
		healthWidget.setSize(140f, 25f)
		healthWidget.setPosition(CesGame.VIRTUAL_WIDTH / 2 - healthWidget.width / 2, 0f)
		//      oxygenWidget.setSize(140, 25);
		//      oxygenWidget.setPosition(CesGame.VIRTUAL_WIDTH / 2 - oxygenWidget.getWidth() / 2, 30);
		//      energyWidget.setSize(140, 25);
		//      energyWidget.setPosition(CesGame.VIRTUAL_WIDTH / 2 - energyWidget.getWidth() / 2, 60);
		scoreWidget.setSize(140f, 25f)
		scoreWidget.setPosition(0f, CesGame.VIRTUAL_HEIGHT - scoreWidget.height)
		pauseWidget.setSize(64f, 64f)
		pauseWidget.setPosition(CesGame.VIRTUAL_WIDTH - pauseWidget.width, CesGame.VIRTUAL_HEIGHT - pauseWidget.height)
		gameOverWidget.setSize(280f, 100f)
		gameOverWidget.setPosition(CesGame.VIRTUAL_WIDTH / 2 - 280 / 2, CesGame.VIRTUAL_HEIGHT / 2)
		crosshairWidget.setPosition(CesGame.VIRTUAL_WIDTH / 2 - 16, CesGame.VIRTUAL_HEIGHT / 2 - 16)
		crosshairWidget.setSize(32f, 32f)

		fpsLabel.setPosition(0f, 10f)

		stage.addActor(healthWidget)
		//      stage.addActor(oxygenWidget);
		//      stage.addActor(energyWidget);
		stage.addActor(scoreWidget)
		stage.addActor(crosshairWidget)
		stage.keyboardFocus = pauseWidget
		stage.addActor(fpsLabel)
		if(Gdx.app.type == Application.ApplicationType.Android) {
			controllerWidget = ControllerWidget()
			controllerWidget!!.addToStage(stage)
		}
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
