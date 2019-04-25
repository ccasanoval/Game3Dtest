package com.cesoft.cesdoom.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.viewport.FitViewport
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.Status
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.components.PlayerComponent


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameUI(game: CesDoom, assets: Assets) {

	var stage = Stage(FitViewport(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT))
	private var healthWidget = HealthWidget(assets)
	private var scoreWidget = ScoreWidget(assets)
	private var ammoWidget = AmmoWidget(assets)
	private var messageWidget = MessageWidget(assets)
	private var crossHairWidget = CrosshairWidget()
	private var fpsLabel = Label("", assets.skin)
	private var levelLabel = Label("", assets.skin)
	var pauseWidget = PauseWidget(game, stage, assets)
	var gameOverWidget = GameOverWidget(game, stage, assets)
		private set
	var gameWinWidget = GameWinWidget(game, stage, assets)
		private set

	init {
		configureWidgets()
	}

	private fun configureWidgets() {
		fpsLabel.setPosition(0f, 10f)
		levelLabel.setPosition(CesDoom.VIRTUAL_WIDTH-150, 10f)
		fpsLabel.setColor(1f, 0f, 0f, .5f)
		levelLabel.setColor(0f, 1f, 0f, .8f)

		stage.addActor(healthWidget)
		stage.addActor(scoreWidget)
		stage.addActor(ammoWidget)
		stage.addActor(messageWidget)
		stage.addActor(crossHairWidget)
		stage.keyboardFocus = pauseWidget
		stage.addActor(fpsLabel)
		stage.addActor(levelLabel)
		if(CesDoom.isMobile)
			ControllerWidget().addToStage(stage)
	}

	private var previousLevel = Int.MAX_VALUE
	fun update(delta: Float) {
		fpsLabel.setText("FPS: ${Gdx.graphics.framesPerSecond}")
		if(previousLevel != PlayerComponent.currentLevel && !Status.paused) {
			previousLevel = PlayerComponent.currentLevel
			levelLabel.setText("LEVEL: ${PlayerComponent.currentLevel}")
		}
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

	fun pause() {
		pauseWidget.pauseOnOf()
	}
}
