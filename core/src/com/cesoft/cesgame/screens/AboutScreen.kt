package com.cesoft.cesgame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.FitViewport
import com.cesoft.cesgame.Assets
import com.cesoft.cesgame.CesGame

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class AboutScreen(internal var game: CesGame, private val assets: Assets) : Screen {
	private var stage: Stage = Stage(FitViewport(CesGame.VIRTUAL_WIDTH, CesGame.VIRTUAL_HEIGHT))
	private var backgroundImage: Image = Image(Texture(Gdx.files.internal("data/background.png")))
	private var backButton: TextButton = TextButton(assets.getString(Assets.ATRAS), assets.skin)

	private val texto: Label = Label(assets.getString(Assets.CREDITOS_TXT), assets.skin)
	private val scrollPane = ScrollPane(texto, assets.skin)
	private val win = Window("test", assets.skin, "special")

	init {
		configureWidgers()
		setListeners()
		Gdx.input.inputProcessor = stage
	}

	//______________________________________________________________________________________________
	private fun configureWidgers() {
		backgroundImage.setSize(CesGame.VIRTUAL_WIDTH, CesGame.VIRTUAL_HEIGHT)
		backButton.setSize(175f, 75f)
		backButton.setPosition(CesGame.VIRTUAL_WIDTH - backButton.width - 5, 5f)

		texto.setWrap(true)
		texto.setColor(.9f, .9f, .9f, 1f)

		scrollPane.setScrollingDisabled(true, false)
		scrollPane.setSize(CesGame.VIRTUAL_WIDTH-250, CesGame.VIRTUAL_HEIGHT-250)
		scrollPane.setPosition(150f, 150f)
		scrollPane.zIndex = 2

		win.setSize(CesGame.VIRTUAL_WIDTH-100, CesGame.VIRTUAL_HEIGHT-120)
		win.setPosition(50f,100f)
		win.zIndex = 10
		win.touchable = Touchable.disabled
		//win.add(scrollPane).left().top()
		//win.setFillParent(true)

		stage.addActor(backgroundImage)
		stage.addActor(backButton)
		stage.addActor(win)
		stage.addActor(scrollPane)

	}

	//______________________________________________________________________________________________
	private fun setListeners() {
		backButton.addListener(object : ClickListener() {
			override fun clicked(event: InputEvent?, x: Float, y: Float) {
				game.setScreen(MainMenuScreen(game, assets))
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
}

/*
* table = new Table(menuSkin);
stage = new Stage();

table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

table.add(headerLabel).spaceBottom(55);
table.row();

table.add(scrollPane);
table.row();

table.add(backButton);

stage.addActor(table);

table.debug();

table.setFillParent(true);

Gdx.input.setInputProcessor(stage);*/