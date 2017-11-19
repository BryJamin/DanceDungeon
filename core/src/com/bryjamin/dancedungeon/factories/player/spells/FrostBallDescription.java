package com.bryjamin.dancedungeon.factories.player.spells;

import com.artemis.Entity;
import com.artemis.World;

/**
 * Created by BB on 19/11/2017.
 */

public class FrostBallDescription extends SkillDescription {

    public FrostBallDescription(){
        spell = new FrostBall();
    }

    @Override
    public void createTargeting(World world, Entity entity) {
        new TargetingFactory().createTargetTile(world, entity, spell, 3);
    }


}
