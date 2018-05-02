package com.bryjamin.dancedungeon.ecs.components.battle;

import com.artemis.Component;


/**
 * Created by BB on 21/10/2017.
 *
 * Used To Track if an Entity has Movement or Attack actions.
 */

public class AvailableActionsCompnent extends Component {

    //Used For AI With This Component
    public enum AIState {
        DECIDING, WAITING, TURN_END
    }

    public AIState aiState = AIState.DECIDING;

    public boolean movementActionAvailable = true;
    public boolean attackActionAvailable = true;

    public AvailableActionsCompnent(){}

    public void reset(){
        movementActionAvailable = true;
        attackActionAvailable = true;
        aiState = AIState.DECIDING;
    }

    public boolean hasActions() {
        return this.movementActionAvailable || this.attackActionAvailable;
    }




}
