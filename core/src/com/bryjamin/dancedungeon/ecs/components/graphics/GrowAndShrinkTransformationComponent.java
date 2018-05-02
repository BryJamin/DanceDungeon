package com.bryjamin.dancedungeon.ecs.components.graphics;

import com.artemis.Component;

/**
 * Created by BB on 21/01/2018.
 *
 * Used to create a repeating 'Grow' and 'Shrink' effect on an Entity's drawable.
 */

public class GrowAndShrinkTransformationComponent extends Component {

    public float maxScaleX;
    public float maxScaleY;

    public float minScaleX = 1;
    public float minScaleY = 1;

    public float currentScaleX;
    public float currentScaleY;

    public float duration = 1f;

    public float time;

    public GrowAndShrinkTransformationComponent(){}

    public GrowAndShrinkTransformationComponent(float scaleXY){
        this.maxScaleX = scaleXY;
        this.maxScaleY = scaleXY;
    }

    public GrowAndShrinkTransformationComponent(float maxScaleX, float maxScaleY){
        this.maxScaleX = maxScaleX;
        this.maxScaleY = maxScaleY;
    }

    public GrowAndShrinkTransformationComponent(float duration, float maxScaleX, float maxScaleY){
        this.duration = duration;
        this.maxScaleX = maxScaleX;
        this.maxScaleY = maxScaleY;
    }


}
