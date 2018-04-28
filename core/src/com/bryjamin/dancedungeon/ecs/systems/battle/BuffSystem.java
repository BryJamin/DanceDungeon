package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.bryjamin.dancedungeon.ecs.components.battle.BuffComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.factories.player.UnitData;

/**
 * Created by BB on 30/01/2018.
 *
 * Buff System is currently discontinued.
 *
 * It will remain here until further notice
 *
 */
public class BuffSystem extends EntityProcessingSystem {

    private ComponentMapper<BuffComponent> buffM;
    private ComponentMapper<UnitComponent> uM;

    public BuffSystem() {
        super(Aspect.all(BuffComponent.class, UnitComponent.class));
    }

    @Override
    protected void process(Entity e) {

        BuffComponent bc = buffM.get(e);

        UnitData unitData = uM.get(e).getUnitData();

        //Reset buffed stats


    }
}
