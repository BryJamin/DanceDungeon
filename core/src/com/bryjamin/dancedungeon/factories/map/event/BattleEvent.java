package com.bryjamin.dancedungeon.factories.map.event;

import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.assets.MapData;
import com.bryjamin.dancedungeon.factories.map.event.objectives.AbstractObjective;
import com.bryjamin.dancedungeon.factories.map.event.objectives.CompleteWithinObjective;
import com.bryjamin.dancedungeon.factories.map.event.objectives.DefeatAllEnemiesObjective;

/**
 * Created by BB on 07/01/2018.
 */

public class BattleEvent extends MapEvent {

    private String mapLocation = MapData.MAP_1;

    private AbstractObjective primaryObjective = new DefeatAllEnemiesObjective();
    private AbstractObjective[] bonusObjectives = new AbstractObjective[]{new CompleteWithinObjective(7)};

    private Array<String> enemies = new Array<String>();

    public BattleEvent(String... enemies){
        this.enemies.addAll(enemies);
    }


    public BattleEvent(Builder b){
        this.mapLocation = b.mapLocation;
        this.primaryObjective = b.primaryObjective;
        this.bonusObjectives = b.bonusObjectives;

        System.out.println(bonusObjectives.length);
        System.out.println("Odd");
        this.enemies = b.enemyPool;

        System.out.println(enemies.size);
    }


    public Array<String> getEnemies() {
        System.out.println("Inside here");
        return enemies;
    }


    public AbstractObjective getPrimaryObjective() {
        return primaryObjective;
    }

    public AbstractObjective[] getBonusObjective() {
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

    public String getMapLocation() {
        return mapLocation;
    }

    public static class Builder {

        private final String mapLocation;

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

        public Builder primaryObjective(AbstractObjective val)
        { this.primaryObjective = val; return this; }

        public Builder bonusObjective(AbstractObjective... val)
        { this.bonusObjectives = val;
            System.out.println("BONUS OBJECTIVE LENGTH IS" + this.bonusObjectives.length);

        return this; }

        public BattleEvent build(){
            return new BattleEvent(this);
        }


    }


}
