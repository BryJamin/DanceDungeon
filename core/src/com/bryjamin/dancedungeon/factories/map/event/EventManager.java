package com.bryjamin.dancedungeon.factories.map.event;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.bryjamin.dancedungeon.assets.MapData;
import com.bryjamin.dancedungeon.factories.enemy.EnemyFactory;
import com.bryjamin.dancedungeon.factories.map.event.objectives.AbstractObjective;
import com.bryjamin.dancedungeon.factories.map.event.objectives.CompleteWithinObjective;
import com.bryjamin.dancedungeon.factories.map.event.objectives.DefeatAllEnemiesObjective;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.random.WeightedObject;
import com.bryjamin.dancedungeon.utils.random.WeightedRoll;

import java.util.Map;

public class EventManager {


    private OrderedMap<String, EventCommand> level1BattleEvents = new OrderedMap<String, EventCommand>();
    //private WeightedRoll<>

    private String[] keys = {
            "64f80f4a-e313-401c-91bb-981c9f623eb8",
            "1e46533c-31ed-41d6-a34e-489c8be40767"
    };


    public EventManager() { //All events require IDs as each
        //level1BattleEvents.put("64f80f4a-e313-401c-91bb-981c9f623eb8", battleEvent1());
        //level1BattleEvents.put("64f80f4a-e313-401c-91bb-981c9f623eb8", battleEvent1());
        level1BattleEvents.put("1e46533c-31ed-41d6-a34e-489c8be40767", enemyBattle());
    }


    public EventCommand getLevel1Event(String id){
        System.out.println(id);
        return level1BattleEvents.get(id);
    }


    public Array<String> getKeys(){
        return level1BattleEvents.keys().toArray();
    }


    public interface EventCommand {
        public BattleEvent getEvent();
    }

    private EventCommand battleEvent1(){
        return new EventCommand() {
            @Override
            public BattleEvent getEvent() {
                return new BattleEvent.Builder(MapData.MAP_1)
                        .enemyPool(EnemyFactory.FAST_BLOB, EnemyFactory.MAGE_BLOB)
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
                        .enemyPool(EnemyFactory.SPITTER_BLOB, EnemyFactory.FAST_BLOB)
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
                        .enemyPool()
                        .primaryObjective(new DefeatAllEnemiesObjective())
                        .bonusObjective(new CompleteWithinObjective(AbstractObjective.Reward.MORALE, 99))
                        .build();
            }
        };
    }







}
