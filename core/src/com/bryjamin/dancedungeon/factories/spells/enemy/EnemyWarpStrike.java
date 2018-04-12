package com.bryjamin.dancedungeon.factories.spells.enemy;

import com.bryjamin.dancedungeon.factories.spells.Skill;

/**
 * Created by BB on 17/03/2018.
 */

public class EnemyWarpStrike extends Skill {

    public EnemyWarpStrike() {
        super(new Skill.Builder()
                .name("Enemy Warp Strike")
                .description("Ranged Damage that deals 1 damage")
                .icon("skills/LaserScope")
                .targeting(Skill.Targeting.Melee)
                .attackType(AttackType.Damage)
                .actionType(Skill.ActionType.UsesMoveAndAttackAction)
                .spellAnimation(SpellAnimation.Slash)
                .attack(Skill.Attack.Ranged)
                .minRange(1)
                .maxRange(3));
    }

}
