package com.bryjamin.dancedungeon.ecs.components.graphics;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.IntMap;

/**
 * Created by BB on 27/12/2017.
 */

public class AnimationMapComponent extends Component {

    public IntMap<AnimationSettings> animations = new IntMap<AnimationSettings>();

    public AnimationMapComponent put(int animationId, String animationKey, float duration, Animation.PlayMode playMode){
        animations.put(animationId, new AnimationSettings(animationKey, duration, playMode));
        return this;
    }


    public class AnimationSettings {

        private String animationRegion;
        private float duration;
        private Animation.PlayMode playMode;
        private Animation<TextureRegion> animation;


        public AnimationSettings(String animationRegion, float duration, Animation.PlayMode playMode){
            this.animationRegion = animationRegion;
            this.duration = duration;
            this.playMode = playMode;
        }

        public Animation<TextureRegion> getAnimation() {
            return animation;
        }

        public void setAnimation(Animation<TextureRegion> animation) {
            this.animation = animation;
        }

        public String getAnimationRegion() {
            return animationRegion;
        }

        public float getDuration() {
            return duration;
        }

        public Animation.PlayMode getPlayMode() {
            return playMode;
        }
    }


}
