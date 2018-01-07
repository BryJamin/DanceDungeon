package com.bryjamin.dancedungeon.ecs.systems.input;

import com.artemis.World;

/**
 * Created by BB on 07/01/2018.
 */

public class TestEvent extends MapEvent {
    @Override
    public EventType getEventType() {
        return EventType.SHOP;
    }

    @Override
    public boolean setUpEvent(World world) {
        return false;
    }
}
