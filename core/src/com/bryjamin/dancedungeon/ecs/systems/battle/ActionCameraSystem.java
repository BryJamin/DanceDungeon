package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.utils.Pair;

/**
 * Created by BB on 20/12/2017.
 *
 * Is currently nothing to do with an action camera, but watches for actions to be completed
 * before allowing the next turn to be activated
 *
 */

public class ActionCameraSystem extends BaseSystem {


    private Queue<Pair<Entity, WorldConditionalAction>> actionQueue = new Queue<Pair<Entity, WorldConditionalAction>>();

    private WorldConditionalAction currentConditionalAction;

    @Override
    protected void processSystem() {

            if (currentConditionalAction == null) {
                actionQueue.first().getRight().performAction(world,
                        actionQueue.first().getLeft());

                currentConditionalAction = actionQueue.first().getRight();

            } else {

                if (currentConditionalAction.condition(world, actionQueue.first().getLeft())) {
                    actionQueue.removeFirst();
                    currentConditionalAction = null;

                    if(actionQueue.size == 0){
                        //TODO should only occur when it is the player's turn
                        world.getSystem(SelectedTargetSystem.class).reselectEntityAfterActionComplete();
                    }

                }
            }
    }



    public void pushLastAction(Entity e, WorldConditionalAction wca){
        actionQueue.addLast(new Pair<Entity, WorldConditionalAction>(e, wca));
    }

    public boolean isProcessing(){
        return checkProcessing();
    }


    @Override
    protected boolean checkProcessing() {
        return actionQueue.size != 0;
    }
}
