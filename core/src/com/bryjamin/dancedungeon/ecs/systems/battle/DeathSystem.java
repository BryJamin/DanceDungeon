package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.bryjamin.dancedungeon.ecs.components.actions.OnDeathActionsComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeadComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.ParentComponent;
import com.bryjamin.dancedungeon.ecs.systems.ParentChildSystem;

/**
 * Created by BB on 15/10/2017.
 *
 * When an entity is given a 'DeadComponent' it is first ran through this system
 * to run any 'OnDeathActions' the entity may have.
 *
 * The entity is this deleted from the world.
 */
public class DeathSystem extends EntityProcessingSystem {



        ComponentMapper<ParentComponent> parentMapper;
        ComponentMapper<OnDeathActionsComponent> onDeathActionsMapper;



    @SuppressWarnings("unchecked")
    public DeathSystem() {
        super(Aspect.all(DeadComponent.class));
    }

    @Override
    protected void process(Entity e) {
        kill(e);
    }

    /**
     * When called cleanly removes an entity from the world, while also performing theur on deah action
     * @param e
     */
    public void kill(Entity e){

        if(onDeathActionsMapper.has(e)){
            for(WorldAction worldAction : onDeathActionsMapper.get(e).actions){
                worldAction.performAction(world, e);
            }
        }

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
