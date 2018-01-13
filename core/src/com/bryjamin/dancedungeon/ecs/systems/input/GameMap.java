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

        for(MapNode mapNode : currentMapNode.successors){

        }



    }*/

    public Array<MapNode> getNextNodes(){
        return currentMapNode.successors;
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



    //I'm trying to make a directed graph

    public static class MapNode {

        private float posX;
        private float posY;

       // Array<MapNode> previousNodes;
        public Array<MapNode> successors = new Array<MapNode>();

        public MapEvent mapEvent;

        public void addSuccessors(MapNode... mapNode){
            this.successors.addAll(mapNode);
        }

        public float getPosX() {
            return posX;
        }

        public void setPosX(float posX) {
            //System.out.println("setPOsx " + posX);
            this.posX = posX;
        }

        public Array<MapNode> getSuccessors() {
            return successors;
        }

        public float getPosY() {
            return posY;
        }

        public void setPosY(float posY) {
            this.posY = posY;
        }

        public MapEvent getMapEvent() {
            return mapEvent;
        }

        public void setMapEvent(MapEvent mapEvent) {
            this.mapEvent = mapEvent;
        }
    }


}
