package com.bryjamin.dancedungeon.factories.spells.basic;

import com.bryjamin.dancedungeon.factories.spells.Skill;

/**
 * Created by BB on 29/01/2018.
 */

public class FireWeapon extends Skill {

    public FireWeapon() {
        super(new Builder()
                .name("Fire Weapon")
                .description("Uses Attack And Movement Action")
                .icon("skills/LaserScope")
                .targeting(Targeting.StraightShot)
                .spellType(SpellType.Attack)
                .actionType(ActionType.UsesMoveAndAttackAction)
                .spellAnimation(SpellAnimation.Slash)
                .attack(Attack.Ranged));
    }


}
