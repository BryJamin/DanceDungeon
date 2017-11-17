package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.BaseSystem;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.ecs.components.BoundComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MovementRangeComponent;
import com.bryjamin.dancedungeon.ecs.systems.FindPlayerSystem;
import com.bryjamin.dancedungeon.utils.math.CoordinateMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 02/11/2017.
 */

public class MovementAiSystem extends BaseSystem {

    public void calculatePath(CoordinateComponent coordinateComponent, MovementRangeComponent movementRangeComponent, MoveToComponent moveToComponent, BoundComponent boundComponent){

        TileSystem tileSystem = world.getSystem(TileSystem.class);

        Coordinates playerCoordinates = world.getSystem(FindPlayerSystem.class).getPlayerComponent(CoordinateComponent.class).coordinates;

        Queue<Coordinates> coordinatesQueue = new Queue<Coordinates>();

        tileSystem.findShortestPath(coordinatesQueue, coordinateComponent.coordinates, CoordinateMath.getCoordinatesInMovementRange(playerCoordinates, 1));

        while (coordinatesQueue.size > movementRangeComponent.range) {
            coordinatesQueue.removeLast();
        }

        for(Coordinates c : coordinatesQueue){
            moveToComponent.movementPositions.add(
                    tileSystem.getPositionUsingCoordinates(c, boundComponent.bound));
        }

    }



    @Override
    protected void processSystem() {

    }
}
