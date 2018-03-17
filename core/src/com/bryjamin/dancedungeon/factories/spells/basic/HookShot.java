package com.bryjamin.dancedungeon.factories.spells.basic;

import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.factories.spells.Skill;

/**
 * Created by BB on 17/03/2018.
 */

public class HookShot extends Skill {

    public HookShot() {
        super(new Builder()
                .name("Hook Shot")
                .description("Grabs a target and pulls them 1 tile. Deals 1 damage")
                .icon(TextureStrings.CLASS_BOW_SPEICALIST)
                .targeting(Targeting.StraightShot)
                .push(-1)
                .spellApplication(SpellDamageApplication.AfterSpellAnimation)
                .spellAnimation(SpellAnimation.Projectile)
                .attack(Attack.Ranged));
    }

}


