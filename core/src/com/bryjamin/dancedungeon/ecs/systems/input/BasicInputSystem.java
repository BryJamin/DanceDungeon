package com.bryjamin.dancedungeon.ecs.systems.input;

import com.artemis.Aspect;
import com.artemis.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.systems.action.ActionOnTapSystem;

/**
 * Created by BB on 02/02/2018.
 */

public class BasicInputSystem extends EntitySystem{

    private Viewport gameport;

    public BasicInputSystem(Viewport gameport){
        super(Aspect.all(ActionOnTapComponent.class));
        this.gameport = gameport;
    }

    @Override
    protected void processSystem() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new Adapter());
        Gdx.input.setInputProcessor(multiplexer);
    }


    private class Adapter extends InputAdapter {

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            Vector3 input = gameport.unproject(new Vector3(screenX, screenY, 0));
            if (world.getSystem(ActionOnTapSystem.class).touch(input.x, input.y)) {
                return true;
            }
            ;
            return false;
        }
    }


}
