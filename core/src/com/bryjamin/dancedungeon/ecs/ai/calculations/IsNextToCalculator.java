package com.bryjamin.dancedungeon.ecs.ai.calculations;

import com.artemis.Entity;
import com.artemis.World;
import com.bryjamin.dancedungeon.ecs.ai.ActionScoreCalculation;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.systems.FindPlayerSystem;
import com.bryjamin.dancedungeon.utils.math.CoordinateMath;

/**
 * Created by BB on 14/11/2017.
 */

public class IsNextToCalculator implements ActionScoreCalculation{

    public float isNextToScore;
    public float isNotNextToScore;

    public IsNextToCalculator(float isNextToScore, float isNotNextToScore){
        this.isNextToScore = isNextToScore;
        this.isNotNextToScore = isNotNextToScore;
    }


    @Override
    public float calculateScore(World world, Entity entity) {
        CoordinateComponent coordinateComponent = entity.getComponent(CoordinateComponent.class);
        CoordinateComponent playerCoordinateComponent = world.getSystem(FindPlayerSystem.class).getPlayerComponent(CoordinateComponent.class);

        if (CoordinateMath.isNextTo(coordinateComponent.coordinates, playerCoordinateComponent.coordinates)) {
            return isNextToScore;
        } else {
            return isNotNextToScore;
        }
    }
}
