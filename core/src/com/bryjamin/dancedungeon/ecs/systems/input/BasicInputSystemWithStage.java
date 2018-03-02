package com.bryjamin.dancedungeon.ecs.systems.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.ecs.systems.ui.StageUIRenderingSystem;

/**
 * Created by BB on 25/02/2018.
 */

public class BasicInputSystemWithStage extends BasicInputSystem {

    private StageUIRenderingSystem stageUIRenderingSystem;

    public BasicInputSystemWithStage(Viewport gameport) {
        super(gameport);
    }

    @Override
    protected void processSystem() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stageUIRenderingSystem.stage);
        multiplexer.addProcessor(gestureListener);
        Gdx.input.setInputProcessor(multiplexer);
    }

}
