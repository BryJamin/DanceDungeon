package com.bryjamin.dancedungeon.factories.enemy;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.ai.ActionScoreCalculator;
import com.bryjamin.dancedungeon.ecs.ai.UtilityAiCalculator;
import com.bryjamin.dancedungeon.ecs.ai.actions.BasicAttackAction;
import com.bryjamin.dancedungeon.ecs.ai.actions.EndTurnAction;
import com.bryjamin.dancedungeon.ecs.ai.actions.FindBestMovementAreaToAttackFromAction;
import com.bryjamin.dancedungeon.ecs.ai.calculations.CanMoveCalculator;
import com.bryjamin.dancedungeon.ecs.ai.calculations.CanUseSkillCalculator;
import com.bryjamin.dancedungeon.ecs.ai.calculations.IsNextToCalculator;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.UtilityAiComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationMapComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationStateComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.factories.player.UnitFactory;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.SkillLibrary;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

public class UnitLibrary {

    private static final String ENEMY_UNITS_FILE = "json/units/enemies.json";
    private static final String CHARACTER_UNITS_FILE = "json/units/characters.json";


    private static final OrderedMap<String, UnitData> enemies = new OrderedMap<>();

    public static void loadFromJSON(){

        enemies.clear();

        Json json = new Json();
        json.setIgnoreUnknownFields(true);


        Array<UnitData> array = json.fromJson(Array.class, Gdx.files.internal(ENEMY_UNITS_FILE));
        array.addAll(json.fromJson(Array.class, Gdx.files.internal(CHARACTER_UNITS_FILE)));

        for(UnitData unitData : array){
            if(enemies.containsKey(unitData.getId())) {
                throw new RuntimeException("Duplicate key found in Unit Library: " + unitData.getId() + "\n" +
                "Unit Name: " + unitData.getName());
            }

            enemies.put(unitData.getId(), unitData);
        }


    }



    public static UnitData getUnitData(String id){

        if(enemies.containsKey(id)){
            return new UnitData(enemies.get(id));
        } else {
            throw new IllegalArgumentException("Enemy ID:" + id + " does not exist");
        }
    }


    public static Entity getEnemyUnit(World world, String id){

        Entity e = getUnit(world, id);
        e.edit().add(new EnemyComponent());
        e.edit().add(new UtilityAiComponent());
        return e;

    }


    public static Entity getPlayerUnit(World world, String id){

        Entity e = getUnit(world, id);
        e.edit().add(new PlayerControlledComponent());

        return e;

    }


    public static Entity convertUnitDataIntoEntity(World world, UnitData unitData){

        UnitFactory unitFactory = new UnitFactory();
        Entity unit = unitFactory.baseUnitBag(world, unitData);


        int STANDING_ANIMATION = 23;
        unit.edit().add(new AnimationStateComponent(STANDING_ANIMATION));
        unit.edit().add(new AnimationMapComponent()
                .put(STANDING_ANIMATION, unitData.icon, 0.6f, Animation.PlayMode.LOOP));

        return unit;


    }


    public static Entity convertUnitDataIntoPlayerEntity(World world, UnitData unitData){
        Entity e = convertUnitDataIntoEntity(world, unitData);
        e.edit().add(new PlayerControlledComponent());
        return e;
    }


    private static Entity getUnit(World world, String id){
        UnitData unitData = getUnitData(id);
        return convertUnitDataIntoEntity(world, unitData);

    }

    public static final String MELEE_BLOB = "cf5db9a9-b053-4de8-ad17-4f56a1e008f6";
    public static final String RANGED_BLASTER = "7720994b-263a-439d-b83c-70586bb63777";


    public static final String CHARACTERS_SGT_SWORD = "2ade2064-eaf1-4a63-8ba4-1fd98b72c0dc";
    public static final String CHARACTERS_BOLAS = "98be151b-d019-49c3-9514-defb59f26d0c";
    public static final String CHARACTERS_FIRAS = "b0a19b01-cf75-4b77-b6e5-0cb7dcb5afe9";



}
