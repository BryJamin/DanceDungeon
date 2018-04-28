package com.bryjamin.dancedungeon.factories.map.event;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.bryjamin.dancedungeon.assets.MapData;
import com.bryjamin.dancedungeon.factories.enemy.UnitLibrary;
import com.bryjamin.dancedungeon.factories.map.event.objectives.AbstractObjective;
import com.bryjamin.dancedungeon.factories.map.event.objectives.CompleteWithinObjective;
import com.bryjamin.dancedungeon.factories.map.event.objectives.DefeatAllEnemiesObjective;

public class EventManager {


    private OrderedMap<String, EventCommand> level1BattleEvents = new OrderedMap<String, EventCommand>();
    //private WeightedRoll<>

    private int eventCount;

    public EventManager() { //All events require IDs as each
        put("64f80f4a-e313-401c-91bb-981c9f623eb8", mageBlobEvent());
       // put("64f80f4a-e313-401c-91bb-981c9f623eb8", battleEvent1());
       // put("e1f0cfc1-fdc0-44fb-ad6e-dadd764061e2", battleEvent2());
       // put("a5b45152-2ff8-4ff4-a358-ea1ae9df7366", enemyBattle());
    }

    public void put(String id, EventCommand ec){
        level1BattleEvents.put(id, ec);
        eventCount++;
    }


    public EventCommand getLevel1Event(String id){
        return level1BattleEvents.get(id);
    }


    public Array<String> getKeys(){
        return level1BattleEvents.keys().toArray();
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

    private EventCommand battleEvent1(){
        return new EventCommand() {
            @Override
            public BattleEvent getEvent() {
                return new BattleEvent.Builder(MapData.MAP_1)
                        .enemyPool(UnitLibrary.MELEE_BLOB, UnitLibrary.RANGED_BLASTER)
                        .primaryObjective(new DefeatAllEnemiesObjective())
                        .bonusObjective(new CompleteWithinObjective(AbstractObjective.Reward.MORALE, 3))
                        .build();
            }
        };
    }


    private EventCommand mageBlobEvent(){
        return new EventCommand() {
            @Override
            public BattleEvent getEvent() {
                return new BattleEvent.Builder(MapData.MAP_1)
                        .enemyPool(UnitLibrary.MELEE_BLOB, UnitLibrary.RANGED_BLASTER)
                        .primaryObjective(new DefeatAllEnemiesObjective())
                        .bonusObjective(new CompleteWithinObjective(AbstractObjective.Reward.MORALE, 3))
                        .build();
            }
        };
    }



    private EventCommand battleEvent2(){
        return new EventCommand() {
            @Override
            public BattleEvent getEvent() {
                return new BattleEvent.Builder(MapData.MAP_2)
                        .enemyPool(UnitLibrary.MELEE_BLOB)
                        .primaryObjective(new DefeatAllEnemiesObjective())
                        .bonusObjective(new CompleteWithinObjective(AbstractObjective.Reward.MORALE, 3))
                        .build();
            }
        };
    }


    private EventCommand enemyBattle(){
        return new EventCommand() {
            @Override
            public BattleEvent getEvent() {
                return new BattleEvent.Builder(MapData.MAP_3)
                        .enemyPool(UnitLibrary.MELEE_BLOB)
                        .primaryObjective(new DefeatAllEnemiesObjective())
                        .bonusObjective(new CompleteWithinObjective(AbstractObjective.Reward.MORALE, 3))
                        .build();
            }
        };
    }







}
