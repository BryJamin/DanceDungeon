package com.bryjamin.dancedungeon.screens;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.screens.menu.MenuScreen;

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
            //This stops 'wobbly' looking Fonts on text/camera movement, may be necessary to create an 'assets'
            //setup class to not have to do this here.
            game.assetManager.get(Fonts.MEDIUM, BitmapFont.class).setUseIntegerPositions(false);
            game.assetManager.get(Fonts.SMALL, BitmapFont.class).setUseIntegerPositions(false);

            game.setScreen(new MenuScreen(game));
        }

    }
}

