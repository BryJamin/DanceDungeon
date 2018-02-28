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
                .description("Deals 1 damage. Stuns a Target For 1 Round")
                .icon(TextureStrings.CLASS_WARRIOR)
                .targeting(Targeting.Enemy)
                .spellAnimation(Skill.SpellAnimation.Slash)
                .spellType(SpellType.MagicAttack)
                .spellEffects(SpellEffect.Stun.value(3))
                .attack(Attack.Melee));

    }



}
