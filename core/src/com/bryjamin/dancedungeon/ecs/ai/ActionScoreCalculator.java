package com.bryjamin.dancedungeon.ecs.ai;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;

/**
 * Created by BB on 03/11/2017.
 */

public class ActionScoreCalculator {

    public ActionScoreCalculator(WorldAction action, com.bryjamin.dancedungeon.ecs.ai.calculations.ActionScoreCalculation... actionScoreCalculations){
        this.worldAction = action;
        this.actionScoreCalculations.addAll(actionScoreCalculations);
    };

    private float score = 0;
    private WorldAction worldAction;
    private Array<com.bryjamin.dancedungeon.ecs.ai.calculations.ActionScoreCalculation> actionScoreCalculations = new Array<com.bryjamin.dancedungeon.ecs.ai.calculations.ActionScoreCalculation>();


    public void calculateScore(World world, Entity entity){

        float score = 0;

        for(com.bryjamin.dancedungeon.ecs.ai.calculations.ActionScoreCalculation actionScoreCalculation: actionScoreCalculations){
            score += actionScoreCalculation.calculateScore(world, entity);
        }

        this.score = score;

    }

    public float getScore() {
        return score;
    }

    public WorldAction getWorldAction() {
        return worldAction;
    }

    public Array<com.bryjamin.dancedungeon.ecs.ai.calculations.ActionScoreCalculation> getActionScoreCalculations() {
        return actionScoreCalculations;
    }
}
