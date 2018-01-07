package com.bryjamin.dancedungeon.ecs.systems.input;

import com.artemis.World;
import com.artemis.utils.Bag;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.factories.enemy.EnemyFactory;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;

/**
 * Created by BB on 07/01/2018.
 */

public class BattleEvent extends MapEvent {

    private Array<String> enemies = new Array<String>();

    public BattleEvent(String... enemies){
        this.enemies.addAll(enemies);
    }

    public Array<String> getEnemies() {
        return enemies;
    }

    public Bag<ComponentBag> convertEnemiesIntoComponentBags(){

        Bag<ComponentBag> enemyBags = new Bag<ComponentBag>();
        EnemyFactory enemyFactory = new EnemyFactory();

        for(String s : enemies){
            enemyBags.add(enemyFactory.get(s));
        }

        return enemyBags;


    }

    @Override
    public EventType getEventType() {
        return EventType.BATTLE;
    }

    @Override
    public boolean setUpEvent(World world) {





        return false;
    }
}
