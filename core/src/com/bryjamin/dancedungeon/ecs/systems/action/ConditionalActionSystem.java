package com.bryjamin.dancedungeon.ecs.systems.action;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.bryjamin.dancedungeon.ecs.components.actions.ConditionalActionsComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;

/**
 * Created by BB on 01/11/2017.
 *
 * Checks the conditions of a given conditional action.
 * If the condition returns true it performs the corresponding Action
 *
 */

public class ConditionalActionSystem extends EntityProcessingSystem {

    ComponentMapper<ConditionalActionsComponent> conditionalMapper;

    @SuppressWarnings("unchecked")
    public ConditionalActionSystem() {
        super(Aspect.all(ConditionalActionsComponent.class));
    }

    @Override
    protected void process(Entity e) {

        ConditionalActionsComponent cac = conditionalMapper.get(e);

        for(WorldConditionalAction worldConditionalAction : cac.conditionalActions){
            if(worldConditionalAction.condition(world, e)){
                worldConditionalAction.performAction(world, e);
            }
        }

    }

}
