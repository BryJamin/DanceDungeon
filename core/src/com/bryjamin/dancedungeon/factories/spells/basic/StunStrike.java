package com.bryjamin.dancedungeon.factories.spells.basic;

import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.factories.spells.Skill;

/**
 * Created by BB on 28/01/2018.
 */

public class StunStrike extends Skill {

    public StunStrike() {
        super(new Builder()
                .name("Stun Strike")
                .icon(TextureStrings.SKILLS_SLASH)
                .targeting(Targeting.FreeAim)
                .spellAnimation(Skill.SpellAnimation.Slash)
                .spellType(Skill.SpellType.PhysicalAttack)
                .spellEffects(SpellEffect.Stun)
                .attack(Attack.Ranged));

    }

}
