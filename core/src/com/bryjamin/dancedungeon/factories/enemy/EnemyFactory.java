package com.bryjamin.dancedungeon.factories.enemy;

import com.badlogic.gdx.utils.OrderedMap;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;

/**
 * Created by BB on 23/12/2017.
 */

public class EnemyFactory {

    public static final String BLOB = "blob";
    public static final String FAST_BLOB = "fast_blob";
    public static final String MAGE_BLOB = "mage_blob";


    public interface Command {
        public ComponentBag getEnemy();
    }

    public OrderedMap<String, Command> enemyUnits = new OrderedMap<String, Command>();

    public EnemyFactory(){

        enemyUnits.put(BLOB, new Command() {
            @Override
            public ComponentBag getEnemy() {
                return new DummyFactory().targetDummyWalker();
            }
        });

        enemyUnits.put(FAST_BLOB, new Command() {
            @Override
            public ComponentBag getEnemy() {
                return new DummyFactory().targetDummySprinter();
            }
        });

        enemyUnits.put(MAGE_BLOB, new Command() {
            @Override
            public ComponentBag getEnemy() {
                return new RangedDummyFactory().rangedDummy();
            }
        });

    }


    public ComponentBag get(String id){
        return enemyUnits.get(id).getEnemy();
    }















}
