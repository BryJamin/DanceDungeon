package com.bryjamin.dancedungeon.ecs.systems.graphical;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.bryjamin.dancedungeon.ecs.components.BoundComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;

/**
 * Created by BB on 15/10/2017.
 */

public class UpdatePositionSystem extends EntityProcessingSystem {

    ComponentMapper<PositionComponent> pm;
    ComponentMapper<HitBoxComponent> hitboxComponentM;
    ComponentMapper<BoundComponent> boundComponentM;

    @SuppressWarnings("unchecked")
    public UpdatePositionSystem() {
        super(Aspect.all(PositionComponent.class));
    }

    @Override
    protected void process(Entity e) {

        PositionComponent pc = pm.get(e);
        if(hitboxComponentM.has(e)) hitboxComponentM.get(e).update(pc);
        if(boundComponentM.has(e)) {
            boundComponentM.get(e).bound.x = pc.getX();
            boundComponentM.get(e).bound.x = pc.getY();
        }

    }

}


