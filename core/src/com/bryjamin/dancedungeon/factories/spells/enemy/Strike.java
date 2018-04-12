package com.bryjamin.dancedungeon.factories.spells.enemy;

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
                .attackType(AttackType.Damage)
                .attack(Attack.Melee));
    }

}
