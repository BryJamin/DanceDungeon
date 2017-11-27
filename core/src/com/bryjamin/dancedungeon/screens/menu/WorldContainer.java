package com.bryjamin.dancedungeon.screens.menu;

import com.artemis.World;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.utils.GameDelta;

/**
 * Created by BB on 26/11/2017.
 */

public abstract class WorldContainer extends InputAdapter {

    protected World world;

    protected MainGame game;
    protected SpriteBatch batch;
    protected Viewport gameport;

    public WorldContainer(MainGame game, Viewport gameport){
        this.game = game;
        this.batch = game.batch;
        this.gameport = gameport;
    }


    public void process(float delta){
        GameDelta.delta(world, delta);
    }



}
