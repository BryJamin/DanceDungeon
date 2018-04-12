package com.bryjamin.dancedungeon.factories.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.bryjamin.dancedungeon.factories.enemy.EnemyFactory;
import com.bryjamin.dancedungeon.factories.map.event.BattleEvent;
import com.bryjamin.dancedungeon.factories.map.event.MapEvent;

import java.util.UUID;

/**
 * Created by BB on 16/01/2018.
 */

public class MapNode {

    private final String id = UUID.randomUUID().toString();
    private String eventId = "undefined";

    private Vector2 position = new Vector2();

    private transient Array<MapNode> successors = new Array<>();
    private transient Array<MapNode> parents = new Array<>();

    private Array<String> successorsIds = new Array<>();
    private Array<String> parentsIds = new Array<>();

    private MapEvent.EventType eventType = MapEvent.EventType.BATTLE;

    public void addSuccessors(MapNode... mapNode){
        this.successors.addAll(mapNode);

        for(MapNode node : mapNode){

            //TODO When you load a quick-saved map, the ids will already be set.
            //TODO so when you add the MapNode object you need to make sure the id is not already contained
            //TODO however, there may be a cleaner way to do this.
            if(!successorsIds.contains(node.getId(), false)) {
                successorsIds.add(node.getId());
            }
            node.addParent(this);
        }

    }


    public void addSuccessors(Array<MapNode> mapNodes){
        this.successors.addAll(mapNodes);
    }

    public void addParent(MapNode mapNode){
        this.parents.add(mapNode);
        this.parentsIds.add(mapNode.getId());
    }

    public Vector2 getPosition(){
        return position;
    }

    public void setPosX(float posX) {
        this.position.x = posX;
    }

    public float getPosX() {
        return position.x;
    }

    public void setPosY(float posY) {
        this.position.y = posY;
    }

    public float getPosY() {
        return position.y;
    }

    public int getParentSize(){
        return parents.size;
    }

    public Array<MapNode> getSuccessors() {
        return successors;
    }

    public void setEventType(MapEvent.EventType eventType) {
        this.eventType = eventType;
    }

    public MapEvent.EventType getEventType() {
        return eventType;
    }

    public Array<MapNode> getParents() {
        return parents;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public Array<String> getSuccessorsIds() {
        return successorsIds;
    }

    public Array<String> getParentsIds() {
        return parentsIds;
    }
}