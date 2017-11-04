package com.bryjamin.dancedungeon.ecs.ai.actions;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.ecs.components.BoundComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldCondition;
import com.bryjamin.dancedungeon.ecs.components.battle.AbilityPointComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MovementRangeComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.systems.FindPlayerSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.utils.math.CoordinateMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 04/11/2017.
 */

public class MeleeMoveToAction implements WorldAction {


    @Override
    public void performAction(World world, Entity entity) {

        TileSystem tileSystem = world.getSystem(TileSystem.class);

        Coordinates playerCoordinates = world.getSystem(FindPlayerSystem.class).getPlayerComponent(CoordinateComponent.class).coordinates;

        Queue<Coordinates> coordinatesQueue = new Queue<Coordinates>();

        tileSystem.findShortestPath(coordinatesQueue, entity.getComponent(CoordinateComponent.class).coordinates, CoordinateMath.getCoordinatesInRange(playerCoordinates, 1));

        while (coordinatesQueue.size > entity.getComponent(MovementRangeComponent.class).range) {
            coordinatesQueue.removeLast();
        }

        for(Coordinates c : coordinatesQueue){
            entity.getComponent(MoveToComponent.class).movementPositions.add(
                    tileSystem.getPositionUsingCoordinates(c, entity.getComponent(BoundComponent.class).bound));
        }


        entity.getComponent(TurnComponent.class).turnOverCondition = new WorldCondition() {
            @Override
            public boolean condition(World world, Entity entity) {
                return entity.getComponent(MoveToComponent.class).isEmpty();
            }
        };

        entity.getComponent(AbilityPointComponent.class).abilityPoints -= 1;

    }
}
