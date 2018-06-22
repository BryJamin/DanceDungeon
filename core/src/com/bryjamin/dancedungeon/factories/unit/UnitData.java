package com.bryjamin.dancedungeon.factories.unit;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.SkillLibrary;

/**
 * Created by BB on 22/12/2017.
 *
 * Used to store the type of unit that can be created
 * As well as the stats and skills of the unit.
 *
 */

public class UnitData implements Json.Serializable {

    public static int MAXIMUM_SKILLS = 2;

    public String id = UnitLibrary.MELEE_BLOB;
    public String icon = TextureStrings.BLOCK;
    public String name = "Jeff";

    //STATS
    private int health;
    private int maxHealth;

    private int accumulatedDamage;
    private int accumulatedHealing;

    private int movementRange;
    private int attackRange;

    public int stun;

    private float mapMovementSpeed = 60f;
    private float drawScale = 0.65f;

    private Array<Skill> skills = new Array<>();

    public UnitData(){}


    public UnitData(UnitData unitData) {
        copyUnitDataAttributes(unitData);

        for(Skill s : unitData.getSkills()){
            this.skills.add(new Skill(s));
        }
    }

    public void copyUnitDataAttributes(UnitData unitData){
        this.id = unitData.id;
        this.icon = unitData.icon;
        this.name = unitData.name;
        this.health = unitData.health;
        this.maxHealth = unitData.maxHealth;
        this.accumulatedDamage = unitData.accumulatedDamage;
        this.accumulatedHealing = unitData.accumulatedHealing;
        this.movementRange = unitData.movementRange;
        this.attackRange = unitData.attackRange;
        this.drawScale = unitData.drawScale;
        this.mapMovementSpeed = unitData.mapMovementSpeed;
    }



    public UnitData(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Array<Skill> getSkills() {
        return skills;
    }

    @Override
    public void write(Json json) {
        json.writeFields(this);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {

        json.readFields(this, jsonData);

        if(jsonData.hasChild("skillIds")){
            JsonValue skills = jsonData.get("skillIds");

            for(int i = 0; i < skills.size; i++){
                this.skills.add(SkillLibrary.getSkill(skills.get(i).asString()));
            }
        }

        //In save games health may be less than max, But for newly loaded data health should be set to max
        this.health = jsonData.hasChild("health") ? this.health : this.maxHealth;

    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getMovementRange() {
        return movementRange;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public int getStun() {
        return stun;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public float getMapMovementSpeed() {
        return mapMovementSpeed;
    }


    public void setHealth(int health) {
        this.health = health;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }


    public float getDrawScale() {
        return drawScale;
    }

    public void changeHealth(int healthChange){

        health += healthChange;

        if(health < 0){
            health = 0;
        } else if(health > maxHealth){
            health = maxHealth;
        }

    }


    public void applyDamage(int accumulatedDamage){
        this.accumulatedDamage += accumulatedDamage;
    }

    public void applyAccumulatedDamage(){
        health = health - accumulatedDamage;
        accumulatedDamage = 0;
    }


    public void applyAccumulatedHealing(){
        health = health + getAccumulatedHealing() > maxHealth ? maxHealth : health + getAccumulatedHealing();
        accumulatedHealing = 0;
    }

    public void applyHealing(int accumulatedHealing){
        this.accumulatedHealing += accumulatedHealing;
    }

    public int getAccumulatedDamage() {
        return accumulatedDamage;
    }

    public int getAccumulatedHealing() {
        return accumulatedHealing;
    }

    public void clearDamage(){
        accumulatedDamage = 0;
    }

    public void clearHealing(){
        accumulatedHealing = 0;
    }



}
