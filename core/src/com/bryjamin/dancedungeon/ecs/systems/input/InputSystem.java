package com.bryjamin.dancedungeon.ecs.systems.input;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.ecs.systems.action.ActionOnTapSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.StageUIRenderingSystem;

public class InputSystem extends BaseSystem{

    protected StageUIRenderingSystem stageUIRenderingSystem;
    protected InputMultiplexer multiplexer = new InputMultiplexer();

    protected Viewport gamePort;

    public InputSystem(Viewport gamePort){
        this.gamePort = gamePort;
    }


    @Override
    protected void processSystem() {
        Gdx.input.setInputProcessor(multiplexer);
    }

    protected void addBasicActionOnTapListener(){
        addProcessor(new GestureDetector(new Adapter()));
    }

    protected void addStageProcessor(){
        addProcessor(stageUIRenderingSystem.stage);
    }


    protected void addProcessor(InputProcessor processor){
        multiplexer.addProcessor(processor);
    }

    private class Adapter extends GestureDetector.GestureAdapter {

        @Override
        public boolean tap(float x, float y, int count, int button) {
            Vector3 input = gamePort.unproject(new Vector3(x, y, 0));
            return world.getSystem(ActionOnTapSystem.class).touch(input.x, input.y);
        }
    }


}
