package com.bryjamin.dancedungeon.ecs.components.graphics;

import com.artemis.Component;

/**
 * Created by BB on 17/10/2017.
 *
 * Graphicaly Component.
 *
 * When attached if an entity is hit, the RenderingSystem will shade the Entity's Drawable pure white.
 *
 * For a brief currentDuration.
 *
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
