package com.bryjamin.dancedungeon.ecs.systems.graphical;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Interpolation;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.FadeComponent;

/**
 * Created by BB on 28/10/2017.
 *
 * System used for creating a fading in an out effect on textures.
 */

public class FadeSystem extends EntityProcessingSystem {

    ComponentMapper<FadeComponent> fm;
    ComponentMapper<DrawableComponent> drawableMapper;

    @SuppressWarnings("unchecked")
    public FadeSystem() {
        super(Aspect.all(FadeComponent.class, DrawableComponent.class));
    }

    @Override
    public void inserted(Entity e) {
        applyFade(e, fm.get(e).alpha);
    }

    @Override
    protected void process(Entity e) {

        FadeComponent fc = fm.get(e);

        if (fc.flicker) { //Fancy fade. Starts at max alpha and then returns to normal fading.
            applyFade(e, fc.maxAlpha);
            fc.flicker = false;
            return;
        }

        //Get current fade position. Fade oscillates from. Fade in increases duration, fade out reduces duration.
        //Naming convention is weird.
        fc.currentDuration = fc.fadeIn ? fc.currentDuration + world.delta : fc.currentDuration - world.delta;

        //Interpolation fade for smoother fade
        fc.alpha = Interpolation.fade.apply(((fc.currentDuration / fc.maximumDuration) * (fc.maxAlpha - fc.minAlpha)) + fc.minAlpha);

        if (fc.alpha <= fc.minAlpha) {
            if (fc.isEndless || fc.count > 0) {
                fc.fadeIn = true;
                fc.alpha = fc.minAlpha;
                fc.count--;
                fc.currentDuration = fc.maximumDuration * (fc.maxAlpha - fc.minAlpha);

            } else {
                fc.alpha = fc.minAlpha;
                //fc.currentDuration = fc.maximumDuration * (fc.maxAlpha - fc.minAlpha);
            }
        } else if (fc.alpha >= fc.maxAlpha) {
            if (fc.isEndless || fc.count > 0) {
                fc.fadeIn = false;
                fc.alpha = fc.maxAlpha;
                fc.count--;
                //fc.currentDuration = fc.maximumDuration;

            } else {
                fc.alpha = fc.maxAlpha;
                //fc.currentDuration = fc.maximumDuration;
            }
        }

        applyFade(e, fc.alpha);

    }

    private void applyFade(Entity e, float alpha) {

        if (drawableMapper.has(e)) {
            drawableMapper.get(e).drawables.getColor().a = alpha;
        }
    }

}

