package com.bryjamin.dancedungeon.screens.battle;

import com.bryjamin.dancedungeon.factories.player.Unit;

/**
 * Created by BB on 17/12/2017.
 */

public class PartyDetails {

    public int money;
    public int grenades;
    public int medicalSupplies;

    private Unit[] party = new Unit[4];

    public void addPartyMember(Unit unit, int position){
        if(position - 1 > party.length) throw new RuntimeException("Not place for the party member");
        party[position] = unit;
    };

    public Unit[] getParty() {
        return party;
    }
}
