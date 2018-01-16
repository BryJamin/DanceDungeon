package com.bryjamin.dancedungeon.screens.strategy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.screens.AbstractScreen;
import com.bryjamin.dancedungeon.screens.strategy.worlds.MapWorld;

/**
 * Created by BB on 17/12/2017.
 */

public class MapScreen extends AbstractScreen {

    private OrthographicCamera gamecam;
    private Viewport gameport;

    private MapWorld mapWorld;

    public MapScreen(MainGame game) {
        super(game);


        gamecam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gameport = new FitViewport(MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT, gamecam);
        gamecam.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);
        gamecam.update();
        gameport.apply();

        mapWorld = new MapWorld(game, gameport);

    }

    @Override
    public void resize(int width, int height) {
        gameport.update(width, height);
    }




    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(gamecam.combined);

        gamecam.update();
        mapWorld.process(delta);
        handleInput(delta);

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
