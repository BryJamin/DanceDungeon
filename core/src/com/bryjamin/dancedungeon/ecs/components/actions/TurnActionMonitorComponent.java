package com.bryjamin.dancedungeon.ecs.components.actions;

import com.artemis.Component;

/**
 * Created by BB on 19/12/2017.
 */

public class TurnActionMonitorComponent extends Component {

    public boolean movementActionAvailable = true;
    public boolean attackActionAvailable = true;

    public TurnActionMonitorComponent(){};



    public void reset(){
        movementActionAvailable = true;
        attackActionAvailable = true;
    }

    public boolean hasActions(){
        return movementActionAvailable || attackActionAvailable;
    }
}
