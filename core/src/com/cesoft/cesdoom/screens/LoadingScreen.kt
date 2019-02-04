package com.cesoft.cesdoom.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.cesoft.cesdoom.CesDoom

/**
 * @author Mats Svensson, modified by CESoft
 */
class LoadingScreen(private val game: CesDoom) : Screen {

    companion object {
        private val tag = LoadingScreen::class.java.simpleName
    }

    private lateinit var stage: Stage
    private lateinit var logo: Image
    private lateinit var loadingFrame: Image
    private lateinit var loadingBarHidden: Image
    private lateinit var screenBg: Image
    private lateinit var loadingBg: Image
    private lateinit var loadingBar: Actor

    private var startX: Float = 0f
    private var endX: Float = 0f
    private var percent: Float = 0f


    inner class LoadingBar internal constructor(private val animation: Animation<*>) : Actor() {
        private var reg: TextureRegion? = null
        private var stateTime: Float = 0.toFloat()

        init {
            reg = animation.getKeyFrame(0f) as TextureRegion
        }

        override fun act(delta: Float) {
            stateTime += delta
            reg = animation.getKeyFrame(stateTime) as TextureRegion
        }

        override fun draw(batch: Batch?, parentAlpha: Float) {
            batch!!.draw(reg, x, y)
        }
    }

    //______________________________________________________________________________________________
    /// Implements: Screen

    override fun show() {
        stage = Stage()
        CesDoom.instance.assets.iniLoading()
        val atlas = CesDoom.instance.assets.getLoading()
        // Grab the regions from the atlas and create some images
        logo = Image(atlas.findRegion("libgdx-logo"))
        loadingFrame = Image(atlas.findRegion("loading-frame"))
        loadingBarHidden = Image(atlas.findRegion("loading-bar-hidden"))
        screenBg = Image(atlas.findRegion("screen-bg"))
        loadingBg = Image(atlas.findRegion("loading-frame-bg"))

        // Add the loading bar animation
        val anim = Animation(0.05f, atlas.findRegions("loading-bar-anim"))
        anim.playMode = Animation.PlayMode.LOOP_REVERSED
        loadingBar = LoadingBar(anim)

        // Or if you only need a static bar, you can do
        // loadingBar = new Image(atlas.findRegion("loading-bar1"));

        // Add all the actors to the stage
        stage.addActor(screenBg)
        stage.addActor(loadingBar)
        stage.addActor(loadingBg)
        stage.addActor(loadingBarHidden)
        stage.addActor(loadingFrame)
        stage.addActor(logo)

        // Add everything to be loaded
        game.loadResources()
    }

    //______________________________________________________________________________________________
    override fun resize(width: Int, height: Int) {

        stage.viewport.update(width, height)

        // Background
        screenBg.setSize(width.toFloat(), height.toFloat())

        // Logo
        val h = height * 2.5f / 4f
        val w = width * 2.5f / 4f//logo.getPrefWidth()
        logo.setSize(w, h)
        logo.x = (width - logo.width) / 2
        logo.y = (height - logo.height) / 2

        // Bar frame
        loadingFrame.x = (stage.width - loadingFrame.width) / 2
        loadingFrame.y = height.toFloat() - loadingFrame.prefHeight - 50f

        // Bar
        loadingBar.x = loadingFrame.x + 15
        loadingBar.y = loadingFrame.y + 5

        // Place the image that will hide the bar on top of the bar, adjusted a few px
        //loadingBarHidden.setScale(2);
        loadingBarHidden.x = loadingBar.x + 35
        loadingBarHidden.y = loadingBar.y - 3
        // The start position and how far to move the hidden loading bar
        startX = loadingBarHidden.x
        endX = 440f

        // The rest of the hidden bar
        loadingBg.setSize(450f, 50f)
        //loadingBg.setScale(2);
        loadingBg.x = loadingBarHidden.x + 30
        loadingBg.y = loadingBarHidden.y + 3
    }

    override fun render(delta: Float) {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        if (CesDoom.instance.assets.update()) { // Load some, will return true if done loading
            game.setScreen(GameScreen())
        } else {
            // Interpolate the percentage to make it more smooth
            percent = Interpolation.linear.apply(percent, game.assets.getProgress(), 0.8f)
            //Log.INSTANCE.e(tag, "LoadingScreen:render:------------------------------%: " + percent);
            // Update positions (and size) to match the percentage
            loadingBarHidden.x = startX + endX * percent
            loadingBg.x = loadingBarHidden.x + 30
            loadingBg.width = 450 - 450 * percent
            loadingBg.invalidate()
            // Show the loading screen
            stage.act()
            stage.draw()
        }
    }

    override fun hide() {
        game.assets.endLoading()
    }
    override fun dispose() {}
    override fun pause() {}
    override fun resume() {}
}
