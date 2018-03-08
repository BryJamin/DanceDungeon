package com.bryjamin.dancedungeon.factories.spells.basic;

import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.factories.spells.Skill;

/**
 * Created by BB on 28/02/2018.
 */

public class HeavyStrike extends Skill{


    public HeavyStrike() {
        super(new Builder()
                .name("Heavy Strike")
                .description("Deals 1 damage. Pushes a storedTargetCoordinates 1 tile")
                .icon(TextureStrings.CLASS_WARRIOR)
                .targeting(Targeting.Melee)
                .spellAnimation(Skill.SpellAnimation.Slash)
                .spellType(SpellType.Attack)
                .push(1)
                .attack(Attack.Melee));

    }



}
