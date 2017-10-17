package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.bryjamin.dancedungeon.ecs.components.graphics.BlinkOnHitComponent;

/**
 * Created by BB on 17/10/2017.
 *
 * System used to track how long an appears to be 'flashing' when hit.
 *
 */

public class BlinkOnHitSystem extends EntityProcessingSystem {

    private ComponentMapper<BlinkOnHitComponent> blinkonHitMapper;

    @SuppressWarnings("unchecked")
    public BlinkOnHitSystem() {
        super(Aspect.all(BlinkOnHitComponent.class));
    }

    @Override
    protected void process(Entity e) {

        BlinkOnHitComponent blinkOnHitComponent = blinkonHitMapper.get(e);

        if(blinkOnHitComponent.isHit){
            blinkOnHitComponent.flashTimer += world.delta;
            if(blinkOnHitComponent.flashTimer >= blinkOnHitComponent.maxFlashTimer) {
                blinkOnHitComponent.reset();
            }
        }

    }

}
