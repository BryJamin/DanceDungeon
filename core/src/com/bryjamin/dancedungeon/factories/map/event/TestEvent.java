package com.bryjamin.dancedungeon.factories.map.event;

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
    public void setUpEvent(World world) {
    }

    @Override
    public boolean isComplete(World world) {
        return true;
    }

    @Override
    public void cleanUpEvent(World world) {

    }

    @Override
    public boolean cleanUpComplete(World world) {
        return true;
    }
}
