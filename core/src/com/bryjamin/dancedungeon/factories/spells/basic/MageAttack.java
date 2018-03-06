package com.bryjamin.dancedungeon.factories.spells.basic;

import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.factories.spells.Skill;

/**
 * Created by BB on 24/12/2017.
 */

public class MageAttack extends Skill {


    public MageAttack() {
        super(new Builder()
                .name("Mage Basic")
                .icon(TextureStrings.BIGGABLOBBA)
                .targeting(Targeting.StraightShot)
                .spellApplication(SpellDamageApplication.AfterSpellAnimation)
                .spellAnimation(SpellAnimation.Projectile)
                .attack(Attack.Ranged));
    }

}
