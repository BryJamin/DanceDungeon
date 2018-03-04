package com.bryjamin.dancedungeon.ecs.systems.input;

import com.artemis.Aspect;
import com.artemis.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.map.MapNodeComponent;
import com.bryjamin.dancedungeon.ecs.systems.FixedToCameraPanAndFlingSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.ActionOnTapSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.StageUIRenderingSystem;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CameraMath;

/**
 * Created by BB on 07/01/2018.
 */

public class MapInputSystem extends EntitySystem {


    private StageUIRenderingSystem stageUIRenderingSystem;

    private Viewport gameport;
    private MainGame game;
    private float minX;
    private float maxX;

    private GestureDetector gd;

    private boolean disable = false;

    private enum State {
        MENU_OPEN, MENU_CLOSED
    }

    public State state = State.MENU_CLOSED;


    public MapInputSystem(MainGame game, Viewport gameport, float minX, float maxX) {
        super(Aspect.all(MapNodeComponent.class, HitBoxComponent.class));
        this.gameport = gameport;
        this.game = game;
        this.minX = minX;
        this.maxX = maxX;
        this.gd = new GestureDetector(new MapGestures());
    }

    @Override
    protected void processSystem() {

        InputMultiplexer multiplexer = new InputMultiplexer();

        switch (state) {

            case MENU_OPEN:
                multiplexer.addProcessor(stageUIRenderingSystem.stage);
                break;

            case MENU_CLOSED:
                multiplexer.addProcessor(stageUIRenderingSystem.stage);
                multiplexer.addProcessor(gd);
                break;


        }
        Gdx.input.setInputProcessor(multiplexer);
    }


    public void openMenu(){
        state = State.MENU_OPEN;
    }

    public void closedMenu(){
        state = State.MENU_CLOSED;
    }



    private class MapGestures extends GestureDetector.GestureAdapter {


        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {
            world.getSystem(FixedToCameraPanAndFlingSystem.class).stopFling();
            return false;
        }

        @Override
        public boolean tap(float x, float y, int count, int button) {
            Vector3 input = gameport.unproject(new Vector3(x, y, 0));
            if (world.getSystem(ActionOnTapSystem.class).touch(input.x, input.y)) {
                return true;
            }
            ;
            return false;
        }


        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY) {

            gameport.getCamera().translate(-deltaX * Measure.units(0.15f), 0, 0);

            if (CameraMath.getBtmLftX(gameport) < minX) {
                CameraMath.setBtmLeftX(gameport, minX);
            } else if (CameraMath.getBtmRightX(gameport) > maxX) {
                CameraMath.setBtmRightX(gameport, maxX);
            }
            gameport.getCamera().update();


            return true;
        }


        @Override
        public boolean fling(float velocityX, float velocityY, int button) {
            world.getSystem(FixedToCameraPanAndFlingSystem.class).flingCamera(-velocityX, velocityY);
            return false;
        }
    }


}

