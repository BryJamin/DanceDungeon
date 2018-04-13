package com.bryjamin.dancedungeon.factories.spells;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by BB on 27/01/2018.
 */

public class SkillLibrary {

    // Hashmap where we store our items as "item name"-"item" pairs
    private static final ObjectMap<String, Skill> items;
    private static final ObjectMap<String, Skill> enemySkills;

    static { //TODO Is this safe? Research it.
        Json json = new Json();
        items = json.fromJson(ObjectMap.class, Gdx.files.internal("json/playerskills.json"));
        enemySkills = json.fromJson(ObjectMap.class, Gdx.files.internal("json/enemyskills.json"));
    }

    public ObjectMap<String, Skill> getItems() {
        return items;
    }

    //Return a new Skill object to avoid any potential over-writes of the skills within the static array
    public static Skill getSkill(String key){
        return new Skill(items.get(key));
    }

    public static Skill getEnemySkill(String key){
        return new Skill(enemySkills.get(key));
    }


    public static final String SKILL_HEAVY_STRIKE = "a5f2cd73-7ade-4577-8d7b-862299baf774";
    public static final String SKILL_HEAVIER_STRIKE = "d92aa63f-1f4d-40bf-9dca-344cebd78d31";
    public static final String SKILL_HOOK_SHOT = "cd8ced23-790b-41cd-99dd-f884abccc003";
    public static final String SKILL_STRAIGHT_SHOT = "0ccac808-557a-4956-b77b-e696d849da68";
    public static final String SKILL_THROW_BOMB = "1e3e7778-14e1-4466-b1fb-b24a01723155";
    public static final String SKILL_CLOBBER = "3fdb6885-00b3-453a-aefc-532384332329";


    public static final String ENEMY_SKILL_SWIPE = "d3059e1f-41f5-48a1-8ff5-5cc67cea1927";


}
