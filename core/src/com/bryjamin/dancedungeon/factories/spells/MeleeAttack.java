package com.bryjamin.dancedungeon.factories.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 20/12/2017.
 *
 * This Melee Attack
 *
 * Used as a basic attack for enemies that do not have a ranged weapon.
 *
 * This attack deals damage based on the power of the entity using it
 *
 */

public class MeleeAttack extends SkillDescription {


    @Override
    public Array<Entity> createTargeting(World world, Entity player) {
        return new Array<Entity>();
    }

    @Override
    public void cast(World world, Entity entity, Coordinates target) {

        for(Entity e : world.getSystem(TileSystem.class).getCoordinateMap().get(target)){
            if(world.getMapper(HealthComponent.class).has(e)){
                //System.out.println(entity.getComponent(StatComponent.class).power);
                e.getComponent(HealthComponent.class).applyDamage(entity.getComponent(StatComponent.class).power);
            }
        };

        entity.getComponent(TurnComponent.class).attackActionAvailable = false;
        entity.getComponent(TurnComponent.class).movementActionAvailable = false;

    }

    @Override
    public void endTurnUpdate() {

    }

    @Override
    public String getIcon() {
        return null;
    }

    @Override
    public boolean canCast(World world, Entity entity) {
        return entity.getComponent(TurnComponent.class).attackActionAvailable = false;
    }
}
