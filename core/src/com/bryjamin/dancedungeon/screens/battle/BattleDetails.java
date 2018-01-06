package com.bryjamin.dancedungeon.screens.battle;

import com.artemis.utils.Bag;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.factories.player.Unit;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;

/**
 * Created by BB on 17/12/2017.
 */

public class BattleDetails {

    //Max party size is 4
    private Array<Unit> playerParty = new Array<Unit>();

    private Bag<Bag<ComponentBag>> enemyParties = new Bag<Bag<ComponentBag>>();


    public Array<Unit> getPlayerParty() {
        return playerParty;
    }



    public void setPlayerParty(Array<Unit> playerParty) {
        this.playerParty = playerParty;
    }

    public void addEnemyWave(ComponentBag... enemies){

        Bag<ComponentBag> enemiesBag = new Bag<ComponentBag>();

        for(ComponentBag enemy: enemies){
            enemiesBag.add(enemy);
        }

        enemyParties.add(enemiesBag);

    }

    public Bag<Bag<ComponentBag>> getEnemyParties() {
        return enemyParties;
    }
}
