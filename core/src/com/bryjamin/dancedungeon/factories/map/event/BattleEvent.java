package com.bryjamin.dancedungeon.factories.map.event;

import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.assets.MapData;
import com.bryjamin.dancedungeon.factories.map.event.objectives.AbstractObjective;
import com.bryjamin.dancedungeon.factories.map.event.objectives.CompleteWithinObjective;
import com.bryjamin.dancedungeon.factories.map.event.objectives.DefeatAllEnemiesObjective;

/**
 * Created by BB on 07/01/2018.
 */

public class BattleEvent extends MapEvent {

    public static final String RANDOM_POOLED_UNIT = "random_pooled_unit";

    private String id = "unidentified";
    private String mapLocation = MapData.MAP_1;

    private int numberOfWaves = 0;

    private AbstractObjective primaryObjective = new DefeatAllEnemiesObjective();
    private AbstractObjective[] bonusObjectives = new AbstractObjective[]{new CompleteWithinObjective(7)};

    private Array<String> enemies = new Array<String>();
    private Queue<Array<String>> waves = new Queue<>();


    public BattleEvent(String... enemies){
        this.enemies.addAll(enemies);
    }


    public BattleEvent(Builder b){
        this.mapLocation = b.mapLocation;
        this.primaryObjective = b.primaryObjective;
        this.bonusObjectives = b.bonusObjectives;
        this.enemies = b.enemyPool;
        this.waves = b.presetWaves;
        this.numberOfWaves = b.numberOfWaves;
    }


    public Array<String> getEnemies() {
        return enemies;
    }

    public Queue<Array<String>> getWaves() {
        return waves;
    }

    public AbstractObjective getPrimaryObjective() {
        return primaryObjective;
    }

    public AbstractObjective[] getBonusObjectives() {
        return bonusObjectives;
    }

    @Override
    public EventType getEventType() {
        return EventType.BATTLE;
    }

    @Override
    public boolean isComplete(World world) {
        return (primaryObjective.isComplete(world));
    }

    public String getId() {
        return id;
    }

    public String getMapLocation() {
        return mapLocation;
    }

    public static class Builder {

        private final String mapLocation;

        private int numberOfWaves = 1;

        private Queue<Array<String>> presetWaves = new Queue<>();
        private Array<String> enemyPool = new Array<String>();
        private AbstractObjective primaryObjective = new DefeatAllEnemiesObjective();
        private AbstractObjective[] bonusObjectives = new AbstractObjective[]{};

        public Builder(String mapLocation){
            this.mapLocation = mapLocation;
        }

        public Builder enemyPool(String... val) {
            this.enemyPool.clear();
            this.enemyPool.addAll(val); return this;
        }


        public Builder addEnemyWave(String... val) {
            Array<String> strings = new Array<>();
            strings.addAll(val);
            presetWaves.addLast(strings);
            return this;
        }


        public Builder numberOfWaves(int val) {
            this.numberOfWaves = val;
            return this;
        }



        public Builder primaryObjective(AbstractObjective val)
        { this.primaryObjective = val; return this; }

        public Builder bonusObjective(AbstractObjective... val)
        { this.bonusObjectives = val;


        return this; }

        public BattleEvent build(){
            return new BattleEvent(this);
        }

    }


    public int getNumberOfWaves() {
        return numberOfWaves;
    }

    public void decreaseNumberOfWaves(){
        numberOfWaves--;
    }


}
