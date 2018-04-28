package com.bryjamin.dancedungeon.factories.player;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.SkillLibrary;
import com.bryjamin.dancedungeon.utils.Measure;

/**
 * Created by BB on 22/12/2017.
 *
 * Used to store the type of unit that can be created
 * As well as the stats and skills of the unit.
 *
 */

public class UnitData implements Json.Serializable {

    public static int MAXIMUM_SKILLS = 2;

    public String id = UnitMap.UNIT_WARRIOR;
    public String icon = TextureStrings.CLASS_CYRONAUT;
    public String name = "Jeff";

    //STATS
    private int health;
    private int maxHealth;

    private int movementRange;
    private int attackRange;

    private float mapMovementSpeed = Measure.units(60f);


    public StatComponent statComponent = new StatComponent();
    private Array<Skill> skills = new Array<>();

    public UnitData(){}





    public UnitData(UnitData unitData) {
        this.id = unitData.id;
        this.icon = unitData.icon;
        this.name = unitData.name;
        this.health = unitData.health;
        this.maxHealth = unitData.maxHealth;
        this.movementRange = unitData.movementRange;
        this.attackRange = unitData.attackRange;
        this.statComponent = unitData.statComponent;

        for(Skill s : unitData.getSkills()){
            this.skills.add(new Skill(s));
        }
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

    public StatComponent getStatComponent() {
        return statComponent;
    }

    public void setStatComponent(StatComponent statComponent) {
        this.statComponent = statComponent;
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
                //System.out.println(skills.get(i));
                this.skills.add(SkillLibrary.getSkill(skills.get(i).asString()));
            }
        }

        //In save games health may be less than max, But for newly loaded data health should be set to max
        this.health = jsonData.hasChild("health") ? this.health : this.maxHealth;
        statComponent.health = this.health;

        statComponent.maxHealth = this.maxHealth;
        statComponent.movementRange = this.movementRange;
        statComponent.attackRange = this.attackRange;

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
}
