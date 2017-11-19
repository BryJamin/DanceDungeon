package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.VelocityComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.BlinkOnHitComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeadComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;

/**
 * Created by BB on 15/10/2017.
 */

public class HealthSystem extends EntityProcessingSystem {

    ComponentMapper<HealthComponent> healthm;
    ComponentMapper<VelocityComponent> vm;
    ComponentMapper<PlayerControlledComponent> pm;
    ComponentMapper<BlinkOnHitComponent> blinkOnHitMapper;

    @SuppressWarnings("unchecked")
    public HealthSystem() {
        super(Aspect.all(HealthComponent.class));
    }


    @Override
    protected void process(Entity e) {

        HealthComponent hc = healthm.get(e);


        if(hc.getAccumulatedDamage() > 0 && blinkOnHitMapper.has(e))
            blinkOnHitMapper.get(e).isHit = true;

        hc.health = hc.health - hc.getAccumulatedDamage();
        hc.clearDamage();
        if(hc.health <= 0) e.edit().add(new DeadComponent());


    }


}

