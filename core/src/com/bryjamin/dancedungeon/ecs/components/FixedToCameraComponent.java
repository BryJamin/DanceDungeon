package com.bryjamin.dancedungeon.ecs.components;

import com.artemis.Component;

/**
 * Created by BB on 20/01/2018.
 *
 * Component for Entities that are fixed to the camera.
 */

public class FixedToCameraComponent extends Component {

    public float offsetX;
    public float offsetY;

    public FixedToCameraComponent(){};

    public FixedToCameraComponent(float offsetX, float offsetY){
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }


}
