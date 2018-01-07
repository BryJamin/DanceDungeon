package com.bryjamin.dancedungeon.ecs.systems.input;

import com.artemis.World;

/**
 * Created by BB on 07/01/2018.
 */

public abstract class MapEvent {

    public enum EventType {
        BATTLE, BOSS, SHOP
    }

    public abstract EventType getEventType();

    public abstract boolean setUpEvent(World world);

}
