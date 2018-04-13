package com.bryjamin.dancedungeon.factories.spells.basic;

import com.bryjamin.dancedungeon.factories.spells.Skill;

/**
 * Created by BB on 28/02/2018.
 */

public class Foresight extends Skill {


    public Foresight() {
        super(new Builder()
                .name("Foresight")
                .icon("skills/Fire")
                .description("Increase the Dodge chance of an self by 20% for three turns")
                .targeting(Targeting.Self)
                .actionType(ActionType.UsesMoveAndAttackAction)
                .spellAnimation(SpellAnimation.Glitter)
                .spellEffects(SpellEffect.Dodge.value(0.2f).duration(3)));
    }











}
