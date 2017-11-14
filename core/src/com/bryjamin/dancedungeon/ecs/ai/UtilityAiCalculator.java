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


    private Array<ActionCalculator> actionCalculators = new Array<ActionCalculator>();

    public UtilityAiCalculator(ActionCalculator... actionCalculators){
        this.actionCalculators.addAll(actionCalculators);
    }




    public WorldAction getAction(World world, Entity entity){

        for(ActionCalculator actionCalculator : actionCalculators){
            actionCalculator.calculateScore(world, entity);
        }

        actionCalculators.sort(new Comparator<ActionCalculator>() {
            @Override
            public int compare(ActionCalculator ac1, ActionCalculator ac2) {
                return ac1.getScore() > ac2.getScore() ? -1 : ac1.getScore() == ac2.getScore() ? 0 : 1;
            }
        });

        return actionCalculators.first().getWorldAction();

    }

    public void performAction(World world, Entity entity){
        getAction(world, entity).performAction(world, entity);
    }








}
