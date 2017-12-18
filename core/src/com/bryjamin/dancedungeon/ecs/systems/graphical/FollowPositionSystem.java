package com.bryjamin.dancedungeon.ecs.systems.graphical;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.bryjamin.dancedungeon.ecs.components.FollowPositionComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;

/**
 * Created by BB on 18/12/2017.
 */

public class FollowPositionSystem extends EntityProcessingSystem {

    ComponentMapper<PositionComponent> pm;
    ComponentMapper<FollowPositionComponent> fm;

    @SuppressWarnings("unchecked")
    public FollowPositionSystem() {
        super(Aspect.all(FollowPositionComponent.class, PositionComponent.class));
    }

    @Override
    public void inserted(Entity e) {
        process(e);
    }

    @Override
    protected void process(Entity e) {
        PositionComponent pc = pm.get(e);
        FollowPositionComponent fc = fm.get(e);
        pc.position.set(fc.trackedPosition.x + fc.offsetX, fc.trackedPosition.y + fc.offsetY, 0);
    }

}

