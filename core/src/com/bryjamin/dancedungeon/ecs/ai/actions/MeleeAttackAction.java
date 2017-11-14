package com.bryjamin.dancedungeon.ecs.ai.actions;

import com.artemis.Entity;
import com.artemis.World;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldCondition;
import com.bryjamin.dancedungeon.ecs.components.battle.AbilityPointComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerComponent;
import com.bryjamin.dancedungeon.ecs.systems.FindPlayerSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;

/**
 * Created by BB on 14/11/2017.
 */

public class MeleeAttackAction implements WorldAction {


    @Override
    public void performAction(World world, Entity entity) {

        for (Entity meleeRangeEntity : world.getSystem(TileSystem.class).getCoordinateMap().get(
                world.getSystem(FindPlayerSystem.class).getPlayerComponent(CoordinateComponent.class).coordinates)) {
            if (world.getMapper(PlayerComponent.class).has(meleeRangeEntity)) {
                meleeRangeEntity.getComponent(HealthComponent.class).applyDamage(2.0f);
            }
        }

        entity.getComponent(AbilityPointComponent.class).abilityPoints = 0;

        entity.getComponent(TurnComponent.class).turnOverCondition = new WorldCondition() {
            @Override
            public boolean condition(World world, Entity entity) {
                return true;
            }
        };

    }
}
