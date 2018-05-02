package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.bryjamin.dancedungeon.ecs.components.battle.AvailableActionsCompnent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationMapComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.GreyScaleComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.SelectedEntityComponent;

/**
 * Created by BB on 23/12/2017.
 */


//TODO should be turned into an Observer. To listen to the actionQueueSystem
public class NoMoreActionsGreyScaleSystem extends EntityProcessingSystem {

    private ComponentMapper<AnimationMapComponent> amc;
    private ComponentMapper<GreyScaleComponent> greyScaleMapper;

    private ComponentMapper<PlayerControlledComponent> playerM;



    /**
     *
     * System used to give a greyscale to units that no longer have actions
     *
     */
    public NoMoreActionsGreyScaleSystem() {
        super(Aspect.all(PlayerControlledComponent.class, AvailableActionsCompnent.class));
    }

    @Override
    protected void process(Entity e) {

        AvailableActionsCompnent availableActionsCompnent = e.getComponent(AvailableActionsCompnent.class);

        if(!availableActionsCompnent.hasActions() && !greyScaleMapper.has(e)){
            e.edit().add(new GreyScaleComponent());
            e.edit().remove(SelectedEntityComponent.class);
        } else if(greyScaleMapper.has(e) && availableActionsCompnent.hasActions()){
            e.edit().remove(GreyScaleComponent.class);
        }

    }

}
