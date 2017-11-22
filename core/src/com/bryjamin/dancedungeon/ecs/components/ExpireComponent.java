package com.bryjamin.dancedungeon.ecs.components;

import com.artemis.Component;

/**
 * Created by BB on 15/10/2017.
 *
 * Component used to determine how long an entity exists before it removes itself
 * from the world
 *
 */

public class ExpireComponent extends Component {

    public float expiryTime;

    public ExpireComponent(){
        this(0);
    }

    public ExpireComponent(float expiryTime){
        this.expiryTime = expiryTime;
    }


}
