package com.bryjamin.dancedungeon.factories.player.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.TurnActionMonitorComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 20/12/2017.
 */

public class BasicAttack extends CooldownSpellDescription {


    @Override
    public Array<Entity> createTargeting(World world, Entity player) {
        return new Array<Entity>();
    }

    @Override
    public void cast(World world, Entity entity, Coordinates target) {

        for(Entity e : world.getSystem(TileSystem.class).getCoordinateMap().get(target)){
            if(world.getMapper(HealthComponent.class).has(e)){
                e.getComponent(HealthComponent.class).applyDamage(entity.getComponent(StatComponent.class).power);
            }
        };

        entity.getComponent(TurnActionMonitorComponent.class).attackActionAvailable = false;
        entity.getComponent(TurnActionMonitorComponent.class).movementActionAvailable = false;

    }
}
