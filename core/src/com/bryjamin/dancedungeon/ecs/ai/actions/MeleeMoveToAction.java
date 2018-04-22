package com.bryjamin.dancedungeon.ecs.ai.actions;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionQueueSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.utils.math.CoordinateMath;
import com.bryjamin.dancedungeon.utils.math.CoordinateSorter;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 04/11/2017.
 */

public class MeleeMoveToAction implements WorldAction {


    @Override
    public void performAction(World world, Entity entity) {

        if(!entity.getComponent(TurnComponent.class).movementActionAvailable) return;

        Array<Entity> entityArray = entity.getComponent(TargetComponent.class).getTargets(world);
        if(entityArray.size <= 0) return;
        entityArray.sort(CoordinateSorter.SORT_BY_NEAREST(entity));



        TileSystem tileSystem = world.getSystem(TileSystem.class);
        Coordinates playerCoordinates = entityArray.first().getComponent(CoordinateComponent.class).coordinates;
        Queue<Coordinates> coordinatesQueue = new Queue<Coordinates>();
        tileSystem.findShortestPath(entity, coordinatesQueue, CoordinateMath.getCoordinatesInLine(playerCoordinates, 1),
                entity.getComponent(StatComponent.class).movementRange);

        world.getSystem(ActionQueueSystem.class).createMovementAction(entity, coordinatesQueue);
        entity.getComponent(TurnComponent.class).movementActionAvailable = false;

    }
}
