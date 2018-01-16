package com.bryjamin.dancedungeon.factories.map;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.bryjamin.dancedungeon.factories.map.event.MapEvent;
import com.bryjamin.dancedungeon.factories.map.event.MapSection;

/**
 * Created by BB on 07/01/2018.
 */

public class GameMap {

    private MapNode currentMapNode;


    private Array<MapSection> mapNodeSections = new Array<MapSection>();
    private Array<MapNode> allNodes = new Array<MapNode>();

    private OrderedMap<MapNode, MapEvent> nodeMap = new OrderedMap<MapNode, MapEvent>();

    public GameMap(Array<MapSection> mapSections){
        this.mapNodeSections = mapSections;
        for(MapSection mapSection : mapNodeSections){
            allNodes.addAll(mapSection.getMapNodes());
        }
    }

    public MapNode getCurrentMapNode() {
        return currentMapNode;
    }


    public Array<MapNode> getNextNodes(){
        return currentMapNode.successors;
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
}
