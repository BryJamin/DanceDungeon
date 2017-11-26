package com.bryjamin.dancedungeon.ecs.ai.calculations;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.utils.math.CoordinateMath;
import com.bryjamin.dancedungeon.utils.math.CoordinateSorter;

/**
 * Created by BB on 15/11/2017.
 */

public class IsInRangeCalculator implements ActionScoreCalculation {

    private float isInRangeScore;
    private float isNotInRangeScore;
    private int range;

    public IsInRangeCalculator(float isInRangeScore, float isNotInRangeScore, int range){
        this.isInRangeScore = isInRangeScore;
        this.isNotInRangeScore = isNotInRangeScore;
        this.range = range;
    }


    @Override
    public float calculateScore(World world, Entity entity) {

        Array<Entity> entityArray = entity.getComponent(TargetComponent.class).getTargets(world);
        if(entityArray.size <= 0) return isNotInRangeScore;
        entityArray.sort(CoordinateSorter.SORT_BY_NEAREST(entity));

        CoordinateComponent coordinateComponent = entity.getComponent(CoordinateComponent.class);
        CoordinateComponent playerCoordinateComponent = entityArray.first().getComponent(CoordinateComponent.class);

        if (CoordinateMath.isWithinRange(coordinateComponent.coordinates, playerCoordinateComponent.coordinates, range)) {
            return isInRangeScore;
        } else {
            return isNotInRangeScore;
        }
    }
}
