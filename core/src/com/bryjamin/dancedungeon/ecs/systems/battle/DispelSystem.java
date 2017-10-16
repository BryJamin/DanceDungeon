package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.bryjamin.dancedungeon.ecs.components.battle.DispellableComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;

/**
 * Created by BB on 16/10/2017.
 */

public class DispelSystem  extends EntitySystem {

    private ComponentMapper<DispellableComponent> dispelMapper;
    private ComponentMapper<HealthComponent> healthMapper;

    public DispelSystem() {
        super(Aspect.all(DispellableComponent.class, HealthComponent.class));
    }

    @Override
    protected void processSystem() {





    }



    public void dispel(DispellableComponent.Type type){


        for(Entity e : this.getEntities()){

            DispellableComponent dispellableComponent = dispelMapper.get(e);
            HealthComponent healthComponent = healthMapper.get(e);

            if(dispellableComponent.dispelArray.contains(type, true)){
                healthComponent.applyDamage(3);
            };


        }




    }



}
