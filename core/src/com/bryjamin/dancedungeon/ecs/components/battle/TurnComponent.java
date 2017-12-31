package com.bryjamin.dancedungeon.ecs.components.battle;

import com.artemis.Component;


/**
 * Created by BB on 21/10/2017.
 */

public class TurnComponent extends Component {


    public enum State {
        DECIDING, WAITING, END
    }

    public State state = State.DECIDING;

    public boolean movementActionAvailable = true;
    public boolean attackActionAvailable = true;


    public TurnComponent(){}

    public void reset(){
        movementActionAvailable = true;
        attackActionAvailable = true;
    }

    public boolean hasActions() {
        return this.movementActionAvailable || this.attackActionAvailable;
    }




}
