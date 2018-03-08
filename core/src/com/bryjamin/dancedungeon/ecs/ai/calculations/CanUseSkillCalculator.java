package com.bryjamin.dancedungeon.ecs.ai.calculations;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

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


        Skill skill = entity.getComponent(SkillsComponent.class).skills.first();
        Array<Coordinates> targetCoordinatesArray = new Array<Coordinates>();

        for (Entity e : entity.getComponent(TargetComponent.class).getTargets(world)) {
            targetCoordinatesArray.add(new Coordinates(e.getComponent(CoordinateComponent.class).coordinates));
        }
        ;


        for (Coordinates c : skill.getAffectedCoordinates(world, entity.getComponent(CoordinateComponent.class).coordinates)) {
            if (targetCoordinatesArray.contains(c, false)) {
                return skill.canCast(world, entity) ? canUseScore : cannotUseScore;
            }
        }

        return cannotUseScore;
    }
}
