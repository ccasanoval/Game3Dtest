package com.cesoft.cesdoom.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.FitViewport
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.Settings
import com.cesoft.cesdoom.assets.Sounds
import com.cesoft.cesdoom.components.PlayerComponent
import com.cesoft.cesdoom.input.Inputs
import com.cesoft.cesdoom.util.Log
import com.cesoft.cesdoom.util.PlayServices


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class MainMenuScreen(private val game: CesDoom, private val assets: Assets) : Screen, PlayServices.Listener {
	companion object {
	    val tag: String = MainMenuScreen::class.java.simpleName
	}

	private val input = game.playerInput
	private var stage: Stage = Stage(FitViewport(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT))
	private var backgroundImage: Image
	private var titleImage: Image
	private var playButton: TextButton = TextButton(assets.getString(Assets.JUGAR), assets.skin)
	private var quitButton: TextButton = TextButton(assets.getString(Assets.SALIR), assets.skin)
	private var settingsButton: TextButton = TextButton(assets.getString(Assets.CONFIG), assets.skin)
	private var aboutButton: TextButton = TextButton(assets.getString(Assets.SOBRE), assets.skin)
	private var leaderBoardButton: TextButton = TextButton(assets.getString(Assets.PUNTUACIONES), assets.skin)
	private var achievementsButton: TextButton = TextButton(assets.getString(Assets.LOGROS), assets.skin)
	private var gpgsSignInButton: TextButton = TextButton(assets.getString(Assets.GPGS_SIGN_IN), assets.skin)

	init {
		PlayerComponent.isGodModeOn = false
		assets.iniMainMenuBg()
		assets.iniMainMenuTitle()
		backgroundImage = assets.getMainMenuBg()
		titleImage = assets.getMainMenuTitle()

		configureWidgets()
		setListeners()
		Gdx.input.inputProcessor = stage
		Controllers.addListener(input)
	}

	//______________________________________________________________________________________________
	private fun configureWidgets() {
		var y = 110f
        val x = 220f
		var delay = 0.50f
		val fadeIn = 0.60f
		//
		backgroundImage.setSize(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT)
		backgroundImage.setColor(1f, 1f, 1f, 0f)
		backgroundImage.addAction(Actions.fadeIn(0.65f))
		//
		titleImage.setColor(1f, 1f, 1f, 0f)
		titleImage.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		titleImage.setPosition(
				(CesDoom.VIRTUAL_WIDTH - titleImage.width) / 2,
				(CesDoom.VIRTUAL_HEIGHT - titleImage.height) / 2 +y)
		y -= 190f
		delay += 0.55f
		//
		playButton.setSize(280f, 90f)
		playButton.label.setFontScale(2f)
		playButton.setPosition((CesDoom.VIRTUAL_WIDTH-playButton.width)/2 -x, CesDoom.VIRTUAL_HEIGHT / 2 +y)
		playButton.setColor(1f, 1f, 1f, 0f)
		playButton.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		//
		settingsButton.setSize(280f, 90f)
		settingsButton.label.setFontScale(1.5f)
		settingsButton.setPosition((CesDoom.VIRTUAL_WIDTH-settingsButton.width)/2 +x, CesDoom.VIRTUAL_HEIGHT / 2 +y)
		settingsButton.setColor(1f, 1f, 1f, 0f)
		settingsButton.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		delay += 0.30f
		y -= 90f
		//
		quitButton.setSize(280f, 90f)
		quitButton.label.setFontScale(2f)
		quitButton.setPosition((CesDoom.VIRTUAL_WIDTH-quitButton.width)/2 -x, CesDoom.VIRTUAL_HEIGHT / 2 +y)
		quitButton.setColor(1f, 1f, 1f, 0f)
		quitButton.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		//
		aboutButton.setSize(280f, 90f)
		aboutButton.label.setFontScale(1.5f)
		aboutButton.setPosition((CesDoom.VIRTUAL_WIDTH-aboutButton.width)/2 +x, CesDoom.VIRTUAL_HEIGHT / 2 +y)
		aboutButton.setColor(1f, 1f, 1f, 0f)
		aboutButton.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		delay += 0.30f
		y -= 90f
		//
		leaderBoardButton.setSize(400f, 90f)
		leaderBoardButton.label.setFontScale(1.5f)
		leaderBoardButton.setPosition((CesDoom.VIRTUAL_WIDTH-leaderBoardButton.width)/2 -x, CesDoom.VIRTUAL_HEIGHT/2 +y)
		leaderBoardButton.setColor(.7f, 1f, .7f, 0f)
		leaderBoardButton.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		//
		achievementsButton.setSize(400f, 90f)
		achievementsButton.label.setFontScale(1.5f)
		achievementsButton.setPosition((CesDoom.VIRTUAL_WIDTH-achievementsButton.width)/2 +x, CesDoom.VIRTUAL_HEIGHT/2 +y)
		achievementsButton.setColor(.7f, 1f, .7f, 0f)
		achievementsButton.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		//
		gpgsSignInButton.setSize(450f, 90f)
		gpgsSignInButton.label.setFontScale(1.5f)
		gpgsSignInButton.setPosition((CesDoom.VIRTUAL_WIDTH-gpgsSignInButton.width) / 2, CesDoom.VIRTUAL_HEIGHT / 2 +y)
		gpgsSignInButton.setColor(.7f, 1f, .7f, 0f)
		gpgsSignInButton.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		delay += 0.30f
		y -= 90f
		//
		stage.addActor(backgroundImage)
		stage.addActor(titleImage)
		stage.addActor(playButton)
		stage.addActor(settingsButton)
		stage.addActor(aboutButton)
		stage.addActor(quitButton)

		stage.addActor(gpgsSignInButton)
		stage.addActor(leaderBoardButton)
		stage.addActor(achievementsButton)

		updateGPGSButtons()
	}

	private fun updateGPGSButtons() {
		if(Settings.isGPGSEnabled) {
			game.playServices?.let {
				game.playServices.addOnSignedIn(this)
				if(it.isSignedIn()) {
					gpgsSignInButton.isVisible = false
					leaderBoardButton.isVisible = true
					achievementsButton.isVisible = true
				}
				else {
					gpgsSignInButton.isVisible = true
					leaderBoardButton.isVisible = false
					achievementsButton.isVisible = false
				}
			}
		}
	}

	//______________________________________________________________________________________________
	/// Implements : PlayServices.SignedInListener
	override fun onSignedIn() {
		Log.e(tag, "onSignedIn------------------------------------------------------------")
		updateGPGSButtons()
	}
	override fun onSignedOut() {
		Log.e(tag, "onSignedOut------------------------------------------------------------")
		updateGPGSButtons()
	}

	//______________________________________________________________________________________________
	private fun setListeners() {
		var counter = 0L
		var lastClick = 0L
		titleImage.addListener {
			val now = System.currentTimeMillis()
			if(now > lastClick + 250) {
				lastClick = now
				if (!PlayerComponent.isGodModeOn && ++counter > 9) {
					Log.e(tag, "GOD MODE ON !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
					PlayerComponent.isGodModeOn = true
				}
			}
			return@addListener true
		}

		playButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				game.reset()
			}
		})
		quitButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				Gdx.app.exit()
			}
		})
        settingsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                game.setScreen(SettingsScreen(game, assets))
            }
        })
		aboutButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				game.setScreen(AboutScreen(game, assets))
			}
		})
		leaderBoardButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				game.playServices?.showLeaderBoard()
			}
		})
		achievementsButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				game.playServices?.showAchievements()
			}
		})
		gpgsSignInButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				game.playServices?.signIn()
			}
		})
	}

	//______________________________________________________________________________________________
	override fun render(delta: Float) {
		processInput()
		stage.act(delta)
		stage.draw()
	}
	private var currentFocus = 0
	private fun processInput() {
		val forward = input.mapper.isAxisValuePositive(Inputs.Action.MOVE_X)
				|| input.mapper.isAxisValuePositive(Inputs.Action.MOVE_Y)
				|| input.mapper.isAxisValuePositive(Inputs.Action.LOOK_X)
				|| input.mapper.isAxisValuePositive(Inputs.Action.LOOK_Y)
		val backward = input.mapper.isAxisValueNegative(Inputs.Action.MOVE_X)
				|| input.mapper.isAxisValueNegative(Inputs.Action.MOVE_Y)
				|| input.mapper.isAxisValueNegative(Inputs.Action.LOOK_X)
				|| input.mapper.isAxisValueNegative(Inputs.Action.LOOK_Y)
		if(game.playerInput.mapper.isButtonPressed(Inputs.Action.START)) {
			currentFocus = 0
			game.reset()
		}
		else if(input.mapper.isButtonPressed(Inputs.Action.EXIT)) {
			currentFocus = 1
			Gdx.app.exit()
		}
		else if(forward) {
			currentFocus++
			if(currentFocus > 3)currentFocus=3
		}
		else if(backward) {
			currentFocus--
			if(currentFocus < 0)currentFocus=0
		}
		updateFocus()
	}
	private fun updateFocus() {
//		playButton.background = playButton.style.up
//		quitButton.background = playButton.style.up
//		settingsButton.background = playButton.style.up
//		aboutButton.background = playButton.style.up
		//playButton.setColor(1f,1f,1f,1f)
		when(currentFocus) {
			0 -> {
				playButton.background = playButton.style.over
				//playButton.setColor(1f,0f,0f,1f)
			}
			1 -> quitButton.background = playButton.style.over
			2 -> settingsButton.background = playButton.style.over
			3 -> aboutButton.background = playButton.style.over
		}
		/*when(currentFocus) {
			0 -> playButton.background = playButton.style.focused
			1 -> quitButton.background = playButton.style.focused
			2 -> settingsButton.background = playButton.style.focused
			3 -> aboutButton.background = playButton.style.focused
		}*/
	}

	//______________________________________________________________________________________________
	override fun resize(width: Int, height: Int) {
		stage.viewport.update(width, height)
	}
	//______________________________________________________________________________________________
	override fun dispose() {
		stage.dispose()
	}
	//______________________________________________________________________________________________
	override fun show() = Unit
	override fun hide() = Unit
	override fun pause() = Unit
	override fun resume() {
		Sounds.stopMusic()
	}
}