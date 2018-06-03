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
import com.bryjamin.dancedungeon.utils.observer.Observable;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.QueuedActionComponent;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

import java.util.UUID;

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

public class ActionQueueSystem extends EntitySystem {

    private OrderedMap<Entity, Array<WorldConditionalAction>> queuedActionMap = new OrderedMap<>();


    private Queue<String> queuedActionIds = new Queue<>();
    private OrderedMap<String, Array<PushedAction>> actionMonitor = new OrderedMap<>();

    private ComponentMapper<MoveToComponent> mtcMapper;

    public Observable observable = new Observable();
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
    public ActionQueueSystem() {
        super(Aspect.all(QueuedActionComponent.class));
    }


    /**
     * Removes entities that are no longer apart of the system from the queue.
     *
     * Sometimes an entity can be deleted or die, mid-action. This avoids null pointers from occuring.
     */
    private void removeDeadEntityActionsFromQueue(Array<PushedAction> pushedActions){

        Array<PushedAction> copy = new Array<>(pushedActions);

        for(PushedAction p : copy){

            boolean remove = true;
            for(Entity e : this.getEntities()){
                if(p.entity.equals(e)){
                    remove = false;
                    break;
                }
            }

            if(remove) {
                pushedActions.removeValue(p, true);
            }

        }

    }


    @Override
    protected void processSystem() {

        if(queuedActionIds.size == 0){

            //If the system has no more actions in the queue, it turns it self off
            //and notifies all observers

            //state = State.PERFORM_ACTION; //Resets the state

            if(processingFlag){
                observable.notifyObservers(this);
            }

            processingFlag = false;

            return;

        } else {
            processingFlag = true;
        }

        Array<PushedAction> pushedActions = actionMonitor.get(queuedActionIds.first());

        removeDeadEntityActionsFromQueue(pushedActions);

        boolean complete = true;

        for(PushedAction p : pushedActions){ //Performs any actions stored in the first action group in the queue

            if(!p.isActionPerformed()){
                p.performAction();
                complete = false;
            } else {

                if(!p.isCompleted()){
                    complete = false;
                }

            }
        }

        if(complete){ //If all actions have been completed remove this action group from the queue.

            for(PushedAction p : pushedActions){

                //Remove the action from the map.
                queuedActionMap.get(p.entity).removeValue(p.worldConditionalAction, true);

                //Checks if the entity is featured in any other actions before removing the identifying Component
                if(queuedActionMap.get(p.entity).size == 0){
                    p.entity.edit().remove(QueuedActionComponent.class);
                    queuedActionMap.remove(p.entity);
                }
            }

            queuedActionIds.removeFirst();
        }

    }


    /**
     * Pushes a new action into the system checks if the action is apart of an 'action id' group that may exist.
     * @param e - Entity assosiated with the action
     * @param wca - The action
     * @param id - The action id group.
     */
    public void pushLastAction(Entity e, WorldConditionalAction wca, String id) {

        if(queuedActionIds.indexOf(id, false) == -1){ //Checks if the id group exists, if it doesn't it creates it.
            queuedActionIds.addLast(id);
            actionMonitor.put(id, new Array<PushedAction>());
            actionMonitor.get(id).add(new PushedAction(e, wca, id));
        } else {
            actionMonitor.get(id).add(new PushedAction(e, wca, id));
        }

        if(queuedActionMap.containsKey(e)){ //Checks if the entity already has queued actions.
            queuedActionMap.get(e).add(wca); //Adds the given action to the entity's action list.
        } else {//Adds an identifier component to new entity's to the system
            e.edit().add(new QueuedActionComponent());
            queuedActionMap.put(e, new Array<WorldConditionalAction>());
        }
        processingFlag = true; //Turns on the system
    }


    public void pushLastAction(Entity e, WorldConditionalAction wca) {
        pushLastAction(e, wca, UUID.randomUUID().toString());
        processingFlag = true; //Turns on the system
    }

    public int getSizeOfQueue(){
        return queuedActionIds.size;
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

                for (Coordinates c : coordinatesSequence) {
                    entity.getComponent(MoveToComponent.class).movementPositions.add(
                            world.getSystem(TileSystem.class).getPositionUsingCoordinates(
                                    c, entity.getComponent(CenteringBoundComponent.class).bound));
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

    public void createDeathWaitAction(Entity entity, String id) {

        pushLastAction(entity, new WorldConditionalAction() {
            @Override
            public boolean condition(World world, Entity entity) {
                return false;
            }

            @Override
            public void performAction(World world, Entity entity) {
            }
        }, id);

    }

    public void createMovementAction(Entity entity, final Vector3... positions){

        if(!mtcMapper.has(entity)) return;

        pushLastAction( entity, new WorldConditionalAction() {
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

    public void createMovementAction(Entity entity, String id, final Vector3... positions){

        if(!mtcMapper.has(entity)) return;

        pushLastAction( entity, new WorldConditionalAction() {
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
        }, id);
    }


    private class PushedAction {

        private String actionGroupId = UUID.randomUUID().toString();
        private boolean isActionPerformed = false;
        private Entity entity;
        private WorldConditionalAction worldConditionalAction;

        public PushedAction(Entity entity, WorldConditionalAction worldConditionalAction, String id){
            this.entity = entity;
            this.worldConditionalAction = worldConditionalAction;
            this.actionGroupId = id;
        }

        public PushedAction(Entity entity, WorldConditionalAction worldConditionalAction){
            this.entity = entity;
            this.worldConditionalAction = worldConditionalAction;
        }


        public void performAction(){
            isActionPerformed = true;
            worldConditionalAction.performAction(world, entity);
        }

        public void setActionGroupId(String actionGroupId) {
            this.actionGroupId = actionGroupId;
        }

        public boolean isActionPerformed() {
            return isActionPerformed;
        }

        public boolean isCompleted() {
            return worldConditionalAction.condition(world, entity);
        }

    }



    public void createDamageApplicationAction(Entity entity, final int damage){

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


    public void createDamageApplicationAction(String skillId, Entity entity, final int damage){

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
        }, skillId);
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



    public void createUpdateIntentAction(Entity entity){

        pushLastAction(entity, new WorldConditionalAction() {
            @Override
            public boolean condition(World world, Entity entity) {
                return true;
            }

            @Override
            public void performAction(World world, Entity entity) {
                world.getSystem(DisplayEnemyIntentUISystem.class).updateIntent();
            }
        });
    }


/*    protected boolean checkProcessing() {
        processingFlag = actionQueue.size != 0;
        return processingFlag;
    }*/
}
