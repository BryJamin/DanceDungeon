package com.bryjamin.dancedungeon.ecs.systems.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuInputSystem extends InputSystem {

    public MenuInputSystem(Viewport gamePort) {
        super(gamePort);
    }


    @Override
    protected void initialize() {
        addStageProcessor();
        addBasicActionOnTapListener();
        addProcessor(new MenuInputAdapter());
    }


    private class MenuInputAdapter extends InputAdapter {

        @Override
        public boolean keyDown(int keycode) {

            switch (keycode){
                case Input.Keys.ESCAPE:
                case Input.Keys.BACK:
                    Gdx.app.exit();
                    break;
            }

            return super.keyDown(keycode);
        }
    }

}
