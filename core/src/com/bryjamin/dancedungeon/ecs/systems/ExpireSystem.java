package com.bryjamin.dancedungeon.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.bryjamin.dancedungeon.ecs.components.ExpireComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeadComponent;

/**
 * Created by BB on 15/10/2017.
 *
 * Keeps tracks of when an entity should expire.
 */

public class ExpireSystem extends EntityProcessingSystem {

    ComponentMapper<ExpireComponent> expirem;

    @SuppressWarnings("unchecked")
    public ExpireSystem() {
        super(Aspect.all(ExpireComponent.class));
    }

    @Override
    protected void process(Entity e) {
        ExpireComponent ec = expirem.get(e);
        if ((ec.expiryTime -= world.delta) <= 0) e.edit().add(new DeadComponent());
    }

}
