package com.bryjamin.dancedungeon.utils.math;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by BB on 17/01/2018.
 */

public class CameraMath {

    public static float getBtmLftX(Viewport viewport){
        return getBtmLftX(viewport.getCamera());
    }

    public static float getBtmLftX(Camera camera){
        return camera.position.x - camera.viewportWidth / 2;
    }

    public static float getBtmY(Camera camera){
        return camera.position.y - camera.viewportHeight / 2;
    }

    public static float getBtmRightX(Viewport viewport){
        return getBtmRightX(viewport.getCamera());
    }

    public static float getBtmRightX(Camera camera){
        return getBtmLftX(camera) + camera.viewportWidth;
    }

    public static void setBtmRightX(Viewport viewport, float x){
        setBtmRightX(viewport.getCamera(), x);
    }

    public static void setBtmRightX(Camera camera, float x){
        camera.position.x = (x + camera.viewportWidth / 2) -camera.viewportWidth;
    }


    public static void setBtmLeftX(Viewport viewport, float x){
        setBtmLeftX(viewport.getCamera(), x);
    }

    public static void setBtmLeftX(Camera camera, float x){
        camera.position.x = (x + camera.viewportWidth / 2);
    }

}

