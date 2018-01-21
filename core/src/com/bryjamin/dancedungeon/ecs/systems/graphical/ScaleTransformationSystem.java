package com.bryjamin.dancedungeon.ecs.systems.graphical;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.ScaleTransformationComponent;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;

/**
 * Created by BB on 21/01/2018.
 *
 * Used for the 'Scale Effect'
 *
 */

public class ScaleTransformationSystem extends EntityProcessingSystem{

    ComponentMapper<DrawableComponent> drawableMapper;
    ComponentMapper<ScaleTransformationComponent> scaleM;

    public ScaleTransformationSystem() {
        super(Aspect.all(ScaleTransformationComponent.class));
    }

    @Override
    protected void process(Entity e) {
        ScaleTransformationComponent stc = scaleM.get(e);
        stc.time += world.delta;
        stc.currentScaleX = calcSinePos(stc.duration, stc.time, stc.minScaleX, stc.maxScaleX);
        stc.currentScaleY = calcSinePos(stc.duration, stc.time, stc.minScaleY, stc.maxScaleY);
        //System.out.println(stc.currentScaleX);

        applyScale(e, stc.currentScaleX, stc.currentScaleY);
    }


    private float calcSinePos(float duration, float time, float min, float max){

        //Find out size of 1 degree in terms of duration
        float anglePct = duration / 360;

        //Convert time into Angle
        float angle = time / anglePct;

        //Calculate position on Sine Graph Using Math Formula
        float average = (max + min) / 2;
        float amplitude = (max - min) / 2;

        return average + (amplitude * MathUtils.sinDeg(angle));

    }


    private void applyScale(Entity e, float scaleX, float scaleY) {

        if (drawableMapper.has(e)) {
            DrawableComponent dc = drawableMapper.get(e);
            for (DrawableDescription drawableDescription : dc.drawables) {
                drawableDescription.setScaleX(scaleX);
                drawableDescription.setScaleY(scaleY);
            }
        }
    }


    @Override
    public void removed(Entity e) {
        applyScale(e, 1, 1);
    }
}
