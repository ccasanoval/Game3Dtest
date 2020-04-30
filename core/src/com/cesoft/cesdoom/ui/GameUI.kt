package com.cesoft.cesdoom.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.FitViewport
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.Status
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.components.PlayerComponent
import com.cesoft.cesdoom.util.Log
import de.golfgl.gdx.controllers.ControllerMenuStage


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameUI(val game: CesDoom, assets: Assets) {
	var stage = ControllerMenuStage(FitViewport(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT))
	private var healthWidget = HealthWidget(assets)
	private var scoreWidget = ScoreWidget(assets)
	private var ammoWidget = AmmoWidget(assets)
	private var messageWidget = MessageWidget(assets)
	private var crossHairWidget = CrosshairWidget()
	private var fpsLabel = Label("", assets.skin)
	private var levelLabel = Label("", assets.skin)
	var gameOverWidget = GameOverWidget(game, stage, assets)
		private set
	var gameWinWidget = GameWinWidget(game, stage, assets)
		private set

    private val pauseWidget = PauseWidget(game, stage)


	init {
		configureWidgets()
		pauseWidget.iniPauseControls(assets)
        Gdx.input.inputProcessor = stage
        pauseWidget.hidePauseControls()
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
		stage.addActor(fpsLabel)
		stage.addActor(levelLabel)
		if(CesDoom.isMobile)
			ControllerWidget().addToStage(stage)

        //stage.keyboardFocus = pauseWidget
        stage.addListener(object : InputListener() {
			override fun keyUp(event: InputEvent?, keycode: Int): Boolean {
				when(keycode) {
					Input.Keys.CENTER -> game.playerInput.center = false
					Input.Keys.LEFT -> game.playerInput.left = false
					Input.Keys.RIGHT -> game.playerInput.right = false
					Input.Keys.DOWN -> game.playerInput.down = false
					Input.Keys.UP -> game.playerInput.up = false
				}
				return false
			}
			override fun keyDown(event: InputEvent?, keycode: Int): Boolean {
				if(keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
                    pauseWidget.pauseOnOf()
					return true
				}
				when(keycode) {
					Input.Keys.CENTER -> game.playerInput.center = true
					Input.Keys.LEFT -> game.playerInput.left = true
					Input.Keys.RIGHT -> game.playerInput.right = true
					Input.Keys.DOWN -> game.playerInput.down = true
					Input.Keys.UP -> game.playerInput.up = true
				}
				return false
			}
		})
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
}
