package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.World;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.WaitActionComponent;
import com.bryjamin.dancedungeon.ecs.systems.SkillUISystem;
import com.bryjamin.dancedungeon.utils.Pair;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 20/12/2017.
 * <p>
 * Is currently nothing to do with an action camera, but watches for actions to be completed
 * before allowing the next turn to be activated
 *
 * The way this currently works, is your queue up a set of actions using and assign it to an entity.
 *
 * You also use the WaitActionComponent to see if the entity still exists, if not their actions are removed
 * from the queue
 *
 */

public class ActionCameraSystem extends EntitySystem {


    private Queue<Pair<Entity, WorldConditionalAction>> actionQueue = new Queue<Pair<Entity, WorldConditionalAction>>();

    private WorldConditionalAction currentConditionalAction;

    private boolean hasBegun = false;

    private boolean processingFlag = true;

    /**
     * Creates an entity system that uses the specified aspect as a matcher
     * against entities.
     *
     */
    public ActionCameraSystem() {
        super(Aspect.all(WaitActionComponent.class));
    }

    @Override
    protected void processSystem() {

            if (currentConditionalAction == null) {
                System.out.println(actionQueue.size);
                actionQueue.first().getRight().performAction(world,
                        actionQueue.first().getLeft());

                currentConditionalAction = actionQueue.first().getRight();

                if (!hasBegun) {
                    hasBegun = true;
                    world.getSystem(SkillUISystem.class).reset();
                }


            } else {

                if (currentConditionalAction.condition(world, actionQueue.first().getLeft())) {
                    actionQueue.removeFirst();
                    currentConditionalAction = null;

                    if (actionQueue.size == 0) {
                        //TODO should only occur when it is the player's turn
                        hasBegun = false;

                    }

                }
            }

        if(!processingFlag){
            //Notify relevant systems
        }

    }


    public void pushLastAction(Entity e, WorldConditionalAction wca) {
        actionQueue.addLast(new Pair<Entity, WorldConditionalAction>(e, wca));
    }

    public boolean isProcessing() {
        System.out.println(actionQueue.size);
        System.out.println(this.getEntities().size());
        return actionQueue.size != 0 || !this.getEntities().isEmpty();
    }


    public void createMovementAction(Entity entity, final Iterable<Coordinates> coordinatesSequence) {

        pushLastAction(entity, new WorldConditionalAction() {
            @Override
            public boolean condition(World world, Entity entity) {
                return entity.getComponent(MoveToComponent.class).isEmpty();
            }

            @Override
            public void performAction(World world, Entity entity) {
                for (Coordinates c : coordinatesSequence) {
                    entity.getComponent(MoveToComponent.class).movementPositions.add(
                            world.getSystem(TileSystem.class).getPositionUsingCoordinates(
                                    c, entity.getComponent(CenteringBoundaryComponent.class).bound));
                }
            }
        });

    }


    public void createDeathWaitAction(Entity entity) {

        entity.edit().add(new WaitActionComponent());

        pushLastAction(entity, new WorldConditionalAction() {
            @Override
            public boolean condition(World world, Entity entity) {
                return false;
            }

            @Override
            public void performAction(World world, Entity entity) {
            }
        });

    }


    @Override
    public void removed(Entity e) {
        for(Pair<Entity, WorldConditionalAction> pair : actionQueue){
            if(e.equals(pair.getLeft())){
                actionQueue.removeValue(pair, true);
            }
        }
    }

    @Override
    protected boolean checkProcessing() {
        processingFlag = actionQueue.size != 0 || !this.getEntities().isEmpty();
        return processingFlag;
    }
}
