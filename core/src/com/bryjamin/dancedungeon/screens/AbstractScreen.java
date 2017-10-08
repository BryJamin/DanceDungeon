package com.bryjamin.dancedungeon.screens;

import com.badlogic.gdx.Screen;
import com.bryjamin.dancedungeon.MainGame;

/**
 * Created by BB on 08/10/2017.
 */

public class AbstractScreen implements Screen {

    protected MainGame game;

    public AbstractScreen(MainGame game) {
        this.game = game;
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

