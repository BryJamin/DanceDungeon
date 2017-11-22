package com.bryjamin.dancedungeon.factories.player.spells;

import com.artemis.Entity;
import com.artemis.World;

/**
 * Created by BB on 19/11/2017.
 */

public class FrostBallDescription extends CooldownSpellDescription {

    public FrostBallDescription(){
        skill = new com.bryjamin.dancedungeon.factories.player.spells.animations.FrostBall();
    }

    @Override
    public void createTargeting(World world, final Entity player) {
       new TargetingFactory().createTargetTiles(world, player, this, 3);
    }


    @Override
    public String getIcon() {
        return "skills/Frost";
    }


}
