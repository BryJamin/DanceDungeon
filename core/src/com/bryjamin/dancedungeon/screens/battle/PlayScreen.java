package com.bryjamin.dancedungeon.screens.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.screens.AbstractScreen;
import com.bryjamin.dancedungeon.screens.battle.worlds.BattleWorld;
import com.bryjamin.dancedungeon.screens.battle.worlds.EndBattleWorld;


/**
 * Created by BB on 11/10/2017.
 */

public class PlayScreen extends AbstractScreen {

    private OrthographicCamera gamecam;
    private Viewport gameport;

    private BattleWorld battleWorld;
    private EndBattleWorld endBattleWorld;


    private enum ScreenState {
        BATTLE, PAUSED, END_BATTLE
    }


    private ScreenState screenState = ScreenState.BATTLE;

    public PlayScreen(MainGame game) {
        super(game);

        gamecam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gameport = new FitViewport(MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT, gamecam);
        gamecam.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);
        gamecam.update();
        gameport.apply();

        this.battleWorld = new BattleWorld(game, gameport);

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

        handleInput(delta);

        battleWorld.process(delta);

        switch(screenState){
            case BATTLE:
                break;
            case END_BATTLE:
                endBattleWorld.process(delta);
                break;
        }

        //GameDelta.delta(world, delta);
        //world.process();


    }

    public void handleInput(float dt) {

        InputMultiplexer multiplexer = new InputMultiplexer();

        switch (screenState) {
            case BATTLE: battleWorld.handleInput(multiplexer);
                break;
            case END_BATTLE: endBattleWorld.handleInput(multiplexer);
                break;
        }

        Gdx.input.setInputProcessor(multiplexer);

    }



    public void victory(){
        screenState = ScreenState.END_BATTLE;
        this.endBattleWorld = new EndBattleWorld(game, gameport, EndBattleWorld.State.VICTORY);
    }


    public void defeat(){
        screenState = ScreenState.END_BATTLE;
        this.endBattleWorld = new EndBattleWorld(game, gameport, EndBattleWorld.State.DEFEAT);
    }






}
