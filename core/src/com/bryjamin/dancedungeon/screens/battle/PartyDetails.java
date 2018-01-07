package com.bryjamin.dancedungeon.screens.battle;

import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.factories.player.Unit;

/**
 * Created by BB on 17/12/2017.
 */

public class PartyDetails {

    //Max party size is 4
    private Array<Unit> playerParty = new Array<Unit>();
    public Array<Unit> getPlayerParty() {
        return playerParty;
    }
    public void setPlayerParty(Array<Unit> playerParty) {
        this.playerParty = playerParty;
    }

}
