package com.bryjamin.dancedungeon.ecs.systems.action;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.ecs.systems.SkillUISystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionCameraSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.SelectedTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.StageUIRenderingSystem;

/**
 * Created by BB on 23/01/2018.
 *
 * Class used for managing the inputs and gestures used through the 'Battle' World
 *
 *
 */

public class BattleWorldInputHandlerSystem extends BaseSystem {

    private TurnSystem turnSystem;
    private ActionOnTapSystem actionOnTapSystem;
    private SelectedTargetSystem selectedTargetSystem;
    private StageUIRenderingSystem stageUIRenderingSystem;
    private InputMultiplexer multiplexer;


    GestureDetector gestureDetector;
    private Viewport gameport;

    public BattleWorldInputHandlerSystem(Viewport gameport){
        gestureDetector = new GestureDetector(new BattleWorldGestures());
        this.gameport = gameport;
        multiplexer = new InputMultiplexer();
    }




    @Override
    protected void processSystem() {
        multiplexer.addProcessor(stageUIRenderingSystem.stage);
        multiplexer.addProcessor(gestureDetector);
        Gdx.input.setInputProcessor(multiplexer);
    }


    private class BattleWorldGestures extends GestureDetector.GestureAdapter {

        @Override
        public boolean tap(float x, float y, int count, int button) {

            Vector3 input = gameport.unproject(new Vector3(x, y, 0));

            if (world.getSystem(ActionCameraSystem.class).isProcessing()) return false;

            if (world.getSystem(TurnSystem.class).getTurn() == TurnSystem.TURN.ALLY) {

                if(world.getSystem(SkillUISystem.class).touch(input.x, input.y))
                   return true;
                if (world.getSystem(ActionOnTapSystem.class).touch(input.x, input.y))
                    return true;

                if (world.getSystem(SelectedTargetSystem.class).selectCharacter(input.x, input.y))
                    return true;

                world.getSystem(SkillUISystem.class).reset();

            }
            return false;
        }
    }



}
