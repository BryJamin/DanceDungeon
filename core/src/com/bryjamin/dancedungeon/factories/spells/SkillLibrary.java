package com.bryjamin.dancedungeon.factories.spells;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by BB on 27/01/2018.
 */

public class SkillLibrary {

    // Map where we store our playerSkills as "item id"-"item" pairs
    private static ObjectMap<String, Skill> playerSkills;
    private static ObjectMap<String, Skill> enemySkills;
    private static ObjectMap<String, Skill> skills;

    private static final Array<String> skillIDList = new Array<>();

    public static void loadFromJSON(){

        Json json = new Json();
        playerSkills = json.fromJson(ObjectMap.class, Gdx.files.internal("json/playerskills.json"));
        enemySkills = json.fromJson(ObjectMap.class, Gdx.files.internal("json/enemyskills.json"));


        skills = new ObjectMap<>();

        skills.putAll(playerSkills);
        skills.putAll(enemySkills);

        for(String s : skills.keys().toArray()){
            skills.put(skills.get(s).getName(), skills.get(s));
        }


    }
    public ObjectMap<String, Skill> getItems() {
        return playerSkills;
    }

    //Return a new Skill object to avoid any potential over-writes of the playerSkills within the static array
    public static Skill getSkill(String key) {

        if(skills.containsKey(key)){
            return new Skill(skills.get(key));
        } else {
            throw new SkillNotFoundException(key);
        }
    }



    public static Skill getEnemySkill(String key){
        return new Skill(enemySkills.get(key));
    }

    public static void empty(){};


    public static final String SKILL_HEAVY_STRIKE;
    public static final String SKILL_HEAVIER_STRIKE;
    public static final String SKILL_HOOK_SHOT;
    public static final String SKILL_STRAIGHT_SHOT;
    public static final String SKILL_THROW_BOMB;
    public static final String SKILL_CLOBBER;
    public static final String SKILL_JAVELIN;
    public static final String SKILL_HEAVY_JAVELIN;
    public static final String SKILL_CLAW;
    public static final String SKILL_HEAVY_CLAW;


    public static final String ENEMY_SKILL_BLAST;
    public static final String ENEMY_SKILL_BIG_BLAST;
    public static final String ENEMY_SKILL_SWIPE;
    public static final String ENEMY_SKILL_THROW_ROCK;



    static {

        //Default
        SKILL_HEAVY_STRIKE = add("a5f2cd73-7ade-4577-8d7b-862299baf774");
        SKILL_HEAVIER_STRIKE = add("d92aa63f-1f4d-40bf-9dca-344cebd78d31");
        SKILL_CLOBBER = add("3fdb6885-00b3-453a-aefc-532384332329");
        ENEMY_SKILL_SWIPE = add("d3059e1f-41f5-48a1-8ff5-5cc67cea1927");

        SKILL_CLAW = add("29ba0cf5-1d07-4906-8dff-eeac727d6dd8");
        SKILL_HEAVY_CLAW = add("48fa44b1-db1f-482b-bd60-556853aeeb3b");

        //Ranged
        SKILL_HOOK_SHOT = add("cd8ced23-790b-41cd-99dd-f884abccc003");
        ENEMY_SKILL_BLAST = add("e682fd39-6f06-47f8-aa60-175065ea12cf");
        ENEMY_SKILL_BIG_BLAST = add("598cbfff-19b7-412c-9618-cde1db76617b");
        SKILL_STRAIGHT_SHOT = add("0ccac808-557a-4956-b77b-e696d849da68");

        //Aerial
        SKILL_THROW_BOMB = add("1e3e7778-14e1-4466-b1fb-b24a01723155");
        ENEMY_SKILL_THROW_ROCK = add("509d0cd7-ad54-4794-b53e-2f97bc87d8b8");

        SKILL_JAVELIN = add("1c7611bb-a5b5-4194-9888-05a68e1dcd83");
        SKILL_HEAVY_JAVELIN = add("42c6d76b-7cf7-439f-8148-554015ac283d");

    }

    private static String add(String id){
        skillIDList.add(id);
        return id;
    }

    public static Array<String> getSkillIDList() {
        return skillIDList;
    }

    private static class SkillNotFoundException extends RuntimeException {

        private String id;

        public SkillNotFoundException(String id){
            this.id = id;
        }

        @Override
        public String getMessage() {
            return "Skill not found in Library. ID: " + id;
        }
    }

}
