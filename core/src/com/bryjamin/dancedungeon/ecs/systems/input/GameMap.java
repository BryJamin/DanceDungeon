package com.bryjamin.dancedungeon.ecs.systems.input;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;

/**
 * Created by BB on 07/01/2018.
 */

public class GameMap {

    private MapNode currentMapNode;

    private Array<MapEvent> mapEvents = new Array<MapEvent>();

    private OrderedMap<MapNode, MapEvent> nodeMap = new OrderedMap<MapNode, MapEvent>();

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

/*    public Array<MapEvent> getNextMapEvents(){

        Array<MapEvent> relatedEvents = new Array<MapEvent>();

        for(MapNode mapNode : currentMapNode.futureNodes){

        }



    }*/

    public Array<MapNode> getNextNodes(){
        return currentMapNode.futureNodes;
    }

    public MapEvent setNodeAndGetNextEvent(MapNode mapNode){
        currentMapNode = mapNode;
        return nodeMap.get(mapNode);
    }


    public MapEvent getNextEvent() {
        if(mapEvents.size > 0){
            return mapEvents.removeIndex(0);
        }
        return new TestEvent();
    }



    public class MapNode {
       // Array<MapNode> previousNodes;
        public Array<MapNode> futureNodes;



    }


}
