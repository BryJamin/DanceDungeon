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
    public static final String SPITTER_BLOB = "spitter_blob";
    public static final String BOSS_MAGE = "boss_guy";


    public interface Command {
        public ComponentBag getEnemy();
    }

    public OrderedMap<String, Command> enemyUnits = new OrderedMap<String, Command>();

    public EnemyFactory(){

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

        enemyUnits.put(SPITTER_BLOB, new Command() {
            @Override
            public ComponentBag getEnemy() {
                return new SpitterFactory().rangedDummy();
            }
        });


        enemyUnits.put(BOSS_MAGE, new Command() {
            @Override
            public ComponentBag getEnemy() {
                return new RangedDummyFactory().bossRangedDummy();
            }
        });

    }


    public ComponentBag get(String id){
        return enemyUnits.get(id).getEnemy();
    }















}
