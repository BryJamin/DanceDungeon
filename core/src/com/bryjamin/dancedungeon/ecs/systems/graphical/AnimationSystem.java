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
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationMapComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationStateComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.KillOnAnimationEndComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeadComponent;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 27/12/2017.
 */

public class AnimationSystem extends EntityProcessingSystem {


    private TextureAtlas atlas;

    ComponentMapper<AnimationStateComponent> am;
    ComponentMapper<AnimationMapComponent> aMapm;
    ComponentMapper<DrawableComponent> drawm;

    ComponentMapper<KillOnAnimationEndComponent> killOnAnimationEndM; //TODO needs testing as this is pretty unclean, as does the whole drawable solution


    public AnimationSystem(MainGame game) {
        super(Aspect.all(AnimationStateComponent.class, AnimationMapComponent.class, DrawableComponent.class));
        this.atlas = game.assetManager.get(FileStrings.SPRITE_ATLAS_FILE, TextureAtlas.class);
    }


    @Override
    public void inserted(Entity e) {

        AnimationMapComponent animationMapComponent = e.getComponent(AnimationMapComponent.class);

        for (int i : animationMapComponent.animations.keys().toArray().toArray()) {

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

        AnimationStateComponent.AnimationState sc = asc.animationState;
        sc.stateTime += world.delta;

        int state;

        if (sc.getStateQueue().size != 0) {

            state = sc.getStateQueue().first(); //Gets the first animation queued.
            boolean canRemove = true;

            if (ac.animations.containsKey(state)) { //Checks if the state exists in animations and if animation is complete
                canRemove = (ac.animations.get(state).getAnimation().isAnimationFinished(sc.stateTime)) && state == sc.getCurrentState();
            }

            if (canRemove) {//Removes queued animation and returns Entity to default animation
                state = sc.getDefaultState();
                sc.getStateQueue().removeFirst();
            }

        } else { //No animations queued, return to default animation.
            state = sc.getDefaultState();
        }

        if (state != sc.getCurrentState()) {
            sc.setCurrentState(state);
        }

        if (ac.animations.containsKey(sc.getCurrentState())) { //Checks current animation is in the animation map and then edits Texture Description
            TextureDescription td = (TextureDescription) dc.drawables;
            td.setRegion(ac.animations.get(sc.getCurrentState()).getAnimationRegion());
            td.setIndex(ac.animations.get(sc.getCurrentState()).getAnimation().getKeyFrameIndex(sc.stateTime));
        }


        //If the animation is finished and it has this component kill the Entity
        if (killOnAnimationEndM.has(e)) {
            if (killOnAnimationEndM.get(e).animationId == sc.getCurrentState() && ac.animations.get(sc.getCurrentState()).getAnimation().isAnimationFinished(sc.stateTime))
                e.edit().add(new DeadComponent());
        }


    }
}
