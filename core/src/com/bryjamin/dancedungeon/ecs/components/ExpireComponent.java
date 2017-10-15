package com.bryjamin.dancedungeon.ecs.components;

import com.artemis.Component;

/**
 * Created by BB on 15/10/2017.
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
