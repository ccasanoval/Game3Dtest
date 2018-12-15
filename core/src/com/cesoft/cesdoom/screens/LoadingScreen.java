package com.cesoft.cesdoom.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.cesoft.cesdoom.CesDoom;
import com.cesoft.cesdoom.util.Log;

/**
 * @author Mats Svensson, modified by CESoft
 */
//TODO: pass to kotlin
public class LoadingScreen implements Screen {

    private static final String tag = LoadingScreen.class.getSimpleName();

    public class LoadingBar extends Actor {

        private Animation animation;
        private TextureRegion reg;
        private float stateTime;

        LoadingBar(Animation animation) {
            this.animation = animation;
            reg = (TextureRegion) animation.getKeyFrame(0);
        }

        @Override
        public void act(float delta) {
            stateTime += delta;
            reg = (TextureRegion) animation.getKeyFrame(stateTime);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            batch.draw(reg, getX(), getY());
        }
    }


    private Stage stage;

    private Image logo;
    private Image loadingFrame;
    private Image loadingBarHidden;
    private Image screenBg;
    private Image loadingBg;

    private float startX, endX;
    private float percent;

    private Actor loadingBar;
    private CesDoom game;

    public LoadingScreen(CesDoom game) {
        super();
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage();
        game.assets.iniLoading();
        TextureAtlas atlas = game.assets.getLoading();
        // Grab the regions from the atlas and create some images
        logo = new Image(atlas.findRegion("libgdx-logo"));
        loadingFrame = new Image(atlas.findRegion("loading-frame"));
        loadingBarHidden = new Image(atlas.findRegion("loading-bar-hidden"));
        screenBg = new Image(atlas.findRegion("screen-bg"));
        loadingBg = new Image(atlas.findRegion("loading-frame-bg"));

        // Add the loading bar animation
        Animation anim = new Animation(0.05f, atlas.findRegions("loading-bar-anim"));
        anim.setPlayMode(PlayMode.LOOP_REVERSED);
        loadingBar = new LoadingBar(anim);

        // Or if you only need a static bar, you can do
        // loadingBar = new Image(atlas.findRegion("loading-bar1"));

        // Add all the actors to the stage
        stage.addActor(screenBg);
        stage.addActor(loadingBar);
        stage.addActor(loadingBg);
        stage.addActor(loadingBarHidden);
        stage.addActor(loadingFrame);
        stage.addActor(logo);

        // Add everything to be loaded
        game.loadResources();
    }

    //______________________________________________________________________________________________
    @Override
    public void resize(int width, int height) {

        int offset = 100;
        stage.getViewport().update(width, height);

        // Make the background fill the screen
        screenBg.setSize(width, height);

        // Place the logo in the middle of the screen and XX px down
        logo.setX((width - logo.getWidth()) / 2);
        logo.setY((height - logo.getHeight()) / 2 - offset/2);

        // Place the loading frame in the middle of the screen
        loadingFrame.setX((stage.getWidth() - loadingFrame.getWidth()) / 2);
        loadingFrame.setY((stage.getHeight() - loadingFrame.getHeight()) / 2 - offset);

        // Place the loading bar at the same spot as the frame, adjusted a few px
        loadingBar.setX(loadingFrame.getX() + 15);
        loadingBar.setY(loadingFrame.getY() + 5);

        // Place the image that will hide the bar on top of the bar, adjusted a few px
        loadingBarHidden.setX(loadingBar.getX() + 35);
        loadingBarHidden.setY(loadingBar.getY() - 3);
        // The start position and how far to move the hidden loading bar
        startX = loadingBarHidden.getX();
        endX = 440;

        // The rest of the hidden bar
        loadingBg.setSize(450, 50);
        loadingBg.setX(loadingBarHidden.getX() + 30);
        loadingBg.setY(loadingBarHidden.getY() + 3);
    }


    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (game.assets.update()) { // Load some, will return true if done loading
            //if (Gdx.input.isTouched()) // If the screen is touched after the game is done loading, go to the main menu screen
                //game.setScreen(new MainMenuScreen(game));
            game.setScreen(new GameScreen(game));
        }
        else {
            // Interpolate the percentage to make it more smooth
            percent = Interpolation.linear.apply(percent, game.assets.getProgress(), 0.5f);
            Log.INSTANCE.e(tag, "LoadingScreen:render:------------------------------%: " + percent);
            // Update positions (and size) to match the percentage
            loadingBarHidden.setX(startX + endX * percent);
            loadingBg.setX(loadingBarHidden.getX() + 30);
            loadingBg.setWidth(450 - 450 * percent);
            loadingBg.invalidate();
            // Show the loading screen
            stage.act();
            stage.draw();
        }
    }

    @Override
    public void hide() {
        game.assets.endLoading();
    }

    @Override
    public void dispose() { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }
}
