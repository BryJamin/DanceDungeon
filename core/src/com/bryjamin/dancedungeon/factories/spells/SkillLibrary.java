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

    static { //TODO Is this safe? Research it.
        Json json = new Json();
        items = json.fromJson(ObjectMap.class, Gdx.files.internal("json/playerskills.json"));
    }

    public ObjectMap<String, Skill> getItems() {
        return items;
    }

    public static Skill getSkill(String key){
        return items.get(key);
    }


    public static final String SKILL_HEAVY_STRIKE = "a5f2cd73-7ade-4577-8d7b-862299baf774";
    public static final String SKILL_HEAVIER_STRIKE = "d92aa63f-1f4d-40bf-9dca-344cebd78d31";
    public static final String SKILL_HOOK_SHOT = "cd8ced23-790b-41cd-99dd-f884abccc003";
    public static final String SKILL_STRAIGHT_SHOT = "0ccac808-557a-4956-b77b-e696d849da68";


}
