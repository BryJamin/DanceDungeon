package com.bryjamin.dancedungeon.ecs.systems.graphical;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;

/**
 * Created by BB on 21/01/2018.
 */

public class ScaleTransformationSystem extends EntityProcessingSystem{

    ComponentMapper<DrawableComponent> drawableMapper;

    public ScaleTransformationSystem(Aspect.Builder aspect) {
        super(Aspect.all());
    }

    @Override
    protected void process(Entity e) {





    }


    private void applyFade(Entity e, float scaleX) {

        if (drawableMapper.has(e)) {
            DrawableComponent dc = drawableMapper.get(e);
            for (DrawableDescription drawableDescription : dc.drawables) {
                //drawableDescription.getScaleX() = scaleX;
            }
        }
    }

}
