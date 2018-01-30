package com.bryjamin.dancedungeon.ecs.ai.calculations;

import com.artemis.Entity;
import com.artemis.World;
import com.bryjamin.dancedungeon.factories.spells.Skill;

/**
 * Created by BB on 29/11/2017.
 */

public class CanUseSkillCalculator implements ActionScoreCalculation {

    private Skill skill;

    private Float canUseScore;
    private Float cannotUseScore;

    public CanUseSkillCalculator(Skill skill, Float canUseScore, Float cannotUseScore){
        this.canUseScore = canUseScore;
        this.cannotUseScore = cannotUseScore;
        this.skill = skill;
    }

    @Override
    public Float calculateScore(World world, Entity entity) {
        System.out.println(skill.canCast(world, entity));
        return skill.canCast(world, entity) ? canUseScore : cannotUseScore;
    }
}
