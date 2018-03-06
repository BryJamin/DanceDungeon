package com.bryjamin.dancedungeon.ecs.ai.actions;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.StoredSkillComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.EnemyIntentSystem;
import com.bryjamin.dancedungeon.utils.math.CoordinateSorter;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 14/11/2017.
 */

public class BasicAttackAction implements WorldAction {

    @Override
    public void performAction(World world, Entity entity) {

        Array<Entity> entityArray = entity.getComponent(TargetComponent.class).getTargets(world);
        if(entityArray.size <= 0) return;
        entityArray.sort(CoordinateSorter.SORT_BY_NEAREST(entity));

        Coordinates c = entityArray.first().getComponent(CoordinateComponent.class).coordinates;

        //TODO this only really works for enemies with the current attacking actions
        //TODO what happens to enemies that have ranged attacks?

        entity.edit().add(new StoredSkillComponent(entity.getComponent(CoordinateComponent.class).coordinates, c,
                entity.getComponent(SkillsComponent.class).basicAttack));

        world.getSystem(EnemyIntentSystem.class).updateIntent();

        entity.getComponent(TurnComponent.class).attackActionAvailable = false;
        entity.getComponent(TurnComponent.class).movementActionAvailable = false;






    }
}
