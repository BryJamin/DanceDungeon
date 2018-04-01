package com.bryjamin.dancedungeon.screens.battle;

import com.bryjamin.dancedungeon.factories.player.UnitData;

/**
 * Created by BB on 17/12/2017.
 */

public class PartyDetails {

    public static int MAX_MORALE = 10;

    public int money;
    public int grenades;
    public int medicalSupplies;
    public int reputation;
    public int morale = 10;

    private UnitData[] party = new UnitData[3];

    public void addPartyMember(UnitData unitData, int position){
        if(position - 1 > party.length) throw new RuntimeException("Not place for the party member");
        party[position] = unitData;
    };

    public UnitData[] getParty() {
        return party;
    }


    public void changeMorale(int morale){

        this.morale += morale;

        if(this.morale < 0) this.morale = 0;
        if(this.morale > MAX_MORALE) this.morale = MAX_MORALE;

    }
}
