package com.bryjamin.dancedungeon.ecs.systems.action;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
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
import com.bryjamin.dancedungeon.utils.observer.Observer;

/**
 * Created by BB on 23/01/2018.
 *
 * Class used for managing the inputs and gestures used in the BattleScreen.
 *
 */

public class BattleScreenInputSystem extends BaseSystem implements Observer {

    private TurnSystem turnSystem;
    private ActionOnTapSystem actionOnTapSystem;
    private ActionQueueSystem actionQueueSystem;
    private SelectedTargetSystem selectedTargetSystem;
    private StageUIRenderingSystem stageUIRenderingSystem;
    private BattleScreenUISystem battleScreenUISystem;
    private BattleDeploymentSystem battleDeploymentSystem;
    private InputMultiplexer multiplexer;

    @Override
    public void update(Object o) {

        if(o instanceof BattleDeploymentSystem){
            if(((BattleDeploymentSystem) o).deploymentComplete()){
                this.state = State.BATTLING;
                this.resetState = State.BATTLING;
            }
        }

    }

    public enum State {
        DEPLOYMENT, BATTLING, ONLY_STAGE
    }

    private State state = State.DEPLOYMENT;
    private State resetState = State.DEPLOYMENT;

    GestureDetector gestureDetector;
    private Viewport gameport;

    public BattleScreenInputSystem(Viewport gameport){
        gestureDetector = new GestureDetector(new BattleWorldGestures());
        this.gameport = gameport;
        multiplexer = new InputMultiplexer();
    }


    @Override
    protected void initialize() {
        battleDeploymentSystem.getObservers().addObserver(this);
        refreshInputProcessor();
    }

    private void refreshInputProcessor(){
        multiplexer.clear();
        multiplexer.addProcessor(new BattleWorldInput());
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

            //No Interaction Can Be Done on Entities When Actions Are Queued
            if (actionQueueSystem.isProcessing()) return false;

            switch (state){

                case DEPLOYMENT: //During Deployment There Are No Turns So Tapping is Unrestricted
                    if(actionOnTapSystem.touch(input.x, input.y))
                        return true;
                    break;

                case BATTLING: //During Battling Players Can Only Interact With Their Characters When it is Their Turn.

                    if (turnSystem.getTurn() == TurnSystem.TURN.PLAYER) {

                        if(world.getSystem(ActionOnTapSystem.class).touch(input.x, input.y))
                            return true;

                        if (selectedTargetSystem.selectCharacter(input.x, input.y))
                            return true;

                        world.getSystem(BattleScreenUISystem.class).resetBottomContainer();
                    }
                    break;

            }
            return false;
        }
    }




    private class BattleWorldInput extends InputAdapter {

        @Override
        public boolean keyDown(int keycode) {

            System.out.println(keycode);

            if(keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE){

                battleScreenUISystem.openQuitMenu();


                return true;
                // Do your optional back button handling (show pause menu?)
            }

            return false;
        }

    }


    /**
     * Restricts the input tracked to only the LibGDX 'Stage'
     */
    public void restrictInputToStage(){
        this.state = State.ONLY_STAGE;
        refreshInputProcessor();
    }


    /**
     * Opens the input.
     */
    public void unRestrictInput(){
        this.state = resetState;
        refreshInputProcessor();
    }


}
