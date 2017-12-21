package com.bryjamin.dancedungeon.ecs.ai.calculations;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.utils.math.CoordinateMath;
import com.bryjamin.dancedungeon.utils.math.CoordinateSorter;

/**
 * Created by BB on 14/11/2017.
 */

public class IsNextToCalculator implements ActionScoreCalculation{

    public Float isNextToScore;
    public Float isNotNextToScore;

    public IsNextToCalculator(Float isNextToScore, Float isNotNextToScore){
        this.isNextToScore = isNextToScore;
        this.isNotNextToScore = isNotNextToScore;
    }


    @Override
    public Float calculateScore(World world, Entity entity) {
        Array<Entity> entityArray = entity.getComponent(TargetComponent.class).getTargets(world);
        if(entityArray.size <= 0) return isNotNextToScore;
        entityArray.sort(CoordinateSorter.SORT_BY_NEAREST(entity));

        CoordinateComponent coordinateComponent = entity.getComponent(CoordinateComponent.class);
        CoordinateComponent playerCoordinateComponent = entityArray.first().getComponent(CoordinateComponent.class);

        if (CoordinateMath.isNextTo(coordinateComponent.coordinates, playerCoordinateComponent.coordinates)) {
            return isNextToScore;
        } else {
            return isNotNextToScore;
        }
    }
}
