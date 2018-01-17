package com.bryjamin.dancedungeon.utils.math;

import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by BB on 17/01/2018.
 */

public class CameraMath {

    public static float getBtmLftX(Viewport viewport){
        return viewport.getCamera().position.x - viewport.getCamera().viewportWidth / 2;
    }

    public static float getBtmRightX(Viewport viewport){
        return getBtmLftX(viewport) + viewport.getCamera().viewportWidth;
    }

    public static void setBtmRightX(Viewport viewport, float x){
        viewport.getCamera().position.x = (x + viewport.getCamera().viewportWidth / 2) - viewport.getCamera().viewportWidth;
    }

}

