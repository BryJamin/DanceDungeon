package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.ReselectEntityComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.SelectedEntityComponent;

/**
 * Created by BB on 13/02/2018.
 *
 * This system exists to reselect a playable character
 */

public class ReselectTargetSystem extends EntitySystem {

    ActionQueueSystem actionQueueSystem;

    private ComponentMapper<TurnComponent> tm;
    private ComponentMapper<ReselectEntityComponent> rem;

    public ReselectTargetSystem() {
        super(Aspect.all(ReselectEntityComponent.class, TurnComponent.class, PlayerControlledComponent.class));
    }

    @Override
    public void inserted(Entity e) {
        if(this.getEntities().size() > 1){
            e.edit().remove(ReselectEntityComponent.class);
        }
    }

    @Override
    protected void processSystem() {

        Entity e = this.getEntities().get(0);

        if(!actionQueueSystem.isProcessing()){
            if(tm.get(e).hasActions()){
                e.edit().add(new SelectedEntityComponent());
            };
            e.edit().remove(ReselectEntityComponent.class);
        }

    }

    @Override
    protected boolean checkProcessing() {
        return this.getEntities().size() > 0;
    }
}
