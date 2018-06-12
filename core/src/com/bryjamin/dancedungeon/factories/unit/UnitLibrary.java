package com.bryjamin.dancedungeon.factories.unit;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.OrderedMap;
import com.bryjamin.dancedungeon.ecs.components.actions.UtilityAiComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationMapComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationStateComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;

/**
 * Library for loading in all units in the game. Both player and enemy units.
 *
 * Also converts UnitData into Entities.
 */
public class UnitLibrary {

    private static final String ENEMY_UNITS_FILE = "json/units/enemies.json";
    private static final String CHARACTER_UNITS_FILE = "json/units/characters.json";


    private static final OrderedMap<String, UnitData> unitList = new OrderedMap<>();

    private static final Array<String> unitIdList = new Array<>();
    private static final Array<String> enemyIdList = new Array<>();



    private static String add(String id){
        unitIdList.add(id);
        return id;
    }


    private static String addEnemy(String id){
        enemyIdList.add(id);
        return add(id);
    }

    public static void loadFromJSON(){

        unitList.clear();

        Json json = new Json();
        json.setIgnoreUnknownFields(true);

        String[] fileArray = new String[]{
                ENEMY_UNITS_FILE,
                CHARACTER_UNITS_FILE
        };

        for(String file : fileArray){

            Array<UnitData> array = json.fromJson(Array.class, Gdx.files.internal(file));

            for(UnitData unitData : array){
                if(unitList.containsKey(unitData.getId())) {
                    throw new RuntimeException("Duplicate key found in Unit Library: " + unitData.getId() + "\n" +
                            "Unit Name: " + unitData.getName()  + "\n" +
                            "File Name: " + file);
                }
                unitList.put(unitData.getId(), unitData);
            }


        }


    }



    public static UnitData getUnitData(String id){

        if(unitList.containsKey(id)){
            return new UnitData(unitList.get(id));
        } else {
            throw new IllegalArgumentException("Unit ID:" + id + " does not exist");
        }
    }


    public static Entity getEnemyUnit(World world, String id){

        Entity e = getUnit(world, id);
        e.edit().add(new EnemyComponent());
        e.edit().add(new UtilityAiComponent());
        return e;

    }

    public static String getRandomEnemyUnitID(){
        return enemyIdList.random();

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

    public static Array<String> getUnitIdList() {
        return unitIdList;
    }


    static {

        //Enemies
        MELEE_BLOB = addEnemy("cf5db9a9-b053-4de8-ad17-4f56a1e008f6");
        RANGED_BLASTER = addEnemy("7720994b-263a-439d-b83c-70586bb63777");
        RANGED_LOBBA = addEnemy("925dcc29-c81c-4abc-9bd3-adee4b8e636a");
        //TODO Infuture I think I need to add a 'Difficulty Rating' of sorts for enemies that
        //TODO can just be randomly selected.


        BIG_BLASTER_BOSS = add("1e08e0ff-77ec-4a26-b75c-0eeca3ea86bd");

        CHARACTERS_SGT_SWORD = add("2ade2064-eaf1-4a63-8ba4-1fd98b72c0dc");
        CHARACTERS_BOLAS = add("98be151b-d019-49c3-9514-defb59f26d0c");
        CHARACTERS_FIRAS = add("b0a19b01-cf75-4b77-b6e5-0cb7dcb5afe9");

        CHARACTERS_HIRAN = add("d33c5918-461c-48fd-9005-d9f2713434d9");
        CHARACTERS_SWITCH = add("72b24678-2468-4394-9ff5-cd5dc0d08922");
        CHARACTERS_WANDA = add("dda34ab5-b1ba-4e6c-bf5f-1d6c349cf888");

    }

    public static final String MELEE_BLOB;
    public static final String RANGED_BLASTER;
    public static final String RANGED_LOBBA;
    public static final String BIG_BLASTER_BOSS;


    public static final String CHARACTERS_SGT_SWORD;
    public static final String CHARACTERS_BOLAS;
    public static final String CHARACTERS_FIRAS;
    public static final String CHARACTERS_HIRAN;
    public static final String CHARACTERS_SWITCH;
    public static final String CHARACTERS_WANDA;



}
