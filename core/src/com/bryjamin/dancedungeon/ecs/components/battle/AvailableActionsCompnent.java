package com.bryjamin.dancedungeon.ecs.components.battle;

import com.artemis.Component;


/**
 * Created by BB on 21/10/2017.
 *
 * Used To Track if an Entity has Movement or Attack actions.
 */

public class AvailableActionsCompnent extends Component {

    public boolean movementActionAvailable = true;
    public boolean attackActionAvailable = true;

    public AvailableActionsCompnent(){}

    public void reset(){
        movementActionAvailable = true;
        attackActionAvailable = true;
    }

    public boolean hasActions() {
        return this.movementActionAvailable || this.attackActionAvailable;
    }




}
