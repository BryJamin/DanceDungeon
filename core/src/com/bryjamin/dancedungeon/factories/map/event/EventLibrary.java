package com.bryjamin.dancedungeon.factories.map.event;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.bryjamin.dancedungeon.assets.MapData;
import com.bryjamin.dancedungeon.factories.unit.UnitLibrary;
import com.bryjamin.dancedungeon.factories.map.event.objectives.AbstractObjective;
import com.bryjamin.dancedungeon.factories.map.event.objectives.CompleteWithinObjective;
import com.bryjamin.dancedungeon.factories.map.event.objectives.DefeatAllEnemiesObjective;

public class EventLibrary {

    private static ObjectMap<String, BattleEvent> level_one_events;
    private static ObjectMap<String, BattleEvent> events;

    private static final Array<String> skillIDList = new Array<>();


    private static final String LEVEL_ONE = "json/battles/battles.json";
    private static final String TUTORIAL = "json/battles/tutorial.json";

    public static void loadFromJSON(){
        Json json = new Json();

        level_one_events = json.fromJson(ObjectMap.class, Gdx.files.internal(LEVEL_ONE));

        events = json.fromJson(ObjectMap.class, Gdx.files.internal(LEVEL_ONE));

        events.putAll(json.fromJson(ObjectMap.class, Gdx.files.internal(TUTORIAL)));




    }

    public ObjectMap<String, BattleEvent> getEvents() {
            return events;
    }

    public static BattleEvent getEvent(String key) {

        if(events.containsKey(key)) {
            return new BattleEvent(events.get(key));
        } else {
            throw new EventNotFoundException(key);
        }
    }


    public static String getEventID() {

        return events.keys().toArray().first();

/*        if(events.containsKey(key)) {
            return new BattleEvent(events.get(key));
        } else {
            throw new EventNotFoundException(key);
        }*/
    }

    private static class EventNotFoundException extends RuntimeException {

        private String id;

        public EventNotFoundException(String id){
            this.id = id;
        }

        @Override
        public String getMessage() {
            return "Event not found in Library. ID: " + id;
        }
    }


    private OrderedMap<String, EventCommand> level1BattleEvents = new OrderedMap<String, EventCommand>();
    //private WeightedRoll<>




    private int eventCount;




    public EventCommand getLevel1Event(String id){
        return level1BattleEvents.get(id);
    }


    public static Array<String> getKeys(){
        return events.keys().toArray();
    }
    public static Array<String> getLevelOneKeys(){
        return level_one_events.keys().toArray();
    }

    public OrderedMap<String, EventCommand> getLevel1BattleEvents() {
        return level1BattleEvents;
    }

    public interface EventCommand {
        public BattleEvent getEvent();
    }

    public int getEventCount() {
        return eventCount;
    }



    public static String TUTORIAL_EVENT = "TUTORIAL";
/*
    public EventCommand bossBattle(){
        return new EventCommand() {
            @Override
            public BattleEvent getEvent() {
                return new BattleEvent.Builder(MapData.MAP_3)
                        .addEnemyWave(UnitLibrary.BIG_BLASTER_BOSS, BattleEvent.RANDOM_POOLED_UNIT, BattleEvent.RANDOM_POOLED_UNIT)
                        .enemyPool(UnitLibrary.RANGED_BLASTER, UnitLibrary.RANGED_LOBBA, UnitLibrary.MELEE_BLOB)
                        .primaryObjective(new DefeatAllEnemiesObjective())
                        .bonusObjective(new CompleteWithinObjective(AbstractObjective.Reward.MORALE, 3))
                        .build();
            }
        };
    }*/






}
