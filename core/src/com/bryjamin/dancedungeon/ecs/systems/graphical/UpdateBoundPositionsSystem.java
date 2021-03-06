package com.bryjamin.dancedungeon.ecs.systems.graphical;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;

/**
 * Created by BB on 15/10/2017.
 */

public class UpdateBoundPositionsSystem extends EntityProcessingSystem {

    ComponentMapper<PositionComponent> pm;
    ComponentMapper<HitBoxComponent> hitboxComponentM;
    ComponentMapper<CenteringBoundComponent> boundComponentM;

    @SuppressWarnings("unchecked")
    public UpdateBoundPositionsSystem() {
        super(Aspect.all(PositionComponent.class));
    }

    @Override
    public void inserted(Entity e) {
        process(e);
    }

    @Override
    protected void process(Entity e) {

        PositionComponent pc = pm.get(e);
        if(hitboxComponentM.has(e)) hitboxComponentM.get(e).update(pc);
        if(boundComponentM.has(e)) {
            boundComponentM.get(e).bound.x = pc.getX();
            boundComponentM.get(e).bound.y = pc.getY();
        }

    }

}


