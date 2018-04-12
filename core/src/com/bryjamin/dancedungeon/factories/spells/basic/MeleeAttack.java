package com.bryjamin.dancedungeon.factories.spells.basic;

import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.factories.spells.Skill;

/**
 * Created by BB on 20/12/2017.
 *
 * This Melee Damage
 *
 * Used as a basic attack for enemies that do not have a ranged weapon.
 *
 * This attack deals damage based on the power of the entity using it
 *
 */

public class MeleeAttack extends Skill {

    public MeleeAttack() {
        super(new Builder()
                .name("Damage")
                .icon(TextureStrings.SKILLS_SLASH)
                .targeting(Targeting.Melee)
                .spellAnimation(SpellAnimation.Slash)
                .attackType(AttackType.Damage)
                .attack(Attack.Melee));
    }
}
