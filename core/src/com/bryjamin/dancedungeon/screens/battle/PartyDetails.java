package com.bryjamin.dancedungeon.screens.battle;

import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.factories.spells.Skill;


/**
 * Created by BB on 17/12/2017.
 */

public class PartyDetails {

    public static int MAX_MORALE = 10;
    public static int MAX_INVENTORY = 4;
    public static int PARTY_SIZE = 3;

    public int money = 10;
    public int grenades;
    public int medicalSupplies;
    public int skillPoints;
    public int morale = 5;

    private UnitData[] party = new UnitData[PARTY_SIZE];


    private Array<Skill> skillInventory = new Array<Skill>();

    public PartyDetails(){
       // skillInventory.add(new StunStrike());
    }

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

    public void changeMoney(int money){

        this.money += money;

        if(this.money < 0) this.money = 0;
        //if(this.morale > ) this.morale = MAX_MORALE;

    }



    public void addSkillToInventory(Skill s){
        skillInventory.add(s);
    }

    public void removeSkillFromInventoryOrParty(Skill s){

        skillInventory.removeValue(s, true);

        for(UnitData unitData : party){
            unitData.getSkillsComponent().skills.removeValue(s, true);
        }
    }

    public Array<Skill> getSkillInventory() {
        return skillInventory;
    }

    public Array<Skill> getEquippedInventory(){

        Array<Skill> equipped = new Array<Skill>();

        for(UnitData unitData : party){
            equipped.addAll(unitData.getSkillsComponent().skills);
        }

        return equipped;

    }

}
