package com.bryjamin.dancedungeon.factories.spells;

import com.bryjamin.dancedungeon.factories.spells.animations.FrostBall;

/**
 * Created by BB on 19/11/2017.
 */

public class FrostBallDescription extends CooldownSpellDescription {

    public FrostBallDescription(){
        super(new Builder()
                .name("FrostBall")
                .icon("skills/Frost")
                .attack(Attack.Ranged));
        skillAnimation = new FrostBall();
    }

}
