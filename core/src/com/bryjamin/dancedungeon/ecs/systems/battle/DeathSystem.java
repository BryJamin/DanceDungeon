package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeadComponent;

/**
 * Created by BB on 15/10/2017.
 */

public class DeathSystem extends EntityProcessingSystem {


    @SuppressWarnings("unchecked")
    public DeathSystem() {
        super(Aspect.all(DeadComponent.class));
    }

    @Override
    protected void process(Entity e) {
        e.deleteFromWorld();
    }

}
