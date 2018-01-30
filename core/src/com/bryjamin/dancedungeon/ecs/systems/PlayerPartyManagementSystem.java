package com.bryjamin.dancedungeon.ecs.systems;

import com.artemis.BaseSystem;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;

/**
 * Created by BB on 30/01/2018.
 */

public class PlayerPartyManagementSystem extends BaseSystem {

    private PartyDetails partyDetails;

    public PlayerPartyManagementSystem(PartyDetails partyDetails){
        this.partyDetails = partyDetails;
    }


    @Override
    protected void processSystem() {

    }

    public PartyDetails getPartyDetails() {
        return partyDetails;
    }
}
