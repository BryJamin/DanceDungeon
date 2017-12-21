package com.bryjamin.dancedungeon.ecs.ai;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;

import java.util.Comparator;

/**
 * Created by BB on 04/11/2017.
 */

public class UtilityAiCalculator {


    private Array<ActionScoreCalculator> actionCalculators = new Array<ActionScoreCalculator>();

    public UtilityAiCalculator(ActionScoreCalculator... actionScoreCalculators){
        this.actionCalculators.addAll(actionScoreCalculators);
    }


    /**
     * Returns the action with the highest calculated score
     */
    public WorldAction getAction(World world, Entity entity){

        for(ActionScoreCalculator actionScoreCalculator : actionCalculators){
            actionScoreCalculator.calculateScore(world, entity);

        }

        actionCalculators.sort(new Comparator<ActionScoreCalculator>() {
            @Override
            public int compare(ActionScoreCalculator ac1, ActionScoreCalculator ac2) {
                return ac1.getScore() > ac2.getScore() ? -1 : ac1.getScore() == ac2.getScore() ? 0 : 1;
            }
        });

        return actionCalculators.first().getWorldAction();

    }

    public void performAction(World world, Entity entity){
        getAction(world, entity).performAction(world, entity);
    }








}
