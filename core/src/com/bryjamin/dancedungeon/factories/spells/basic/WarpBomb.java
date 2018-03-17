package com.bryjamin.dancedungeon.factories.spells.basic;

import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.enemy.Push;
import com.bryjamin.dancedungeon.utils.math.Coordinates;


/**
 * Created by BB on 17/03/2018.
 */

public class WarpBomb extends Skill {

    public WarpBomb() {
        super(new Skill.Builder()
                .name("Warp Bomb")
                .description("Places a bomb that deals 1 damage, The bomb pushes all adjacent tiles 1 tile upon detonation")
                .icon("skills/LaserScope")
                .push(0)
                .targeting(Skill.Targeting.Melee)
                .spellType(Skill.SpellType.Attack)
                .actionType(Skill.ActionType.UsesMoveAndAttackAction)
                .spellAnimation(SpellAnimation.Slash)
                .attack(Skill.Attack.Ranged)
                .minRange(2)
                .maxRange(Skill.MAX_MAX_RANGE));


        affectedAreas = new Coordinates[]{new Coordinates(0, 1), new Coordinates(1, 0), new Coordinates(0, -1), new Coordinates(-1, 0)};
        affectedAreaSkill = new Push();

    }

}
