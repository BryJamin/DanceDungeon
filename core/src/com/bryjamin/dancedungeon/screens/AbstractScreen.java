package com.bryjamin.dancedungeon.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bryjamin.dancedungeon.MainGame;

/**
 * Created by BB on 08/10/2017.
 */

public class AbstractScreen implements Screen {

    protected MainGame game;
    protected SpriteBatch batch;
    protected AssetManager assetManager;

    public AbstractScreen(MainGame game) {
        this.game = game;
        this.batch = game.batch;
        this.assetManager = game.assetManager;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

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

