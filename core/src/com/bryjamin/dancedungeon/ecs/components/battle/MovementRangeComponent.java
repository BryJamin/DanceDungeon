package com.bryjamin.dancedungeon.ecs.components.battle;

import com.artemis.Component;

/**
 * Created by BB on 28/10/2017.
 */

public class MovementRangeComponent extends Component {

    public int range;

    public MovementRangeComponent(){}

    public MovementRangeComponent(int range){
        this.range = range;
    }

}
