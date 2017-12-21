package com.bryjamin.dancedungeon.ecs.ai.actions;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.TurnActionMonitorComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.factories.player.spells.SkillDescription;
import com.bryjamin.dancedungeon.factories.player.spells.SlashDescription;
import com.bryjamin.dancedungeon.utils.math.CoordinateSorter;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 14/11/2017.
 */

public class MeleeAttackAction implements WorldAction {

    private SkillDescription skillDescription = new SlashDescription();

    public MeleeAttackAction(SkillDescription skillDescription){
        this.skillDescription = skillDescription;
    }

    @Override
    public void performAction(World world, Entity entity) {

        Array<Entity> entityArray = entity.getComponent(TargetComponent.class).getTargets(world);
        if(entityArray.size <= 0) return;
        entityArray.sort(CoordinateSorter.SORT_BY_NEAREST(entity));

        Coordinates c = entityArray.first().getComponent(CoordinateComponent.class).coordinates;
        skillDescription.cast(world, entity, c);

        entity.getComponent(TurnActionMonitorComponent.class).attackActionAvailable = false;

    }
}
