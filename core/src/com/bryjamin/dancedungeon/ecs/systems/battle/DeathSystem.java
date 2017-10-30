package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeadComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.ParentComponent;
import com.bryjamin.dancedungeon.ecs.systems.ParentChildSystem;

/**
 * Created by BB on 15/10/2017.
 */

public class DeathSystem extends EntityProcessingSystem {



    ComponentMapper<ParentComponent> parentMapper;



    @SuppressWarnings("unchecked")
    public DeathSystem() {
        super(Aspect.all(DeadComponent.class));
    }

    @Override
    protected void process(Entity e) {
        kill(e);
    }

    public void kill(Entity e){

        if(parentMapper.has(e)){
            killChildComponents(e.getComponent(ParentComponent.class));
        }

        e.deleteFromWorld();
    };

    public void killChildComponents(ParentComponent parentComponent){
        for(Entity e : world.getSystem(ParentChildSystem.class).getChildren(parentComponent)){
            kill(e);
        };
    }

    public void killChildComponents(Entity e){
        if(parentMapper.has(e)){
            killChildComponents(e.getComponent(ParentComponent.class));
        }
    }





}
