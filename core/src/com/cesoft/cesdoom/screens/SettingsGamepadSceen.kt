package com.cesoft.cesdoom.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.controllers.Controller
import com.badlogic.gdx.controllers.ControllerListener
import com.badlogic.gdx.controllers.Controllers
import com.badlogic.gdx.controllers.PovDirection
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.cesoft.cesdoom.CesDoom
import com.cesoft.cesdoom.Settings
import com.cesoft.cesdoom.assets.Assets
import com.cesoft.cesdoom.input.Inputs
import com.cesoft.cesdoom.systems.PlayerSystem
import com.cesoft.cesdoom.util.Log

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class SettingsGamepadSceen(internal val game: CesDoom, private val assets: Assets)
    : Screen, InputProcessor, ControllerListener {

    companion object {
        private val tag: String = SettingsGamepadSceen::class.java.simpleName
    }

    private var stage = Stage(FitViewport(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT))
    private var backgroundImage = Image(Texture(Gdx.files.internal("data/background.png")))
    private var backButton = TextButton(assets.getString(Assets.ATRAS), assets.skin)
    //private var saveButton = TextButton(assets.getString(Assets.SAVE), assets.skin)

    private val win = Window("SettingsGamepadSceen", assets.skin, "special")
    private val titleLabel = Label("GAME PAD", assets.skin)
    private val lblSeparator = Label("", assets.skin)

    private val txt0 = arrayListOf<TextField>()
    private val txt1 = arrayListOf<TextField>()
    /*private val txtStart = TextField("", assets.skin)
    private val txtBack = TextField("", assets.skin)
    private val txtFire = TextField("", assets.skin)
    private val txtJump = TextField("", assets.skin)
    private val txtMoveLeft = TextField("", assets.skin)
    private val txtMoveRight = TextField("", assets.skin)
    private val txtMoveUp = TextField("", assets.skin)
    private val txtMoveDown = TextField("", assets.skin)
    private val txtLookLeft = TextField("", assets.skin)
    private val txtLookRight = TextField("", assets.skin)
    private val txtLookUp = TextField("", assets.skin)
    private val txtLookDown = TextField("", assets.skin)*/

    private var currentTxt: TextField? = null

    init {
        initFromSettings()
        configure()
        setListeners()
        Gdx.input.inputProcessor = this
        Controllers.addListener(this)
    }


    //______________________________________________________________________________________________
    private fun initFromSettings() {
        txt0.clear()
        for(i in 0 until Inputs.MAX) {
            txt0.add(i, TextField(Settings.inputMapping0[i].toString(), assets.skin))
            txt0[i].setOnscreenKeyboard {
                txt0[i].selectAll()
                currentTxt = txt0[i]
            }
        }
        txt1.clear()
        for(i in 0 until Inputs.MAX) {
            txt1.add(i, TextField(Settings.inputMapping1[i].toString(), assets.skin))
            txt1[i].setOnscreenKeyboard {
                txt1[i].selectAll()
                currentTxt = txt1[i]
            }
        }

        /*txtStart.text = Settings.inputMapping[0].toString()//TODO: Parametrizar todo
        txtBack.text = Settings.inputMapping[1].toString()
        txtFire.text = Settings.inputMapping[2].toString()
        txtJump.text = Settings.inputMapping[3].toString()
        txtMoveLeft.text = Settings.inputMapping[4].toString()
        txtMoveRight.text = Settings.inputMapping[4].toString()
        txtMoveUp.text = Settings.inputMapping[5].toString()
        txtMoveDown.text = Settings.inputMapping[5].toString()
        txtLookLeft.text = Settings.inputMapping[6].toString()
        txtLookRight.text = Settings.inputMapping[6].toString()
        txtLookUp.text = Settings.inputMapping[7].toString()
        txtLookDown.text = Settings.inputMapping[7].toString()*/
    }
    //______________________________________________________________________________________________
    private fun saveToSettings() {
        for(i in 0 until Inputs.MAX) {
            Settings.inputMapping0[i] = txt0[i].text.toInt()
            Settings.inputMapping1[i] = txt1[i].text.toInt()
        }
        Settings.savePrefs()
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

        val cx = 100f
        val cy = 50f
        val alignLabel = Align.left
        val alignField = Align.left
        val scrollTable = Table()
        scrollTable.width = win.width-10
        scrollTable.add(titleLabel).size(2*cx,2*cy).align(alignLabel)
        scrollTable.row()

        for(i in 0 until Inputs.MAX) {
            scrollTable.add(Label(Inputs.Names[i], assets.skin)).size(cx,cy).align(alignLabel)
            scrollTable.add(txt0[i]).size(cx,cy).align(alignField)
            scrollTable.add(txt1[i]).size(cx,cy).align(alignField)
            scrollTable.row()
        }
/*
        txtStart.setOnscreenKeyboard { currentTxt = txtStart; Log.e(tag, "txtStart: Keyboard -------------------------") }
        scrollTable.add(Label("Start:", assets.skin)).size(cx,cy).align(alignLabel)
        scrollTable.add(txtStart).size(cx,cy).align(alignField)
        scrollTable.row()
        //
        txtBack.setOnscreenKeyboard { currentTxt = txtBack; Log.e(tag, "txtBack: Keyboard -------------------------") }
        scrollTable.add(Label("Back:", assets.skin)).size(cx,cy).align(alignLabel)
        scrollTable.add(txtBack).size(cx,cy).align(alignField)
        scrollTable.row()
        //
        txtFire.setOnscreenKeyboard { currentTxt = txtFire; Log.e(tag, "txtFire: Keyboard -------------------------") }
        scrollTable.add(Label("Fire:", assets.skin)).size(cx,cy).align(alignLabel)
        scrollTable.add(txtFire).size(cx,cy).align(alignField)
        scrollTable.row()
        //
        txtJump.setOnscreenKeyboard { currentTxt = txtJump }
        scrollTable.add(Label("Jump:", assets.skin)).size(cx,cy).align(alignLabel)
        scrollTable.add(txtJump).size(cx,cy).align(Align.center)
        scrollTable.row()
        //
        txtMoveLeft.setOnscreenKeyboard { currentTxt = txtMoveLeft }
        scrollTable.add(Label("Move Left:", assets.skin)).size(cx,cy).align(alignLabel)
        scrollTable.add(txtMoveLeft).size(cx,cy).align(Align.center)
        scrollTable.row()
        //
        txtMoveRight.setOnscreenKeyboard { currentTxt = txtMoveRight }
        scrollTable.add(Label("Move Right:", assets.skin)).size(cx,cy).align(alignLabel)
        scrollTable.add(txtMoveRight).size(cx,cy).align(Align.center)
        scrollTable.row()
        //
        txtMoveUp.setOnscreenKeyboard { currentTxt = txtMoveUp }
        scrollTable.add(Label("Move Up:", assets.skin)).size(cx,cy).align(alignLabel)
        scrollTable.add(txtMoveUp).size(cx,cy).align(Align.center)
        scrollTable.row()
        //
        txtMoveDown.setOnscreenKeyboard { currentTxt = txtMoveDown }
        scrollTable.add(Label("Move Down:", assets.skin)).size(cx,cy).align(alignLabel)
        scrollTable.add(txtMoveDown).size(cx,cy).align(Align.center)
        scrollTable.row()
        //
        txtLookLeft.setOnscreenKeyboard { currentTxt = txtLookLeft }
        scrollTable.add(Label("Look Left:", assets.skin)).size(cx,cy).align(alignLabel)
        scrollTable.add(txtLookLeft).size(cx,cy).align(Align.center)
        scrollTable.row()
        //
        txtLookRight.setOnscreenKeyboard { currentTxt = txtLookRight }
        scrollTable.add(Label("Look Right:", assets.skin)).size(cx,cy).align(alignLabel)
        scrollTable.add(txtLookRight).size(cx,cy).align(Align.center)
        scrollTable.row()
        //
        txtLookUp.setOnscreenKeyboard { currentTxt = txtLookUp }
        scrollTable.add(Label("Look Up:", assets.skin)).size(cx,cy).align(alignLabel)
        scrollTable.add(txtLookUp).size(cx,cy).align(Align.center)
        scrollTable.row()
        //
        txtLookDown.setOnscreenKeyboard { currentTxt = txtLookDown }
        scrollTable.add(Label("Look Down:", assets.skin)).size(cx,cy).align(Align.left)
        scrollTable.add(txtLookDown).size(cx,cy).align(Align.center)
        scrollTable.row()*/
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
        saveToSettings()
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



    //______________________________________________________________________________________________
    /// Implements: ControllerListener
    private var controller: Controller? = null

    //----------------------------------------------------------------------------------------------
    override fun connected(controller: Controller?) {
        this.controller = controller
        Log.e(PlayerSystem.tag, "connected:----------*****************************--------------"+controller?.name)
    }
    override fun disconnected(controller: Controller?) {
        this.controller = null
        Log.e(PlayerSystem.tag, "disconnected:---------*************************---------------"+controller?.name)
    }
    ///
    override fun axisMoved(controller: Controller?, axisCode: Int, value: Float): Boolean {
        Log.e(tag, "axisMoved:------------"+controller?.name+" : $axisCode : $value")
        currentTxt?.let {
            it.text = axisCode.toString()
        }
        return false
    }
    ///
    override fun buttonUp(controller: Controller?, buttonCode: Int): Boolean {
        Log.e(PlayerSystem.tag, "buttonUp:----------------"+controller?.name+" : "+buttonCode)
        return false
    }
    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        Log.e(PlayerSystem.tag, "buttonDown:----------------"+controller?.name+" : "+buttonCode)
        currentTxt?.let {
            it.text = buttonCode.toString()
        }
        return false
    }
    ///
    override fun accelerometerMoved(controller: Controller?, accelerometerCode: Int, value: Vector3?): Boolean {
        Log.e(PlayerSystem.tag, "accelerometerMoved:------------"+controller?.name+" : "+accelerometerCode+" : "+value)
        return false
    }
    override fun ySliderMoved(controller: Controller?, sliderCode: Int, value: Boolean): Boolean {
        Log.e(PlayerSystem.tag, "ySliderMoved:------------"+controller?.name+" : "+sliderCode+" : "+value)
        return false
    }
    override fun xSliderMoved(controller: Controller?, sliderCode: Int, value: Boolean): Boolean {
        Log.e(PlayerSystem.tag, "xSliderMoved:------------"+controller?.name+" : "+sliderCode+" : "+value)
        return false
    }
    override fun povMoved(controller: Controller?, povCode: Int, value: PovDirection?): Boolean {
        Log.e(PlayerSystem.tag, "povMoved:------------"+controller?.name+" : "+povCode+" : "+value)
        return false
    }
}