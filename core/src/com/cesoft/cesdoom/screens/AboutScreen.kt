package com.cesoft.cesdoom.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.FitViewport
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.util.Log

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class AboutScreen(internal val game: CesDoom) : Screen, InputProcessor {

	private var stage: Stage = Stage(FitViewport(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT))
	private var backgroundImage: Image = Image(Texture(Gdx.files.internal("data/background.png")))
	private var backButton: TextButton = TextButton(game.assets.getString(Assets.ATRAS), game.assets.skin)

	private val texto: Label = Label(game.assets.getString(Assets.SOBRE_TXT), game.assets.skin)
	private val scrollPane = ScrollPane(texto, game.assets.skin)
	private val win = Window("test", game.assets.skin, "special")

	init {
		configureWidgers()
		setListeners()
		Gdx.input.inputProcessor = this
        stage.keyboardFocus
	}

	//______________________________________________________________________________________________
	private fun configureWidgers() {
		backgroundImage.setSize(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT)
		backButton.setSize(175f, 85f)
		backButton.setPosition(CesDoom.VIRTUAL_WIDTH - backButton.width - 5, 5f)

		texto.setWrap(true)
		texto.setFontScale(1.5f)
		texto.setColor(.9f, .9f, .9f, 1f)

		scrollPane.setScrollingDisabled(true, false)
		scrollPane.setSize(CesDoom.VIRTUAL_WIDTH-250, CesDoom.VIRTUAL_HEIGHT-250)
		scrollPane.setPosition(150f, 150f)
		scrollPane.zIndex = 2

		win.setSize(CesDoom.VIRTUAL_WIDTH-100, CesDoom.VIRTUAL_HEIGHT-100)
		win.setPosition(75f,100f)
		win.zIndex = 10
		win.touchable = Touchable.disabled

		stage.addActor(backgroundImage)
		stage.addActor(backButton)
		stage.addActor(win)
		stage.addActor(scrollPane)
	}

	//______________________________________________________________________________________________
	private fun goBack() { game.setScreen(MainMenuScreen(game)) }
	private fun setListeners() {
		backButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				Log.e("AboutScreen", "backButton:clicked------------------------------------------")
				goBack()
			}
		})
	}

	//______________________________________________________________________________________________
	override fun render(delta: Float) {
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

	override fun show() {}
	override fun pause() {}
	override fun resume() {}
	override fun hide() {}


	/// Implements: InputProcessor
	override fun keyDown(keycode: Int): Boolean {
		if (keycode == Input.Keys.BACK) goBack()
		return false
	}
	override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = stage.touchUp(screenX, screenY, pointer, button)
	override fun mouseMoved(screenX: Int, screenY: Int): Boolean = stage.mouseMoved(screenX, screenY)
	override fun keyTyped(character: Char): Boolean = stage.keyTyped(character)
	override fun scrolled(amount: Int): Boolean = stage.scrolled(amount)
	override fun keyUp(keycode: Int): Boolean = stage.keyUp(keycode)
	override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = stage.touchDragged(screenX, screenY, pointer)
	override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = stage.touchDown(screenX, screenY, pointer, button)
}
