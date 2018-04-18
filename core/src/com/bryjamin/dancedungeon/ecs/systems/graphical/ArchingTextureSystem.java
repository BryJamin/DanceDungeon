package com.bryjamin.dancedungeon.ecs.systems.graphical;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.ArchingTextureComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;

/**
 * Used for creating an 'arching' effect for some spell animations.
 *
 * This system requires the spell cast to be fired in a straight line.
 */
public class ArchingTextureSystem extends EntityProcessingSystem {

    ComponentMapper<PositionComponent> pm;
    ComponentMapper<ArchingTextureComponent> archMapper;
    ComponentMapper<DrawableComponent> drawm;

    public ArchingTextureSystem() {
        super(Aspect.all(PositionComponent.class, ArchingTextureComponent.class, DrawableComponent.class));
    }

    @Override
    protected void process(Entity e) {

        ArchingTextureComponent atc = archMapper.get(e);
        PositionComponent pc = pm.get(e);

        float fullDistance = atc.startPos.dst(atc.endPos);
        float currentDistance = atc.startPos.dst(pc.position);

        float midPoint = fullDistance / 2;

        float scaleX;
        float scaleY;

        if(currentDistance < midPoint){
            float ratio = (currentDistance / midPoint);
            scaleX = atc.minScaleX + ((atc.maxScaleX - atc.minScaleX) * ratio);
            scaleY = atc.mixScaleY + ((atc.maxScaleY - atc.mixScaleY) *ratio);
        } else {
            float ratio = 1 - ((currentDistance - midPoint) / midPoint);
            scaleX = atc.minScaleX + ((atc.maxScaleX - atc.minScaleX) * ratio);
            scaleY = atc.mixScaleY + ((atc.maxScaleY - atc.mixScaleY) * ratio);
        }


        DrawableComponent dc = drawm.get(e);
        dc.drawables.setScaleX(scaleX);
        dc.drawables.setScaleY(scaleY);

    }
}
