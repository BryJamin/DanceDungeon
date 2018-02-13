package com.bryjamin.dancedungeon.screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.screens.AbstractScreen;
import com.bryjamin.dancedungeon.screens.menu.worlds.MenuWorld;

/**
 * Created by BB on 08/10/2017.
 */

public class MenuScreen extends AbstractScreen {

    private MenuWorld menuWorld;

    public MenuScreen(MainGame game) {
        super(game);
        menuWorld = new MenuWorld(game, gameport);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(gamecam.combined);

        gamecam.update();
        menuWorld.process(delta);
        handleInput(delta);

    }

    public void handleInput(float dt) {


        InputMultiplexer multiplexer = new InputMultiplexer();
        menuWorld.handleInput(multiplexer);
        Gdx.input.setInputProcessor(multiplexer);

    }





}
