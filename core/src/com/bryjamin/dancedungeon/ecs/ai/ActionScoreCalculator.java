package com.bryjamin.dancedungeon.ecs.ai;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.ai.calculations.ActionScoreCalculation;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;

/**
 * Created by BB on 03/11/2017.
 */

public class ActionScoreCalculator {

    public ActionScoreCalculator(WorldAction action, com.bryjamin.dancedungeon.ecs.ai.calculations.ActionScoreCalculation... actionScoreCalculations){
        this.worldAction = action;
        this.actionScoreCalculations.addAll(actionScoreCalculations);
    };

    private Float score = 0f;
    private WorldAction worldAction;
    private Array<com.bryjamin.dancedungeon.ecs.ai.calculations.ActionScoreCalculation> actionScoreCalculations = new Array<com.bryjamin.dancedungeon.ecs.ai.calculations.ActionScoreCalculation>();


    public void calculateScore(World world, Entity entity){

        Float score = 0f;

        for(ActionScoreCalculation actionScoreCalculation: actionScoreCalculations){

            Float f = actionScoreCalculation.calculateScore(world, entity);

            if(f == null){
                score = null;
                break;
            }

            score += actionScoreCalculation.calculateScore(world, entity);
        }

        this.score = score;

    }

    public Float getScore() {
        return score;
    }

    public WorldAction getWorldAction() {
        return worldAction;
    }

    public Array<com.bryjamin.dancedungeon.ecs.ai.calculations.ActionScoreCalculation> getActionScoreCalculations() {
        return actionScoreCalculations;
    }
}
