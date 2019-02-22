package com.cesoft.cesdoom.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.Settings
import com.cesoft.cesdoom.assets.Assets

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class SettingsGamepadSceen(internal val game: CesDoom, private val assets: Assets) : Screen, InputProcessor {

    private var stage = Stage(FitViewport(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT))
    private var backgroundImage = Image(Texture(Gdx.files.internal("data/background.png")))
    private var backButton = TextButton(assets.getString(Assets.ATRAS), assets.skin)

    private val win = Window("SettingsGamepadSceen", assets.skin, "special")
    private val titleLabel = Label("GAME PAD", assets.skin)
    private val lblSeparator = Label("", assets.skin)

    private val txtStart = TextField("START", assets.skin)
    private val txtBack = TextField("BACK", assets.skin)
    private val txtFire = TextField("FIRE", assets.skin)
    private val txtJump = TextField("JUMP", assets.skin)//TODO:I18N
    private val txtMoveLeft = TextField("MOVE LEFT", assets.skin)
    private val txtMoveRight = TextField("MOVE RIGHT", assets.skin)
    private val txtMoveUp = TextField("MOVE UP", assets.skin)
    private val txtMoveDown = TextField("MOVE DOWN", assets.skin)
    private val txtLookLeft = TextField("LOOK LEFT", assets.skin)

    init {
        configure()
        setListeners()
        Gdx.input.inputProcessor = this
    }

    //______________________________________________________________________________________________
    private fun configure() {

        backgroundImage.setSize(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT)

        backButton.setSize(175f, 85f)
        backButton.setPosition(CesDoom.VIRTUAL_WIDTH - backButton.width - 5, 5f)

        /// Inside Window
        val xWin = 50f
        val yWin = 100f
        win.setSize(CesDoom.VIRTUAL_WIDTH-100, CesDoom.VIRTUAL_HEIGHT-100)
        win.setPosition(xWin, yWin)

        /// Title
        titleLabel.setColor(.9f, .9f, .9f, 1f)
        titleLabel.setFontScale(2f)
        titleLabel.setSize(500f, 70f)

        //TODO

        val scrollTable = Table()
        scrollTable.width = win.width-5
        scrollTable.add(titleLabel).width(500f).height(90f).align(Align.left)
        scrollTable.row()
        //
        scrollTable.add(Label("Start:", assets.skin)).width(200f).height(90f).align(Align.left)
        scrollTable.add(txtStart).width(200f).height(90f).align(Align.center)
        scrollTable.row()
        //
        scrollTable.add(Label("Back:", assets.skin)).width(200f).height(90f).align(Align.left)
        scrollTable.add(txtBack).width(200f).height(90f).align(Align.center)
        scrollTable.row()
        //
        scrollTable.add(Label("Fire:", assets.skin)).width(200f).height(90f).align(Align.left)
        scrollTable.add(txtFire).width(200f).height(90f).align(Align.center)
        scrollTable.row()
        //
        scrollTable.add(lblSeparator).size(50f, 50f).row()

        val scroller = ScrollPane(scrollTable)
        val table = Table()
        table.setFillParent(true)
        table.add(scroller).fill().expand()
        win.addActor(table)

        stage.addActor(backgroundImage)
        stage.addActor(backButton)
        stage.addActor(win)
    }


    //______________________________________________________________________________________________
    private fun goBack() {
        Settings.savePrefs()
        //game.setScreen(MainMenuScreen(game, assets))
        game.setScreen(SettingsScreen(game, assets))
    }
    //______________________________________________________________________________________________
    private fun setListeners() {

        backButton.addListener {
            goBack()
            return@addListener false
        }
    }


    //______________________________________________________________________________________________
    /// Implements: Screen
    override fun render(delta: Float) {
        stage.act(delta)
        stage.draw()
    }
    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }
    override fun dispose() {
        stage.dispose()
    }
    override fun show() = Unit
    override fun pause() = Unit
    override fun resume() = Unit
    override fun hide() = Unit

    //______________________________________________________________________________________________
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