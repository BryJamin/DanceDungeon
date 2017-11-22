package com.bryjamin.dancedungeon.factories.player.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 20/11/2017.
 */

public class Slash implements Spell{


    @Override
    public void cast(World world, Entity entity, Coordinates target) {

        for (Entity meleeRangeEntity : world.getSystem(TileSystem.class).getCoordinateMap().get(target)) {
            if (world.getMapper(HealthComponent.class).has(meleeRangeEntity)) {
                meleeRangeEntity.getComponent(HealthComponent.class).applyDamage(6.0f);
            }
        }

    }

    @Override
    public int getApCost() {
        return 1;
    }
}
