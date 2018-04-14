package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.World;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.Observer;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.QueuedActionComponent;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 20/12/2017.
 * <p>
 * Is currently nothing to do with an action camera, but watches for actions to be completed
 * before allowing the next turn to be activated
 * <p>
 * The way this currently works, is your queue up a set of actions using and assign it to an entity.
 * <p>
 * You also use the QueuedActionComponent to see if the entity still exists, if not their actions are removed
 * from the queue
 */

public class ActionCameraSystem extends EntitySystem {


   // private OrderedMap<WorldConditionalAction, Entity> queuedActionMap = new OrderedMap<WorldConditionalAction, Entity>();

    private OrderedMap<Entity, Array<WorldConditionalAction>> queuedActionMap = new OrderedMap<>();

    private ComponentMapper<MoveToComponent> mtcMapper;

    public Array<Observer> observerArray = new Array<>();
    private Queue<Array<PushedAction>> actionQueue = new Queue<>();

    private Array<PushedAction> asyncActionArray = new Array<>();

    private boolean asynchronous = false;

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
        super(Aspect.all(QueuedActionComponent.class));
    }


    /**
     * Removes entities that are no longer apart of the system from the queue.
     *
     * Sometimes an entity can be deleted or die, mid-action. This avoids null pointers from occuring.
     */
    private void removeDeadEntityActionsFromQueue(){

        Array<PushedAction> copy = new Array<>(actionQueue.first());

        System.out.println("Before " + actionQueue.size);

        for(PushedAction p : copy){

            boolean remove = true;
            for(Entity e : this.getEntities()){
                if(p.entity.equals(e)){
                    remove = false;
                    System.out.println("Break");
                    break;
                }
            }

            if(remove) {
                actionQueue.first().removeValue(p, true);
            }

        }

        System.out.println("After " + actionQueue.size);

    }


    @Override
    protected void processSystem() {

        if(asyncActionArray.size > 0){
            actionQueue.addLast(asyncActionArray);
            asyncActionArray.clear();
        }

        if(actionQueue.size == 0){

            //If the system has no more actions in the queue, it turns it self off
            //and notifies all observers

            state = State.PERFORM_ACTION; //Resets the state

            if(processingFlag){
                for(Observer o : observerArray){
                    o.onNotify();
                }
            }

            processingFlag = false;

        } else {
            processingFlag = true;
        }


        switch (state) {

            case PERFORM_ACTION:

                if (actionQueue.size == 0) return;

                removeDeadEntityActionsFromQueue();

                for(PushedAction p : actionQueue.first()){
                    p.worldConditionalAction.performAction(world, p.entity);
                    System.out.println("Action Qeue size " + actionQueue.size);
                    p.entity.edit().add(new QueuedActionComponent());
                }

                state = State.WAIT_ACTION;
                break;

            case WAIT_ACTION:

                removeDeadEntityActionsFromQueue(); //Checks for entites that may have died during the wait.

                if(actionQueue.first().size == 0){ //If all entities are dead return and go to the next in the queue
                    state = State.PERFORM_ACTION;
                    actionQueue.removeFirst();
                    return;
                }

                boolean finished = true;

                for(PushedAction p : actionQueue.first()){
                    if(!p.worldConditionalAction.condition(world, p.entity)){

                        finished = false;
                    }
                }

                //TODO Pushing into enemy animatios n have stopped for some reason. Not sure how to fix it.
                //TODO probably due to the actions being deleted from the Array, Use debug toll to solve it.

                if(finished){


                    System.out.println("Moving onto the next size is" + actionQueue.size);

                    for(PushedAction p : actionQueue.first()){

                        //Check if the entity is featured in any other actions before removing the Component
                        queuedActionMap.get(p.entity).removeValue(p.worldConditionalAction, true);

                        if(queuedActionMap.get(p.entity).size == 0){
                            p.entity.edit().remove(QueuedActionComponent.class);
                            queuedActionMap.remove(p.entity);
                        }
                    }

                    actionQueue.removeFirst();
                    state = State.PERFORM_ACTION;
                }

        }

    }

    public void pushLastAction(Entity e, WorldConditionalAction wca) {

        actionQueue.addLast(new Array<>(new PushedAction[]{new PushedAction(e, wca)}));

        if(queuedActionMap.containsKey(e)){ //Checks if the entity already have queued actions.
            queuedActionMap.get(e).add(wca); //Adds the given action to the entity's action list.
        } else {//Adds an identifier component to new entity's to the system
            e.edit().add(new QueuedActionComponent());
            queuedActionMap.put(e, new Array<WorldConditionalAction>());
        }

        processingFlag = true; //Turns on the system
    }

    private void pushAsyncAction(Entity e, WorldConditionalAction wca){

        asyncActionArray.add(new PushedAction(e, wca));

        if(queuedActionMap.containsKey(e)){ //Checks if the entity already have queued actions.
            queuedActionMap.get(e).add(wca); //Adds the given action to the entity's action list.
        } else {//Adds an identifier component to new entity's to the system
            e.edit().add(new QueuedActionComponent());
            queuedActionMap.put(e, new Array<WorldConditionalAction>());
        }

        processingFlag = true; //Turns on the system


    }

    public boolean isProcessing() {
        return processingFlag;
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

                System.out.println("MOVEMENT ACTION ");

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
                System.out.println("Death wait");
                return false;
            }

            @Override
            public void performAction(World world, Entity entity) {
            }
        });

    }

    public void createDeathWaitAction(Entity entity, boolean asynchronous) {

        pushAsyncAction(entity, new WorldConditionalAction() {
            @Override
            public boolean condition(World world, Entity entity) {
                System.out.println("Death wait");
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

                System.out.println("MOVEMENT ACTION ");

                for (Vector3 p : positions) {
                    entity.getComponent(MoveToComponent.class).movementPositions.add(p);
                }
            }
        });
    }


    private class PushedAction {

        private Entity entity;
        private WorldConditionalAction worldConditionalAction;

        public PushedAction(Entity entity, WorldConditionalAction worldConditionalAction){
            this.entity = entity;
            this.worldConditionalAction = worldConditionalAction;
        }

    }



    public void createDamageApplicationAction(Entity entity, final int damage){ //TODO check if it has health component

        if(!mtcMapper.has(entity)) return;

        pushLastAction(entity, new WorldConditionalAction() {
            @Override
            public boolean condition(World world, Entity entity) {
                return true;
            }

            @Override
            public void performAction(World world, Entity entity) {

                if(entity.getComponent(HealthComponent.class) == null) {
                    return;
                }
                entity.getComponent(HealthComponent.class).applyDamage(damage);
            }
        });
    }


    public void createASyncMovementAction(Entity entity, final Vector3... positions){

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


/*    protected boolean checkProcessing() {
        processingFlag = actionQueue.size != 0;
        return processingFlag;
    }*/
}
