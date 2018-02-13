package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.World;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.WaitActionComponent;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 20/12/2017.
 * <p>
 * Is currently nothing to do with an action camera, but watches for actions to be completed
 * before allowing the next turn to be activated
 * <p>
 * The way this currently works, is your queue up a set of actions using and assign it to an entity.
 * <p>
 * You also use the WaitActionComponent to see if the entity still exists, if not their actions are removed
 * from the queue
 */

public class ActionCameraSystem extends EntitySystem {


    private OrderedMap<WorldConditionalAction, Entity> queuedActionMap = new OrderedMap<WorldConditionalAction, Entity>();

    private Queue<WorldConditionalAction> actionQueue = new Queue<WorldConditionalAction>();

    private ComponentMapper<WaitActionComponent> waitActionMapper;


    private WorldConditionalAction currentConditionalAction;

    private boolean hasBegun = false;

    private boolean processingFlag = true;

    private enum State {
        PERFORM_ACTION, WAIT_ACTION, END_ACTION
    }

    private State state = State.PERFORM_ACTION;

    /**
     * Creates an entity system that uses the specified aspect as a matcher
     * against entities.
     */
    public ActionCameraSystem() {
        super(Aspect.all(WaitActionComponent.class));
    }

    @Override
    protected void processSystem() {

        if(this.getEntities().size() == 0){
            actionQueue.clear();
            state = State.PERFORM_ACTION;
        }


        switch (state) {

            case PERFORM_ACTION:
                if (actionQueue.size == 0) return;

                boolean skip = true;

                for(Entity e : this.getEntities()){
                    if(queuedActionMap.get(actionQueue.first()).equals(e)){
                        skip = false;
                        break;
                    }
                }

                if(skip) return;

                actionQueue.first().performAction(world,
                        queuedActionMap.get(actionQueue.first()));

                queuedActionMap.get(actionQueue.first()).edit().add(new WaitActionComponent());
                state = State.WAIT_ACTION;
                break;

            case WAIT_ACTION:

                boolean skip2 = true;

                for(Entity e : this.getEntities()){
                    if(queuedActionMap.get(actionQueue.first()).equals(e)){
                        skip2 = false;
                        break;
                    }
                }

                if(skip2) {
                    if(actionQueue.size == 0) return;
                    actionQueue.removeFirst();
                    state = State.PERFORM_ACTION;
                    return;
                }


                if (actionQueue.first().condition(world, queuedActionMap.get(actionQueue.first()))) {




                    Entity current = queuedActionMap.get(actionQueue.first());


                    actionQueue.removeFirst();
                    state = State.PERFORM_ACTION;



                    boolean noMoreActions = true;


                    for(WorldConditionalAction wca : actionQueue){
                        if(queuedActionMap.get(wca).equals(current)){
                            noMoreActions = false;
                            break;
                        }
                    }

                    if(noMoreActions){
                        current.edit().remove(WaitActionComponent.class);
                    }


                }

        }

    }


    public void pushLastAction(Entity e, WorldConditionalAction wca) {
        actionQueue.addLast(wca);
        queuedActionMap.put(wca, e);
        e.edit().add(new WaitActionComponent());
    }

    public boolean isProcessing() {
        return checkProcessing();
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
    }

    @Override
    protected boolean checkProcessing() {
        processingFlag = actionQueue.size != 0;
        return processingFlag;
    }
}
