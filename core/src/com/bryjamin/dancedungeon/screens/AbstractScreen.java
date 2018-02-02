package com.bryjamin.dancedungeon.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;

/**
 * Created by BB on 08/10/2017.
 */

public class AbstractScreen implements Screen {

    protected MainGame game;
    protected Viewport gameport;
    protected OrthographicCamera gamecam;
    protected SpriteBatch batch;
    protected AssetManager assetManager;

    public AbstractScreen(MainGame game) {
        this.game = game;
        this.batch = game.batch;
        this.assetManager = game.assetManager;

        gamecam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gameport = new FitViewport(MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT, gamecam);
        gamecam.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);
        gamecam.update();
        gameport.apply();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {
        gameport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}

