package com.bryjamin.dancedungeon.screens.battle;

import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.SkillLibrary;

import java.util.Arrays;


/**
 * Created by BB on 17/12/2017.
 */

public class PartyDetails {

    public static int MAX_MORALE = 10;
    public static int MAX_INVENTORY = 4;
    public static int PARTY_SIZE = 3;

    private int money = 3;
    public int skillPoints;
    private int morale = 5;

    private UnitData[] party = new UnitData[PARTY_SIZE];


    private Array<Skill> skillInventory = new Array<Skill>();

    public PartyDetails(){

       //skillInventory.add(SkillLibrary.getSkill(SkillLibrary.SKILL_HEAVY_STRIKE));
       // skillInventory.add(SkillLibrary.getSkill(SkillLibrary.SKILL_HEAVY_STRIKE));
        skillInventory.add(SkillLibrary.getSkill(SkillLibrary.SKILL_HEAVY_STRIKE));
        skillInventory.add(SkillLibrary.getSkill(SkillLibrary.SKILL_HEAVY_STRIKE));

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


    /**
     * Used to swap character skills with skills in inventory.
     * @param character - The party member
     */
    public void swapCharacterSkillWithInventorySkill(UnitData character, Skill characterSkill, Skill inventorySkill){

        Array<Skill> characterSkills = character.getSkillsComponent().skills;

        if(characterSkill != null && !characterSkills.contains(characterSkill, true)) return;
        if(inventorySkill != null && !skillInventory.contains(inventorySkill, true)) return;

        if(characterSkill != null && inventorySkill != null){//Swap if both slots have Skills
            characterSkills.set(characterSkills.indexOf(characterSkill, true), inventorySkill);
            skillInventory.set(skillInventory.indexOf(inventorySkill, true), characterSkill);
        } else if(characterSkill == null && inventorySkill != null){//Remove if only one slot has a skill and add to the other.
            characterSkills.add(inventorySkill);
            skillInventory.removeValue(inventorySkill, true);
        } else if(characterSkill != null && inventorySkill == null){
            skillInventory.add(characterSkill);
            characterSkills.removeValue(characterSkill, true);
        }
    }


    public int getMoney() {
        return money;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public int getMorale() {
        return morale;
    }
}
