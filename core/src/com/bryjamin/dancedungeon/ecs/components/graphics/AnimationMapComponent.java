package com.bryjamin.dancedungeon.ecs.components.graphics;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.IntMap;

/**
 * Created by BB on 27/12/2017.
 */

public class AnimationMapComponent extends Component {

    public IntMap<Animation<TextureRegion>> animations = new IntMap<Animation<TextureRegion>>();

    public AnimationMapComponent(){
        animations = new IntMap<Animation<TextureRegion>>();
    }

    public AnimationMapComponent(IntMap<Animation<TextureRegion>> animations) {
        this.animations = animations;
    }

    public AnimationMapComponent put(int animationId, Animation<TextureRegion> animation){
        animations.put(animationId, animation);
        return this;
    }


}
