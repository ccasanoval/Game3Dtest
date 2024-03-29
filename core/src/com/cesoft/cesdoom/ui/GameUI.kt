package com.cesoft.cesdoom.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.viewport.FitViewport
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.Settings
import com.cesoft.cesdoom.Status
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.components.PlayerComponent
import com.cesoft.cesdoom.input.Inputs
import com.cesoft.cesdoom.util.Log
import de.golfgl.gdx.controllers.ControllerMenuStage


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class GameUI(val game: CesDoom, val assets: Assets) {
	companion object {
		private val tag: String = GameUI::class.simpleName!!
	}

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
    val pauseWidget = PauseWidget(game, stage, assets)

	init {
		configureWidgets()
        Gdx.input.inputProcessor = stage
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
		//TODO: Why its not called with GamePad SELECT button ????
        stage.addListener(object : InputListener() {
			//--------------------------------------------------------------------------------------
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
			//--------------------------------------------------------------------------------------
			override fun keyDown(event: InputEvent?, keycode: Int): Boolean {
				Log.e(tag, "keyDown----------------------------------------$event-----$keycode------")
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
			val level = assets.formatString(Assets.LEVEL, PlayerComponent.currentLevel)
			levelLabel.setText(level)
		}

		stage.act(delta)

		processInput(delta)
		pauseWidget.processInput(delta)
        gameWinWidget.processInput(delta)
        gameOverWidget.processInput(delta)
	}
	private var inputDelay = 0f
	private fun processInput(delta: Float) {
		inputDelay+=delta
		if(inputDelay > Settings.GAMEPAD_INPUT_DELAY) {
			inputDelay = 0f
			if(game.playerInput.mapper.isButtonPressed(Inputs.Action.Exit)
					|| game.playerInput.mapper.isButtonPressed(Inputs.Action.Back)) {
				pauseWidget.pauseOnOf()
			}
		}
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
