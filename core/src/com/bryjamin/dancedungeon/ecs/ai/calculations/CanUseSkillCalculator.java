package com.bryjamin.dancedungeon.ecs.ai.calculations;

import com.artemis.Entity;
import com.artemis.World;
import com.bryjamin.dancedungeon.factories.player.spells.SkillDescription;

/**
 * Created by BB on 29/11/2017.
 */

public class CanUseSkillCalculator implements ActionScoreCalculation {

    private SkillDescription skillDescription;

    private Float canUseScore;
    private Float cannotUseScore;

    public CanUseSkillCalculator(SkillDescription skillDescription, Float canUseScore, Float cannotUseScore){
        this.canUseScore = canUseScore;
        this.cannotUseScore = cannotUseScore;
        this.skillDescription = skillDescription;
    }

    @Override
    public Float calculateScore(World world, Entity entity) {
        return skillDescription.canCast(world, entity) ? canUseScore : cannotUseScore;
    }
}
