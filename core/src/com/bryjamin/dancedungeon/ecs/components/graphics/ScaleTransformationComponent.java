package com.bryjamin.dancedungeon.ecs.components.graphics;

import com.artemis.Component;

/**
 * Created by BB on 21/01/2018.
 */

public class ScaleTransformationComponent extends Component {

    public float maxScaleX;
    public float maxScaleY;

    public float minScaleX = 1;
    public float minScaleY = 1;

    public float currentScaleX;
    public float currentScaleY;

    public float duration = 1f;

    public float time;

    public ScaleTransformationComponent(){}

    public ScaleTransformationComponent(float scaleXY){
        this.maxScaleX = scaleXY;
        this.maxScaleY = scaleXY;
    }

    public ScaleTransformationComponent(float maxScaleX, float maxScaleY){
        this.maxScaleX = maxScaleX;
        this.maxScaleY = maxScaleY;
    }

    public ScaleTransformationComponent(float duration, float maxScaleX, float maxScaleY){
        this.duration = duration;
        this.maxScaleX = maxScaleX;
        this.maxScaleY = maxScaleY;
    }


}
