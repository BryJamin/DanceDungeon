package com.bryjamin.dancedungeon.ecs.systems.action;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionQueueSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BattleDeploymentSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.BattleScreenUISystem;
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

    public enum State {
        DEPLOYMENT, BATTLING, ONLY_STAGE
    }

    private State state = State.DEPLOYMENT;

    GestureDetector gestureDetector;
    private Viewport gameport;

    public BattleWorldInputHandlerSystem(Viewport gameport){
        gestureDetector = new GestureDetector(new BattleWorldGestures());
        this.gameport = gameport;
        multiplexer = new InputMultiplexer();
    }


    @Override
    protected void initialize() {
        multiplexer.addProcessor(stageUIRenderingSystem.stage);

        if(state != State.ONLY_STAGE) {
            multiplexer.addProcessor(gestureDetector);
        }
    }

    @Override
    protected void processSystem() {
        Gdx.input.setInputProcessor(multiplexer);
    }


    private class BattleWorldGestures extends GestureDetector.GestureAdapter {

        @Override
        public boolean tap(float x, float y, int count, int button) {

            Vector3 input = gameport.unproject(new Vector3(x, y, 0));

            if (world.getSystem(ActionQueueSystem.class).isProcessing()) return false;

            //TODO It may be beter to have the input system of 'Battle' have states. So within certain states,
            //TODO you are unabelt o interact with other objects. Such as during deployment you cna't activate movment

            //TODO this currently does this but is not clean.

            if (world.getSystem(BattleDeploymentSystem.class).isProcessing()) {

                if(world.getSystem(ActionOnTapSystem.class).touch(input.x, input.y))
                    return true;

            } else if (world.getSystem(TurnSystem.class).getTurn() == TurnSystem.TURN.ALLY) {

                if(world.getSystem(ActionOnTapSystem.class).touch(input.x, input.y))
                    return true;

                if (world.getSystem(SelectedTargetSystem.class).selectCharacter(input.x, input.y))
                    return true;


                world.getSystem(BattleScreenUISystem.class).reset();

            }

            return false;
        }
    }


    public void setState(State state) {
        this.state = state;
        multiplexer.clear();
        initialize();
    }
}
