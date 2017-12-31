package com.bryjamin.dancedungeon.ecs.systems.graphical;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.FileStrings;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationStateComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationMapComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 27/12/2017.
 */

public class AnimationSystem extends EntityProcessingSystem{


    private TextureAtlas atlas;

    ComponentMapper<AnimationStateComponent> am;
    ComponentMapper<AnimationMapComponent> aMapm;
    ComponentMapper<DrawableComponent> drawm;


    public AnimationSystem(MainGame game) {
        super(Aspect.all(AnimationStateComponent.class, AnimationMapComponent.class, DrawableComponent.class));
        this.atlas = game.assetManager.get(FileStrings.SPRITE_ATLAS_FILE, TextureAtlas.class);
    }


    @Override
    public void inserted(Entity e) {

        AnimationMapComponent animationMapComponent = e.getComponent(AnimationMapComponent.class);

        for(int i : animationMapComponent.animations.keys().toArray().toArray()){

            AnimationMapComponent.AnimationSettings as = animationMapComponent.animations.get(i);

            //Create and set up animation from Atlas Regions
            Animation<TextureRegion> animation = new Animation<TextureRegion>(1, atlas.findRegions(as.getAnimationRegion()).toArray());
            animation.setPlayMode(as.getPlayMode());

            animation.setFrameDuration(
                    as.getDuration() /
                            animation.getKeyFrames().length);

            as.setAnimation(animation);

        }


    }

    @Override
    protected void process(Entity e) {
        AnimationMapComponent ac = aMapm.get(e);
        AnimationStateComponent asc = am.get(e);
        DrawableComponent dc = drawm.get(e);

        for(AnimationStateComponent.AnimationState sc : asc.drawableIdAnimationStateMap.values().toArray()) {

            sc.stateTime += world.delta;

            int state;

            if (sc.getStateQueue().size != 0) {

                state = sc.getStateQueue().first();
                boolean canRemove = true;

                if (ac.animations.containsKey(state)) {
                    canRemove = (ac.animations.get(state).getAnimation().isAnimationFinished(sc.stateTime)) && state == sc.getCurrentState();
                }

                if (canRemove) {
                    state = sc.getDefaultState();
                    sc.getStateQueue().removeFirst();
                }

            } else {
                state = sc.getDefaultState();
            }

            if (state != sc.getCurrentState()) {
                sc.setCurrentState(state);
            }

            if (ac.animations.containsKey(sc.getCurrentState())) {
                TextureDescription td = (TextureDescription) dc.trackedDrawables.get(asc.drawableIdAnimationStateMap.findKey(sc, false, 0));
                td.setRegion(ac.animations.get(sc.getCurrentState()).getAnimationRegion());
                td.setIndex(ac.animations.get(sc.getCurrentState()).getAnimation().getKeyFrameIndex(sc.stateTime));
            }
        }
    }
}
