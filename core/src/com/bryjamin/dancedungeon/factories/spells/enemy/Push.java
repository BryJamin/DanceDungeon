package com.bryjamin.dancedungeon.factories.spells.enemy;

import com.bryjamin.dancedungeon.factories.spells.Skill;

/**
 * Created by BB on 17/03/2018.
 */

public class Push extends Skill {

    public Push() {
        super(new Builder()
                .name("Fireball")
                .icon("skills/Fire")
                .description("Fires a ball of flame at the enemy. This is a FREE Action")
                .targeting(Targeting.StraightShot)
                .baseDamage(0)
                .push(1)
                .attackType(AttackType.Damage)
                .spellAnimation(SpellAnimation.Projectile)
                .spellCoolDown(3));
    }
}
