package com.bryjamin.dancedungeon.factories.spells.basic;

import com.bryjamin.dancedungeon.factories.spells.Skill;

/**
 * Created by BB on 28/02/2018.
 */

public class StraightShot extends Skill {

    public StraightShot() {
        super(new Builder()
                .name("Straight Shot")
                .description("Ranged Attack that deals 1 damage")
                .icon("skills/LaserScope")
                .targeting(Targeting.StraightShot)
                .spellType(SpellType.MagicAttack)
                .actionType(ActionType.UsesMoveAndAttackAction)
                .spellAnimation(SpellAnimation.Slash)
                .attack(Attack.Ranged));
    }


}
