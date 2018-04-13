package com.bryjamin.dancedungeon.factories.map.event;

import com.artemis.World;

/**
 * Created by BB on 07/01/2018.
 */

public abstract class MapEvent {

    public enum EventType {
        BATTLE, BOSS, SHOP, REST
    }

    public abstract EventType getEventType();
    public abstract boolean isComplete(World world);

}
