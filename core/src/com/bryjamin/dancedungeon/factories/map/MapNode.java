package com.bryjamin.dancedungeon.factories.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.factories.enemy.EnemyFactory;
import com.bryjamin.dancedungeon.factories.map.event.BattleEvent;
import com.bryjamin.dancedungeon.factories.map.event.MapEvent;

/**
 * Created by BB on 16/01/2018.
 */

public class MapNode {

    private String eventId = "undefined";

    private Vector2 position = new Vector2();

    private Array<MapNode> successors = new Array<MapNode>();
    private Array<MapNode> parents = new Array<MapNode>();

    private MapEvent.EventType eventType = MapEvent.EventType.BATTLE;

    public void addSuccessors(MapNode... mapNode){
        this.successors.addAll(mapNode);

        for(MapNode node : mapNode){
            node.addParent(this);
        }

    }

    public void addSuccessors(Array<MapNode> mapNodes){
        this.successors.addAll(mapNodes);
    }

    public void addParent(MapNode mapNode){
        this.parents.add(mapNode);
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

    public String getEventId() {
        return eventId;
    }
}