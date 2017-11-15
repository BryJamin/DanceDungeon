package com.bryjamin.dancedungeon.ecs.ai.calculations;

import com.artemis.Entity;
import com.artemis.World;
import com.bryjamin.dancedungeon.ecs.ai.ActionScoreCalculation;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.systems.FindPlayerSystem;
import com.bryjamin.dancedungeon.utils.math.CoordinateMath;

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
        CoordinateComponent coordinateComponent = entity.getComponent(CoordinateComponent.class);
        CoordinateComponent playerCoordinateComponent = world.getSystem(FindPlayerSystem.class).getPlayerComponent(CoordinateComponent.class);

        if (CoordinateMath.isWithinRange(coordinateComponent.coordinates, playerCoordinateComponent.coordinates, range)) {
            return isInRangeScore;
        } else {
            return isNotInRangeScore;
        }
    }
}
