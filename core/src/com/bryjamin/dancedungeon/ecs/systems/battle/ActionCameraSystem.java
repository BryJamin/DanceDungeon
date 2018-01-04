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
import com.bryjamin.dancedungeon.utils.Pair;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 20/12/2017.
 * <p>
 * Is currently nothing to do with an action camera, but watches for actions to be completed
 * before allowing the next turn to be activated
 */

public class ActionCameraSystem extends EntitySystem {


    private Queue<Pair<Entity, WorldConditionalAction>> actionQueue = new Queue<Pair<Entity, WorldConditionalAction>>();

    private WorldConditionalAction currentConditionalAction;

    private boolean hasBegun = false;

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

        if(!this.getEntities().isEmpty()) return;

        if (currentConditionalAction == null) {
            actionQueue.first().getRight().performAction(world,
                    actionQueue.first().getLeft());

            currentConditionalAction = actionQueue.first().getRight();

            if (!hasBegun) {
                hasBegun = true;
                world.getSystem(SelectedTargetSystem.class).clearTargeting();
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
    }


    public void pushLastAction(Entity e, WorldConditionalAction wca) {
        actionQueue.addLast(new Pair<Entity, WorldConditionalAction>(e, wca));
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


    @Override
    protected boolean checkProcessing() {
        return actionQueue.size != 0 || !this.getEntities().isEmpty();
    }
}
