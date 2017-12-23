package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.GreyScaleComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;

/**
 * Created by BB on 23/12/2017.
 */

public class NoMoreActionsSystem extends EntityProcessingSystem {

    private ComponentMapper<GreyScaleComponent> greyScaleMapper;


    /**
     *
     * System used to give a greyscale to units that no longer have actions
     *
     */
    public NoMoreActionsSystem() {
        super(Aspect.all(PlayerControlledComponent.class, TurnComponent.class));
    }

    @Override
    protected void process(Entity e) {

        TurnComponent turnComponent = e.getComponent(TurnComponent.class);

        if(!turnComponent.hasActions() && !greyScaleMapper.has(e)){
            e.edit().add(new GreyScaleComponent());
        } else if(greyScaleMapper.has(e) && turnComponent.hasActions()){
            e.edit().remove(GreyScaleComponent.class);
        }

    }

/*    @Override
    protected void processSystem() {

    }*/
}
