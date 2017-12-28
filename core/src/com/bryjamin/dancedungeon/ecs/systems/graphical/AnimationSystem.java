package com.bryjamin.dancedungeon.ecs.systems.graphical;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationMapComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.FadeComponent;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 27/12/2017.
 */

public class AnimationSystem extends EntityProcessingSystem{


    ComponentMapper<AnimationComponent> am;
    ComponentMapper<AnimationMapComponent> aMapm;
    ComponentMapper<DrawableComponent> drawm;


    public AnimationSystem() {
        super(Aspect.all(AnimationComponent.class, AnimationMapComponent.class, DrawableComponent.class));
    }

    @Override
    protected void process(Entity e) {
        AnimationMapComponent ac = aMapm.get(e);
        AnimationComponent sc = am.get(e);
        DrawableComponent trc = drawm.get(e);

        sc.stateTime += world.delta;

        int state;

        if(sc.getStateQueue().size != 0) {

            state = sc.getStateQueue().first();
            boolean canRemove = true;

            if(ac.animations.containsKey(state)){
                canRemove = (ac.animations.get(state).isAnimationFinished(sc.stateTime)) && state == sc.getCurrentState();
            }

            if(canRemove)  {
                state = sc.getDefaultState();
                sc.getStateQueue().removeFirst();
            }

        } else {
            state = sc.getDefaultState();
        }

        if(state != sc.getCurrentState()) {
            sc.setCurrentState(state);
        }

        if(ac.animations.containsKey(sc.getCurrentState())){

            TextureDescription td = (TextureDescription) drawm.get(e).drawables.first();
            td.(ac.animations.get(sc.getCurrentState()).getKeyFrame(sc.stateTime).);

            gettrc.region = ac.animations.get(sc.getCurrentState()).getKeyFrame(sc.stateTime);
        }
    }
}
