package com.bryjamin.dancedungeon.factories.map;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.bryjamin.dancedungeon.factories.map.event.MapSection;

/**
 * Created by BB on 07/01/2018.
 */

public class GameMap {

    private MapNode currentMapNode;

    private Array<MapSection> mapNodeSections = new Array<>();
    private Array<MapNode> allNodes = new Array<>();

    public GameMap(){}

    public GameMap(Array<MapSection> mapSections){
        this.mapNodeSections = mapSections;
        for(MapSection mapSection : mapNodeSections){
            allNodes.addAll(mapSection.getMapNodes());
        }
    }

    public MapNode getById(String id){
        for(MapNode node : allNodes){
            if(node.getId().equals(id)) return node;
        }

        return null;
    }

    public MapNode getCurrentMapNode() {
        return currentMapNode;
    }

    public void setCurrentMapNode(MapNode currentMapNode) {
        this.currentMapNode = currentMapNode;
    }

    public Array<MapNode> getAllNodes() {
        return allNodes;
    }

    public Array<MapSection> getMapNodeSections() {
        return mapNodeSections;
    }


    public float getWidth() {

        MapSection first = mapNodeSections.first();
        MapSection last = mapNodeSections.get(mapNodeSections.size - 1);

        return (last.getStartX() + last.getWidth()) - first.getStartX();
    }

    /**
     * Sets up the connections of loaded maps.
     */
    public void setUpLoadedMap(){

        OrderedMap<String, MapNode> mapOfNodes = new OrderedMap<>();



        for(MapNode mapNode : allNodes){
            mapOfNodes.put(mapNode.getId(), mapNode);
            if(mapNode.getId().equals(currentMapNode.getId()))
                currentMapNode = mapNode;
        }

        for(MapNode mapNode : allNodes){
            for(String s : mapNode.getSuccessorsIds()){
                mapNode.addSuccessors(mapOfNodes.get(s));
            }
        }

    }




}
