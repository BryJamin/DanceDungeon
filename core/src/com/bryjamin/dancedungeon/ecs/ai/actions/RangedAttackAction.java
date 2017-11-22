package com.bryjamin.dancedungeon.ecs.ai.actions;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldCondition;
import com.bryjamin.dancedungeon.ecs.components.battle.AbilityPointComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.factories.player.spells.animations.Skill;
import com.bryjamin.dancedungeon.utils.math.CoordinateSorter;

/**
 * Created by BB on 15/11/2017.
 */

public class RangedAttackAction implements WorldAction {

    private Skill skill;

    public RangedAttackAction(Skill skill){
        this.skill = skill;
    }


    @Override
    public void performAction(World world, Entity entity) {

        Array<Entity> entityArray = entity.getComponent(TargetComponent.class).getTargets(world);
        if(entityArray.size <= 0) return;
        entityArray.sort(CoordinateSorter.SORT_BY_NEAREST(entity));


        skill.cast(world, entity, entityArray.first().getComponent(CoordinateComponent.class).coordinates);
        entity.getComponent(AbilityPointComponent.class).abilityPoints = 0;

        entity.getComponent(TurnComponent.class).turnOverCondition = new WorldCondition() {
            @Override
            public boolean condition(World world, Entity entity) {
                return true;
            }
        };

    }


}
