package com.bryjamin.dancedungeon.ecs.ai;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;

/**
 * Created by BB on 03/11/2017.
 */

public class ActionCalculator {

    //TODO a lot

    public ActionCalculator(WorldAction action, ActionScoreCalculation... actionScoreCalculations){
        this.worldAction = action;
        this.actionScoreCalculations.addAll(actionScoreCalculations);
    };

    private float score = 0;
    private WorldAction worldAction;
    private Array<ActionScoreCalculation> actionScoreCalculations = new Array<ActionScoreCalculation>();


    public void calculateScore(World world, Entity entity){

        float score = 0;

        for(ActionScoreCalculation actionScoreCalculation: actionScoreCalculations){
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

    public Array<ActionScoreCalculation> getActionScoreCalculations() {
        return actionScoreCalculations;
    }
}
