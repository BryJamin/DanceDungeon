package com.bryjamin.dancedungeon.factories.spells.basic;

import com.bryjamin.dancedungeon.factories.spells.Skill;

/**
 * Created by BB on 28/02/2018.
 */

public class StraightShot extends Skill {

    public StraightShot() {
        super(new Builder()
                .name("Straight Shot")
                .description("Ranged Damage that deals 1 damage, Pushes target 1 tile")
                .icon("skills/LaserScope")
                .push(1)
                .targeting(Targeting.StraightShot)
                .attackType(AttackType.Damage)
                .actionType(ActionType.UsesMoveAndAttackAction)
                .spellAnimation(SpellAnimation.Projectile));
    }


}
