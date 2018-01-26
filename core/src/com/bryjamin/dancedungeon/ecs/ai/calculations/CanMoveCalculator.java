package com.bryjamin.dancedungeon.ecs.ai.calculations;

import com.artemis.Entity;
import com.artemis.World;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;

/**
 * Created by BB on 26/01/2018.
 */

public class CanMoveCalculator implements ActionScoreCalculation {

    private Float canMoveScore;
    private Float cannotMoveScore;

    public CanMoveCalculator(Float canMoveScore, Float cannotMoveScore){
        this.canMoveScore = canMoveScore;
        this.cannotMoveScore = cannotMoveScore;
    }

    @Override
    public Float calculateScore(World world, Entity entity) {
        return entity.getComponent(TurnComponent.class).movementActionAvailable ? canMoveScore : cannotMoveScore;
    }


}
