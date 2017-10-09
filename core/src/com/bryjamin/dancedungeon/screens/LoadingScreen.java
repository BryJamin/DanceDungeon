package com.bryjamin.dancedungeon.screens;

import com.bryjamin.dancedungeon.MainGame;

/**
 * Created by BB on 08/10/2017.
 *
 * This can be used to display a default image while the game loads
 *
 */

public class LoadingScreen extends AbstractScreen {


    public LoadingScreen(MainGame game) {
        super(game);
    }


    @Override
    public void render(float delta) {

        if(game.assetManager.update())
        {
            game.setScreen(new MenuScreen(game));
        }

    }
}

