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
import com.cesoft.cesdoom.ui.Styles
import com.cesoft.cesdoom.util.Log
import de.golfgl.gdx.controllers.ControllerMenuStage
import kotlin.math.absoluteValue

////////////////////////////////////////////////////////////////////////////////////////////////////
//
class SettingsGamePadScreen(internal val game: CesDoom, private val assets: Assets)
    : Screen, InputProcessor, ControllerListener {

    companion object {
        private val tag: String = SettingsGamePadScreen::class.java.simpleName
    }

    private var stage = ControllerMenuStage(FitViewport(CesDoom.VIRTUAL_WIDTH, CesDoom.VIRTUAL_HEIGHT))
    private var backgroundImage = Image(Texture(Gdx.files.internal("data/background.png")))
    private var backButton = TextButton(assets.getString(Assets.ATRAS), assets.skin)

    private val win = Window(tag, assets.skin, Styles.windowStyle)
    private val titleLabel = Label("GAME PAD", assets.skin)
    private val lblSeparator = Label("", assets.skin)

    private val txt0 = arrayListOf<TextField>()
    private val txt1 = arrayListOf<TextField>()

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
		txt1.clear()
        for(i in Inputs.Action.values()) {
            txt0.add(i.value, TextField(Settings.inputMapping0[i.value].toString(), assets.skin))
            txt0[i.value].setOnscreenKeyboard {
                txt0[i.value].selectAll()
                currentTxt = txt0[i.value]
            }
			txt1.add(i.value, TextField(Settings.inputMapping1[i.value].toString(), assets.skin))
            txt1[i.value].setOnscreenKeyboard {
                txt1[i.value].selectAll()
                currentTxt = txt1[i.value]
            }
        }
    }
    //______________________________________________________________________________________________
    private fun saveToSettings() {
        for(i in Inputs.Action.values()) {
            Settings.inputMapping0[i.value] = txt0[i.value].text.toInt()
            Settings.inputMapping1[i.value] = txt1[i.value].text.toInt()
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

        for(i in Inputs.Action.values()) {
            scrollTable.add(Label(Inputs.Names[i.value], assets.skin)).size(cx,cy).align(alignLabel)
            scrollTable.add(txt0[i.value]).size(cx,cy).align(alignField)
            scrollTable.add(txt1[i.value]).size(cx,cy).align(alignField)
            scrollTable.row()
        }
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

        stage.addFocusableActor(backButton)
        stage.escapeActor = backButton
        stage.focusedActor = backButton
        Gdx.input.inputProcessor = stage
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
		//val v = if(value.absoluteValue < 0.1) 0f else value
        //Log.e(tag, "axisMoved:------------"+controller?.name+"--------------axis=$axisCode  value=$v")
        currentTxt?.let {
            it.text = axisCode.toString()
        }
        return false
    }
    ///
    override fun buttonUp(controller: Controller?, buttonCode: Int): Boolean {
        //Log.e(PlayerSystem.tag, "buttonUp:----------------"+controller?.name+" : "+buttonCode)
        return false
    }
    override fun buttonDown(controller: Controller?, buttonCode: Int): Boolean {
        //Log.e(PlayerSystem.tag, "buttonDown:----------------"+controller?.name+" : "+buttonCode)
        currentTxt?.let {
            it.text = buttonCode.toString()
        }
        return false
    }
    ///
    override fun accelerometerMoved(controller: Controller?, accelerometerCode: Int, value: Vector3?): Boolean {
        //Log.e(PlayerSystem.tag, "accelerometerMoved:------------"+controller?.name+" : "+accelerometerCode+" : "+value)
        return false
    }
    override fun ySliderMoved(controller: Controller?, sliderCode: Int, value: Boolean): Boolean {
        //Log.e(PlayerSystem.tag, "ySliderMoved:------------"+controller?.name+" : "+sliderCode+" : "+value)
        return false
    }
    override fun xSliderMoved(controller: Controller?, sliderCode: Int, value: Boolean): Boolean {
        //Log.e(PlayerSystem.tag, "xSliderMoved:------------"+controller?.name+" : "+sliderCode+" : "+value)
        return false
    }
    override fun povMoved(controller: Controller?, povCode: Int, value: PovDirection?): Boolean {
        //Log.e(PlayerSystem.tag, "povMoved:------------"+controller?.name+" : "+povCode+" : "+value)
        return false
    }
}