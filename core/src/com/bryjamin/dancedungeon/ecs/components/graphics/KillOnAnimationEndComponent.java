package com.bryjamin.dancedungeon.ecs.components.graphics;

import com.artemis.Component;

/**
 * Created by BB on 26/01/2018.
 *
 * Component that is used To Kill An Entity Once it's animation has been completed. 
 */

public class KillOnAnimationEndComponent extends Component {

    public float animationId = 0;

    public KillOnAnimationEndComponent(){}

    public KillOnAnimationEndComponent(float animationId){
        this.animationId = animationId;
    }

}
