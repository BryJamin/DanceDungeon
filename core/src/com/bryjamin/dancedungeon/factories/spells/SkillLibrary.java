package com.bryjamin.dancedungeon.factories.spells;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by BB on 27/01/2018.
 */

public class SkillLibrary {

    // Map where we store our items as "item id"-"item" pairs
    private static ObjectMap<String, Skill> items;
    private static ObjectMap<String, Skill> enemySkills;

    private static final Array<String> skillIDList = new Array<>();

    public static void loadFromJSON(){

        Json json = new Json();
        items = json.fromJson(ObjectMap.class, Gdx.files.internal("json/playerskills.json"));
        enemySkills = json.fromJson(ObjectMap.class, Gdx.files.internal("json/enemyskills.json"));


    }
    public ObjectMap<String, Skill> getItems() {
        return items;
    }

    //Return a new Skill object to avoid any potential over-writes of the skills within the static array
    public static Skill getSkill(String key) {

        if(items.containsKey(key)){
            return new Skill(items.get(key));
        } else if(enemySkills.containsKey(key)){
            return new Skill(enemySkills.get(key));
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


    public static final String ENEMY_SKILL_BLAST;
    public static final String ENEMY_SKILL_BIG_BLAST;
    public static final String ENEMY_SKILL_SWIPE;
    public static final String ENEMY_SKILL_THROW_ROCK;



    static {

        //Melee
        SKILL_HEAVY_STRIKE = add("a5f2cd73-7ade-4577-8d7b-862299baf774");
        SKILL_HEAVIER_STRIKE = add("d92aa63f-1f4d-40bf-9dca-344cebd78d31");
        SKILL_CLOBBER = add("3fdb6885-00b3-453a-aefc-532384332329");
        ENEMY_SKILL_SWIPE = add("d3059e1f-41f5-48a1-8ff5-5cc67cea1927");

        //Ranged
        SKILL_HOOK_SHOT = add("cd8ced23-790b-41cd-99dd-f884abccc003");
        ENEMY_SKILL_BLAST = add("e682fd39-6f06-47f8-aa60-175065ea12cf");
        ENEMY_SKILL_BIG_BLAST = add("598cbfff-19b7-412c-9618-cde1db76617b");
        SKILL_STRAIGHT_SHOT = add("0ccac808-557a-4956-b77b-e696d849da68");

        //Aerial
        SKILL_THROW_BOMB = add("1e3e7778-14e1-4466-b1fb-b24a01723155");
        ENEMY_SKILL_THROW_ROCK = add("509d0cd7-ad54-4794-b53e-2f97bc87d8b8");

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
