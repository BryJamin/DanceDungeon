package com.bryjamin.dancedungeon.ecs.systems.graphical;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.ChangeScaleUsingDistanceComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;

/**
 * Used for creating an 'arching' effect for some spell animations.
 *
 * This system requires the spell cast to be fired in a straight line.
 *
 * Used mainly on Aerial skills
 */
public class ArchingTextureSystem extends EntityProcessingSystem {

    ComponentMapper<PositionComponent> pm;
    ComponentMapper<ChangeScaleUsingDistanceComponent> archMapper;
    ComponentMapper<DrawableComponent> drawm;

    public ArchingTextureSystem() {
        super(Aspect.all(PositionComponent.class, ChangeScaleUsingDistanceComponent.class, DrawableComponent.class));
    }

    @Override
    protected void process(Entity e) {

        ChangeScaleUsingDistanceComponent atc = archMapper.get(e);
        PositionComponent pc = pm.get(e);

        float fullDistance = atc.startPos.dst(atc.endPos);
        float currentDistance = atc.startPos.dst(pc.position);

        float midPoint = fullDistance / 2;

        float scaleX;
        float scaleY;


        if(currentDistance < midPoint){//From start to mid-point increase to max scale.
            float ratio = (currentDistance / midPoint);
            scaleX = atc.minScaleX + ((atc.maxScaleX - atc.minScaleX) * ratio);
            scaleY = atc.mixScaleY + ((atc.maxScaleY - atc.mixScaleY) *ratio);
        } else {//From mid-point to destination decrease to minimum scale
            float ratio = 1 - ((currentDistance - midPoint) / midPoint);
            scaleX = atc.minScaleX + ((atc.maxScaleX - atc.minScaleX) * ratio);
            scaleY = atc.mixScaleY + ((atc.maxScaleY - atc.mixScaleY) * ratio);
        }


        DrawableComponent dc = drawm.get(e);
        dc.drawables.setScaleX(scaleX);
        dc.drawables.setScaleY(scaleY);

    }
}
