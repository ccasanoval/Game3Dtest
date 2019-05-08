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
import com.cesoft.cesdoom.ui.Styles
import com.cesoft.cesdoom.util.PlayServices


////////////////////////////////////////////////////////////////////////////////////////////////////
//
class MainMenuScreen(private val game: CesDoom, private val assets: Assets) : Screen, PlayServices.Listener {
	companion object {
	    val tag: String = MainMenuScreen::class.java.simpleName
	}

	private val mapper = game.playerInput.mapper
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
		//
		configureWidgets()
		setListeners()
		Gdx.input.inputProcessor = stage
		Controllers.addListener(game.playerInput)
	}

	private fun goPlay() { game.reset() }
	private fun goQuit() { Gdx.app.exit() }
	private fun goSettings() { game.setScreen(SettingsScreen(game, assets)) }
	private fun goAbout() { game.setScreen(AboutScreen(game, assets)) }
	private fun goLeaderBoard() { game.playServices?.showLeaderBoard() }
	private fun goAchievements() { game.playServices?.showAchievements() }
	private fun goSignInGPGS() { game.playServices?.signIn() }

	//______________________________________________________________________________________________
	private fun configureWidgets() {
		var y = 110f
        val x = 220f
		var delay = 0.50f
		val fadeIn = 0.60f
		//
		backgroundImage.setSize(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT)
		backgroundImage.color = Styles.colorNormal0
		backgroundImage.addAction(Actions.fadeIn(fadeIn))
		//
		titleImage.color = Styles.colorNormal0
		titleImage.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		titleImage.setPosition(
				(CesDoom.VIRTUAL_WIDTH - titleImage.width) / 2,
				(CesDoom.VIRTUAL_HEIGHT - titleImage.height) / 2 +y)
		y -= 190f
		delay += 0.50f
		//------------
		playButton.setSize(280f, 90f)
		playButton.label.setFontScale(2f)
		playButton.setPosition((CesDoom.VIRTUAL_WIDTH-playButton.width)/2 -x, CesDoom.VIRTUAL_HEIGHT / 2 +y)
		playButton.color = Styles.colorNormal0
		playButton.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		//
		settingsButton.setSize(280f, 90f)
		settingsButton.label.setFontScale(1.5f)
		settingsButton.setPosition((CesDoom.VIRTUAL_WIDTH-settingsButton.width)/2 +x, CesDoom.VIRTUAL_HEIGHT / 2 +y)
		settingsButton.color = Styles.colorNormal0
		settingsButton.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		delay += 0.50f
		y -= 90f
		//------------
		quitButton.setSize(280f, 90f)
		quitButton.label.setFontScale(2f)
		quitButton.setPosition((CesDoom.VIRTUAL_WIDTH-quitButton.width)/2 -x, CesDoom.VIRTUAL_HEIGHT / 2 +y)
		quitButton.color = Styles.colorNormal0
		quitButton.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		//
		aboutButton.setSize(280f, 90f)
		aboutButton.label.setFontScale(1.5f)
		aboutButton.setPosition((CesDoom.VIRTUAL_WIDTH-aboutButton.width)/2 +x, CesDoom.VIRTUAL_HEIGHT / 2 +y)
		aboutButton.color = Styles.colorNormal0
		aboutButton.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		delay += 0.50f
		y -= 90f
		//------------
		leaderBoardButton.setSize(400f, 90f)
		leaderBoardButton.label.setFontScale(1.5f)
		leaderBoardButton.setPosition((CesDoom.VIRTUAL_WIDTH-leaderBoardButton.width)/2 -x, CesDoom.VIRTUAL_HEIGHT/2 +y)
		leaderBoardButton.color = Styles.colorGPGS0
		leaderBoardButton.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		//
		achievementsButton.setSize(400f, 90f)
		achievementsButton.label.setFontScale(1.5f)
		achievementsButton.setPosition((CesDoom.VIRTUAL_WIDTH-achievementsButton.width)/2 +x, CesDoom.VIRTUAL_HEIGHT/2 +y)
		achievementsButton.color = Styles.colorGPGS0
		achievementsButton.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		//
		gpgsSignInButton.setSize(450f, 90f)
		gpgsSignInButton.label.setFontScale(1.5f)
		gpgsSignInButton.setPosition((CesDoom.VIRTUAL_WIDTH-gpgsSignInButton.width) / 2, CesDoom.VIRTUAL_HEIGHT / 2 +y)
		gpgsSignInButton.color = Styles.colorGPGS0
		gpgsSignInButton.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		delay += 0.50f
		y -= 90f

		//------------
		stage.addActor(backgroundImage)
		stage.addActor(titleImage)
		stage.addActor(playButton)
		stage.addActor(settingsButton)
		stage.addActor(aboutButton)
		stage.addActor(quitButton)
		//
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
		else {
			gpgsSignInButton.isVisible = false
			leaderBoardButton.isVisible = false
			achievementsButton.isVisible = false
		}
	}

	//______________________________________________________________________________________________
	/// Implements : PlayServices.SignedInListener
	override fun onSignedIn() {
		//Log.e(tag, "onSignedIn------------------------------------------------------------")
		updateGPGSButtons()
	}
	override fun onSignedOut() {
		//Log.e(tag, "onSignedOut------------------------------------------------------------")
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
					//Log.e(tag, "GOD MODE ON !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
					PlayerComponent.isGodModeOn = true
				}
			}
			return@addListener true
		}
		playButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				goPlay()
			}
		})
		quitButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				goQuit()
			}
		})
        settingsButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                goSettings()
            }
        })
		aboutButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				goAbout()
			}
		})
		leaderBoardButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				goLeaderBoard()
			}
		})
		achievementsButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				goAchievements()
			}
		})
		gpgsSignInButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				goSignInGPGS()
			}
		})
	}

	//______________________________________________________________________________________________
	private var inputDelay = 0f
	override fun render(delta: Float) {
		inputDelay+=delta
		if(inputDelay > .250f) {
			inputDelay = 0f
			processInput()
		}
		stage.act(delta)
		stage.draw()
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


    /// PROCESS INPUT ------------------------------------------------------------------------------
    private var currentFocus = ButtonFocus.NONE
    private enum class ButtonFocus {
        NONE,
        PLAY,					SETTINGS,
        QUIT,					ABOUT,
        LEADERBOARD,			ACHIEVEMENTS,
        SIGNIN
    }
    private fun processInput() {
        if(mapper.isButtonPressed(Inputs.Action.START)) {
            currentFocus = ButtonFocus.PLAY
            goPlay()
        }
        else if(mapper.isButtonPressed(Inputs.Action.EXIT)) {
            currentFocus = ButtonFocus.QUIT
            goQuit()
        }
        updateFocusSelection()
        updateFocusColor()
        if(mapper.isButtonPressed(Inputs.Action.FIRE)) {
            processSelectedButton()
        }
    }
    private fun updateFocusSelection() {
        val down = mapper.isGoingDown()
        val up = mapper.isGoingUp()
        val backward = mapper.isGoingBackwards()
        val forward = mapper.isGoingForward()
        if(forward) {
            when(currentFocus) {
                ButtonFocus.NONE -> currentFocus = ButtonFocus.PLAY
                ButtonFocus.PLAY -> currentFocus = ButtonFocus.SETTINGS
                ButtonFocus.QUIT -> currentFocus = ButtonFocus.ABOUT
                ButtonFocus.LEADERBOARD -> currentFocus = ButtonFocus.ACHIEVEMENTS
                else -> Unit
            }
        }
        else if(backward) {
            when(currentFocus) {
                ButtonFocus.NONE -> currentFocus = ButtonFocus.PLAY
                ButtonFocus.SETTINGS -> currentFocus = ButtonFocus.PLAY
                ButtonFocus.ABOUT -> currentFocus = ButtonFocus.QUIT
                ButtonFocus.ACHIEVEMENTS -> currentFocus = ButtonFocus.LEADERBOARD
                else -> Unit
            }
        }
        if(up) {
            when(currentFocus) {
                ButtonFocus.NONE -> currentFocus = ButtonFocus.PLAY
                ButtonFocus.QUIT -> currentFocus = ButtonFocus.PLAY
                ButtonFocus.ABOUT -> currentFocus = ButtonFocus.SETTINGS
                ButtonFocus.ACHIEVEMENTS -> currentFocus = ButtonFocus.ABOUT
                ButtonFocus.LEADERBOARD -> currentFocus = ButtonFocus.QUIT
                ButtonFocus.SIGNIN -> currentFocus = ButtonFocus.ABOUT
                else -> Unit
            }
        }
        else if(down) {
            when(currentFocus) {
                ButtonFocus.NONE -> currentFocus = ButtonFocus.PLAY
                ButtonFocus.PLAY -> currentFocus = ButtonFocus.QUIT
                ButtonFocus.SETTINGS -> currentFocus = ButtonFocus.ABOUT
                ButtonFocus.QUIT ->
                    currentFocus = if(gpgsSignInButton.isVisible) ButtonFocus.SIGNIN
                    else ButtonFocus.LEADERBOARD
                ButtonFocus.ABOUT ->
                    currentFocus = if(gpgsSignInButton.isVisible) ButtonFocus.SIGNIN
                    else ButtonFocus.ACHIEVEMENTS
                else -> Unit
            }
        }
    }
    private fun updateFocusColor() {
        //Log.e("updateFocus", "-----------------------------------$currentFocus")
        if(aboutButton.color.a != 0f) {
            playButton.color = Styles.colorNormal1
            settingsButton.color = Styles.colorNormal1
            quitButton.color = Styles.colorNormal1
            aboutButton.color = Styles.colorNormal1
        }
        if(gpgsSignInButton.color.a != 0f)
            gpgsSignInButton.color = Styles.colorGPGS1
        if(leaderBoardButton.color.a != 0f) {
            leaderBoardButton.color = Styles.colorGPGS1
            achievementsButton.color = Styles.colorGPGS1
        }
        when(currentFocus) {
            ButtonFocus.NONE -> Unit
            ButtonFocus.PLAY -> playButton.color = Styles.colorSelected1
            ButtonFocus.QUIT -> quitButton.color = Styles.colorSelected1
            ButtonFocus.SETTINGS -> settingsButton.color = Styles.colorSelected1
            ButtonFocus.ABOUT -> aboutButton.color = Styles.colorSelected1
            ButtonFocus.SIGNIN -> gpgsSignInButton.color = Styles.colorSelected1
            ButtonFocus.LEADERBOARD -> leaderBoardButton.color = Styles.colorSelected1
            ButtonFocus.ACHIEVEMENTS -> achievementsButton.color = Styles.colorSelected1
        }
    }
    private fun processSelectedButton() {
        when(currentFocus) {
            ButtonFocus.NONE -> Unit
            ButtonFocus.PLAY -> goPlay()
            ButtonFocus.ABOUT -> goAbout()
            ButtonFocus.QUIT -> goQuit()
            ButtonFocus.SETTINGS -> goSettings()
            ButtonFocus.SIGNIN -> goSignInGPGS()
            ButtonFocus.LEADERBOARD -> goLeaderBoard()
            ButtonFocus.ACHIEVEMENTS -> goAchievements()
        }
    }

}