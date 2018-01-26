package com.bryjamin.dancedungeon.factories.spells;

import com.bryjamin.dancedungeon.assets.TextureStrings;

/**
 * Created by BB on 20/11/2017.
 */

public class SlashDescription extends CooldownSpellDescription {

    public SlashDescription() {
        super(new Builder()
                .name("Slash")
                .icon(TextureStrings.SKILLS_SLASH)
                .targeting(Targeting.Enemy)
                .spellAnimation(SpellAnimation.Slash)
                .spellType(SpellType.PhysicalAttack)
                .attack(Attack.Melee));
    }

}
