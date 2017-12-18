package com.bryjamin.dancedungeon.screens.battle;

import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;

/**
 * Created by BB on 17/12/2017.
 */

public class BattleDetails {

    //Max party size is 4
    private Array<ComponentBag> playerParty = new Array<ComponentBag>();

    private Array<ComponentBag> enemyParty = new Array<ComponentBag>();


    public Array<ComponentBag> getPlayerParty() {
        return playerParty;
    }

    public Array<ComponentBag> getEnemyParty() {
        return enemyParty;
    }


    public void setPlayerParty(Array<ComponentBag> playerParty) {
        this.playerParty = playerParty;
    }

    public void setEnemyParty(Array<ComponentBag> enemyParty) {
        this.enemyParty = enemyParty;
    }
}
