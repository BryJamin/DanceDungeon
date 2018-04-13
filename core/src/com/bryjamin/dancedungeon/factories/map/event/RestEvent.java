package com.bryjamin.dancedungeon.factories.map.event;

import com.artemis.Entity;
import com.artemis.World;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeadComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.PlayerControlledSystem;
import com.bryjamin.dancedungeon.factories.ButtonFactory;
import com.bryjamin.dancedungeon.utils.Measure;

/**
 * Created by BB on 08/01/2018.
 */

public class RestEvent extends MapEvent {

    public boolean complete = false;

    @Override
    public EventType getEventType() {
        return null;
    }

    @Override
    public boolean isComplete(World world) {
        return complete;
    }

}
