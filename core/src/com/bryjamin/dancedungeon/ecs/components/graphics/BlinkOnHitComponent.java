package com.bryjamin.dancedungeon.ecs.components.graphics;

import com.artemis.Component;

/**
 * Created by BB on 17/10/2017.
 */

public class BlinkOnHitComponent extends Component {

    public boolean isHit;

    public float flashTimer;
    public float maxFlashTimer = 0.2f;

    public BlinkOnHitComponent(){}


    public void reset(){
        isHit = false;
        flashTimer = 0;
    }

}
