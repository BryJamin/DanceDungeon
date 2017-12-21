package com.bryjamin.dancedungeon.ecs.ai.actions;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.TurnActionMonitorComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.factories.player.spells.SkillDescription;
import com.bryjamin.dancedungeon.utils.math.CoordinateSorter;

/**
 * Created by BB on 15/11/2017.
 */

public class RangedAttackAction implements WorldAction {

    private SkillDescription skill;

    public RangedAttackAction(SkillDescription skill){
        this.skill = skill;
    }


    @Override
    public void performAction(World world, Entity entity) {

        Array<Entity> entityArray = entity.getComponent(TargetComponent.class).getTargets(world);
        if(entityArray.size <= 0) return;
        entityArray.sort(CoordinateSorter.SORT_BY_NEAREST(entity));


        skill.cast(world, entity, entityArray.first().getComponent(CoordinateComponent.class).coordinates);

        entity.getComponent(TurnActionMonitorComponent.class).attackActionAvailable = false;


    }


}
