package com.bryjamin.dancedungeon.factories.spells.basic;

import com.bryjamin.dancedungeon.factories.spells.Skill;

/**
 * Created by BB on 18/11/2017.
 */

public class Fireball extends Skill {

    public Fireball() {
        super(new Builder()
                .name("Fireball")
                .icon("skills/Fire")
                .description("Fires a ball of flame at the enemy. This is a FREE Action")
                .targeting(Targeting.Enemy)
                .spellType(SpellType.MagicAttack)
                .spellApplication(SpellDamageApplication.AfterSpellAnimation)
                .actionType(ActionType.Free)
                .spellAnimation(SpellAnimation.Projectile)
                .spellCoolDown(3)
                .attack(Attack.Ranged));
    }

}
