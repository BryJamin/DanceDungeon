package com.bryjamin.dancedungeon.factories.spells.basic;

import com.bryjamin.dancedungeon.factories.spells.Skill;

/**
 * Created by BB on 30/01/2018.
 */

public class DodgeUp extends Skill {

    public DodgeUp() {
        super(new Builder()
                .name("Dodge Up")
                .icon("skills/Fire")
                .description("Increase the Dodge chance of an ally by 20% for one turn")
                .targeting(Targeting.Ally)
                .actionType(ActionType.UsesMoveAndAttackAction)
                .spellAnimation(SpellAnimation.Glitter)
                .spellEffects(SpellEffect.Dodge.value(1).duration(2))
                .spellCoolDown(3));
    }



}
