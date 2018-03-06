package com.bryjamin.dancedungeon.factories.spells.basic;

import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.factories.spells.Skill;

/**
 * Created by BB on 20/11/2017.
 */

public class Strike extends Skill {

    public Strike() {
        super(new Builder()
                .name("Strike")
                .icon(TextureStrings.SKILLS_SLASH)
                .targeting(Targeting.StraightShot)
                .spellAnimation(SpellAnimation.Slash)
                .spellType(SpellType.PhysicalAttack)
                .attack(Attack.Melee));
    }

}
