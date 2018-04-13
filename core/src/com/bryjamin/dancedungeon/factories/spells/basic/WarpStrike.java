package com.bryjamin.dancedungeon.factories.spells.basic;

import com.bryjamin.dancedungeon.factories.spells.Skill;

/**
 * Created by BB on 17/03/2018.
 */

public class WarpStrike extends Skill{

    public WarpStrike() {
        super(new Skill.Builder()
                .name("Warp Strike")
                .description("Ranged Damage that deals 1 damage, Pulls Target 1 tile")
                .icon("skills/LaserScope")
                .push(-1)
                .targeting(Skill.Targeting.Melee)
                .attackType(AttackType.Damage)
                .actionType(Skill.ActionType.UsesMoveAndAttackAction)
                .spellAnimation(SpellAnimation.Slash)
                .minRange(2)
                .maxRange(Skill.MAX_MAX_RANGE));

    }

}
