package com.bryjamin.dancedungeon.factories.spells.restorative;

import com.bryjamin.dancedungeon.factories.spells.Skill;

/**
 * Created by BB on 04/01/2018.
 */

public class Heal extends Skill {

    public Heal() {
        super(new Builder()
                .name("Heal")
                .attack(Attack.Ranged)
                .spellAnimation(SpellAnimation.Glitter)
                .spellType(SpellType.Heal)
                .icon("skills/Medicine")
                .targeting(Targeting.Ally));
    }

}
