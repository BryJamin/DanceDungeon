package com.bryjamin.dancedungeon.factories.player.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;

/**
 * Created by BB on 19/11/2017.
 */

public class FrostBallDescription extends CooldownSpellDescription {

    public FrostBallDescription(){
        skill = new com.bryjamin.dancedungeon.factories.player.spells.animations.FrostBall();
    }

    @Override
    public Array<Entity> createTargeting(World world, final Entity player) {
        return new TargetingFactory().createTargetTiles(world, player, this, 3);
    }


    @Override
    public String getIcon() {
        return "skills/Frost";
    }


}
