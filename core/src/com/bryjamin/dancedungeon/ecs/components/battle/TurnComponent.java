package com.bryjamin.dancedungeon.ecs.components.battle;

import com.artemis.Component;
import com.artemis.Entity;
import com.artemis.World;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.EmptyTask;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldCondition;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldTask;


/**
 * Created by BB on 21/10/2017.
 */

public class TurnComponent extends Component {


    public enum State {
        DECIDING, WAITING, END
    }

    public State state = State.DECIDING;


    public boolean isTurnOver = false;

    public WorldCondition turnOverCondition;
    public WorldTask turnAction;

    public TurnComponent(){

        turnAction = new EmptyTask();

        turnOverCondition = new WorldCondition() {
            @Override
            public boolean condition(World world, Entity entity) {
                return false;
            }
        };
    }




}
