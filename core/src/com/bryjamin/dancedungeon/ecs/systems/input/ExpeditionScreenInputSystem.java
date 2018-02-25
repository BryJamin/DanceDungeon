package com.bryjamin.dancedungeon.ecs.systems.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.ecs.systems.action.ActionOnTapSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.StageUIRenderingSystem;

/**
 * Created by BB on 25/02/2018.
 */

public class ExpeditionScreenInputSystem extends BasicInputSystem{

    private StageUIRenderingSystem stageUIRenderingSystem;

    public ExpeditionScreenInputSystem(Viewport gameport) {
        super(gameport);
    }




    @Override
    protected void processSystem() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stageUIRenderingSystem.stage);
        multiplexer.addProcessor(gestureListener);
        Gdx.input.setInputProcessor(multiplexer);
    }


    private class Adapter extends GestureDetector.GestureAdapter {

        @Override
        public boolean tap(float x, float y, int count, int button) {
            Vector3 input = gameport.unproject(new Vector3(x, y, 0));
            if (world.getSystem(ActionOnTapSystem.class).touch(input.x, input.y)) {
                return true;
            }
            return false;
        }
    }

}
