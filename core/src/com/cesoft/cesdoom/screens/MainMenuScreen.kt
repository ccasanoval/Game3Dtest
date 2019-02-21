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
import com.cesoft.cesdoom.managers.PlayerInput
import com.cesoft.cesdoom.util.Log

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class MainMenuScreen(private val game: CesDoom, private val assets: Assets) : Screen {
	private var stage: Stage = Stage(FitViewport(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT))
	private var backgroundImage: Image// = game.assets.getMainMenuBg()//Image(Texture(Gdx.files.internal("data/background.png")))
	private var titleImage: Image// = game.assets.getMainMenuTitle()//Image(Texture(Gdx.files.internal("data/title.png")))
	private var playButton: TextButton = TextButton(assets.getString(Assets.JUGAR), assets.skin)
	private var quitButton: TextButton = TextButton(assets.getString(Assets.SALIR), assets.skin)
	private var settingsButton: TextButton = TextButton(assets.getString(Assets.CONFIG), assets.skin)
	private var aboutButton: TextButton = TextButton(assets.getString(Assets.SOBRE), assets.skin)
	private var leaderBoardButton: TextButton = TextButton(assets.getString(Assets.PUNTUACIONES), assets.skin)
	private var gpgsSignInButton: TextButton = TextButton(assets.getString(Assets.GPGS_SIGN_IN), assets.skin)

	private val input = PlayerInput()

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
		playButton.setPosition(CesDoom.VIRTUAL_WIDTH / 2 - playButton.width / 2 -x, CesDoom.VIRTUAL_HEIGHT / 2 +y)
		playButton.setColor(1f, 1f, 1f, 0f)
		playButton.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		//
		settingsButton.setSize(280f, 90f)
		settingsButton.label.setFontScale(1.5f)
		settingsButton.setPosition(CesDoom.VIRTUAL_WIDTH / 2 - playButton.width / 2 +x, CesDoom.VIRTUAL_HEIGHT / 2 +y)
		settingsButton.setColor(1f, 1f, 1f, 0f)
		settingsButton.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		delay += 0.30f
		y -= 90f
		//
		quitButton.setSize(280f, 90f)
		quitButton.label.setFontScale(2f)
		quitButton.setPosition(CesDoom.VIRTUAL_WIDTH / 2 - playButton.width / 2 -x, CesDoom.VIRTUAL_HEIGHT / 2 +y)
		quitButton.setColor(1f, 1f, 1f, 0f)
		quitButton.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		//
		aboutButton.setSize(280f, 90f)
		aboutButton.label.setFontScale(1.5f)
		aboutButton.setPosition(CesDoom.VIRTUAL_WIDTH / 2 - playButton.width / 2 +x, CesDoom.VIRTUAL_HEIGHT / 2 +y)
		aboutButton.setColor(1f, 1f, 1f, 0f)
		aboutButton.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		delay += 0.30f
		y -= 90f
		//
		leaderBoardButton.setSize(400f, 90f)
		leaderBoardButton.label.setFontScale(1.5f)
		leaderBoardButton.setPosition((CesDoom.VIRTUAL_WIDTH-leaderBoardButton.width) / 2, CesDoom.VIRTUAL_HEIGHT / 2 +y)
		leaderBoardButton.setColor(1f, 1f, 1f, 0f)
		leaderBoardButton.addAction(SequenceAction(Actions.delay(delay), Actions.fadeIn(fadeIn)))
		gpgsSignInButton.setSize(450f, 90f)
		gpgsSignInButton.label.setFontScale(1.5f)
		gpgsSignInButton.setPosition((CesDoom.VIRTUAL_WIDTH-leaderBoardButton.width) / 2, CesDoom.VIRTUAL_HEIGHT / 2 +y)
		gpgsSignInButton.setColor(1f, 1f, 1f, 0f)
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
		if(Settings.isGPGSEnabled) {
			game.playServices?.let {
				if (it.isSignedIn())
					stage.addActor(leaderBoardButton)
				else
					stage.addActor(gpgsSignInButton)
			}
		}
	}

	private fun setListeners() {

		var counter = 0L
		var lastClick = 0L
		titleImage.addListener {
			val now = System.currentTimeMillis()
			if(now > lastClick + 500) {
				lastClick = now
				if (!PlayerComponent.isGodModeOn && ++counter > 9) {
					Log.e("MainMenuScreen", "GOD MODE ON !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
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
		gpgsSignInButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				game.playServices?.signIn()
			}
		})
	}

	override fun render(delta: Float) {
		procInput()
		stage.act(delta)
		stage.draw()
	}
	private enum class Button { PLAY, SETTINGS, QUIT, ABOUT }
	private var currentButton = Button.PLAY
	private fun procInput() {
		if(input.btnStart) {
			game.reset()
		}

		if(input.btnLeft) {
			if(currentButton == Button.SETTINGS)
				currentButton = Button.PLAY
			else if(currentButton == Button.ABOUT)
				currentButton = Button.QUIT
		}
		else if(input.btnRight) {
			if(currentButton == Button.PLAY)
				currentButton = Button.SETTINGS
			else if(currentButton == Button.QUIT)
				currentButton = Button.ABOUT
		}
		if(input.btnUp) {
			if(currentButton == Button.QUIT)
				currentButton = Button.PLAY
			else if(currentButton == Button.ABOUT)
				currentButton = Button.SETTINGS
		}
		else if(input.btnDown) {
			if(currentButton == Button.PLAY)
				currentButton = Button.QUIT
			else if(currentButton == Button.SETTINGS)
				currentButton = Button.ABOUT
		}

		playButton.isChecked = false
		settingsButton.isChecked = false
		aboutButton.isChecked = false
		quitButton.isChecked = false
		//Log.e("MAinMenu", "-----------------------------------------currentButton=$currentButton")
		when(currentButton) {
			Button.PLAY -> playButton.isChecked = true
			Button.SETTINGS -> settingsButton.isChecked = true
			Button.ABOUT -> aboutButton.isChecked = true
			Button.QUIT -> quitButton.isChecked = true
		}
	}

	override fun resize(width: Int, height: Int) {
		stage.viewport.update(width, height)
	}

	override fun dispose() {
		stage.dispose()
	}

	override fun show() = Unit
	override fun hide() = Unit
	override fun pause() = Unit
	override fun resume() {
		Sounds.stopMusic()
	}
}