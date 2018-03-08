package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.World;
import com.badlogic.gdx.math.Vector3;
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


    private OrderedMap<WorldConditionalAction, Entity> queuedActioEntityMap = new OrderedMap<WorldConditionalAction, Entity>();

    private Queue<WorldConditionalAction> actionQueue = new Queue<WorldConditionalAction>();

    private ComponentMapper<WaitActionComponent> waitActionMapper;
    private ComponentMapper<MoveToComponent> mtcMapper;

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


    private boolean isActionEntityDead(){

        boolean isEntityDead = true;

        for(Entity e : this.getEntities()){
            if(queuedActioEntityMap.get(actionQueue.first()).equals(e)){
                isEntityDead = false;
                break;
            }
        }

        return isEntityDead;
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

                if(isActionEntityDead()) {
                    actionQueue.removeFirst();
                    return;
                }

                Entity queued = queuedActioEntityMap.get(actionQueue.first());
                actionQueue.first().performAction(world, queued);
                queued.edit().add(new WaitActionComponent());
                state = State.WAIT_ACTION;
                break;

            case WAIT_ACTION:

                if(isActionEntityDead()) {
                    if(actionQueue.size == 0) return;
                    actionQueue.removeFirst();
                    state = State.PERFORM_ACTION;
                    return;
                }


                if (actionQueue.first().condition(world, queuedActioEntityMap.get(actionQueue.first()))) {

                    Entity current = queuedActioEntityMap.get(actionQueue.first());

                    actionQueue.removeFirst();
                    state = State.PERFORM_ACTION;

                    boolean noMoreActions = true;

                    for(WorldConditionalAction wca : actionQueue){
                        if(queuedActioEntityMap.get(wca).equals(current)){
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


    @Override
    public void removed(Entity e) {


    }

    public void pushLastAction(Entity e, WorldConditionalAction wca) {//Pushes a new action to the queue and adds a WaitActionComponent
        actionQueue.addLast(wca);
        queuedActioEntityMap.put(wca, e);
        e.edit().add(new WaitActionComponent());
    }

    public boolean isProcessing() {
        return checkProcessing();
    }


    public void createMovementAction(Entity entity, final Iterable<Coordinates> coordinatesSequence) {

        if(!mtcMapper.has(entity)) return;

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

    public void createMovementAction(Entity entity, final Vector3... positions){

        if(!mtcMapper.has(entity)) return;

        pushLastAction(entity, new WorldConditionalAction() {
            @Override
            public boolean condition(World world, Entity entity) {
                return entity.getComponent(MoveToComponent.class).isEmpty();
            }

            @Override
            public void performAction(World world, Entity entity) {

                if(entity.getComponent(MoveToComponent.class) == null) {
                    return;
                }


                for (Vector3 p : positions) {
                    entity.getComponent(MoveToComponent.class).movementPositions.add(p);
                }
            }
        });
    }



    public void createIntentAction(Entity entity){


        pushLastAction(entity, new WorldConditionalAction() {
            @Override
            public boolean condition(World world, Entity entity) {
                return true;
            }

            @Override
            public void performAction(World world, Entity entity) {
                world.getSystem(EnemyIntentSystem.class).updateIntent();
            }
        });
    }


    @Override
    protected boolean checkProcessing() {
        processingFlag = actionQueue.size != 0;
        return processingFlag;
    }
}
