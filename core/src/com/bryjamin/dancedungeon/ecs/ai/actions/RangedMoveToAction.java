package com.bryjamin.dancedungeon.ecs.ai.actions;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MovementRangeComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionCameraSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.factories.player.spells.SkillDescription;
import com.bryjamin.dancedungeon.utils.math.CoordinateMath;
import com.bryjamin.dancedungeon.utils.math.CoordinateSorter;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 15/11/2017.
 */

//TODO create a generic moveto action that is given a set of coordinates that are avaliable for movmement and then it moves there

public class RangedMoveToAction implements WorldAction {

    private SkillDescription movementSkill;
    private int range;

    public RangedMoveToAction(SkillDescription movementSkill, int range){
        this.movementSkill = movementSkill;
        this.range = range;
    }


    @Override
    public void performAction(World world, Entity entity) {

        if(!movementSkill.canCast(world, entity)) return;

        Array<Entity> entityArray = entity.getComponent(TargetComponent.class).getTargets(world);
        if(entityArray.size <= 0) return;
        entityArray.sort(CoordinateSorter.SORT_BY_NEAREST(entity));

        TileSystem tileSystem = world.getSystem(TileSystem.class);

        Coordinates playerCoordinates = entityArray.first().getComponent(CoordinateComponent.class).coordinates;

        Queue<Coordinates> coordinatesQueue = new Queue<Coordinates>();
        tileSystem.findShortestPath(coordinatesQueue, entity.getComponent(CoordinateComponent.class).coordinates, CoordinateMath.getCoordinatesInSquareRange(playerCoordinates, range));


        while (coordinatesQueue.size > entity.getComponent(MovementRangeComponent.class).range) {
            coordinatesQueue.removeLast();
        }

        world.getSystem(ActionCameraSystem.class).createMovementAction(entity, coordinatesQueue);
        entity.getComponent(TurnComponent.class).movementActionAvailable = false;

    }

}
