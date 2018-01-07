package com.bryjamin.dancedungeon.ecs.systems.input;

import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 07/01/2018.
 */

public class GameMap {

    private Coordinates currentPlayerPosition;
    private MapEvent currentMapEvent;

    private Array<MapEvent> mapEvents = new Array<MapEvent>();

    public GameMap(){
        mapEvents = new Array<MapEvent>();
    }

    //For now
    public GameMap(MapEvent... mapEvents){
        this.mapEvents.addAll(mapEvents);
    }

    public Array<MapEvent> getMapEvents() {
        return mapEvents;
    }


    public MapEvent getNextEvent() {
        if(mapEvents.size > 0){
            return mapEvents.removeIndex(0);
        }
        return new TestEvent();
    }


}
