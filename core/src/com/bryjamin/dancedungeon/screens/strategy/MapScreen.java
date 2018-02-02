package com.bryjamin.dancedungeon.screens.strategy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.screens.AbstractScreen;
import com.bryjamin.dancedungeon.screens.strategy.worlds.MapWorld;

/**
 * Created by BB on 17/12/2017.
 */

public class MapScreen extends AbstractScreen {

    private MapWorld mapWorld;

    public MapScreen(MainGame game) {
        super(game);
        mapWorld = new MapWorld(game, gameport);
    }


    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput(delta);
        mapWorld.process(delta);
        gamecam.update();


    }


    public void handleInput(float dt) {
        InputMultiplexer multiplexer = new InputMultiplexer();
        mapWorld.handleInput(multiplexer);
        Gdx.input.setInputProcessor(multiplexer);
    }



    public void battleVictory(){
        mapWorld.victory();
    }

}
