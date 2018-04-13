package com.bryjamin.dancedungeon.factories.spells;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by BB on 27/01/2018.
 */

public class SkillLibrary {

    // Hashmap where we store our items as "item name"-"item" pairs
    private ObjectMap<String, Skill> items = new ObjectMap<String, Skill>();

    public SkillLibrary() {
        loadItemsFromJSON();
    }

    private void loadItemsFromJSON() {
        Json json = new Json();
        items = json.fromJson(ObjectMap.class, Gdx.files.internal("json/playerskills.json"));
    }

    public ObjectMap<String, Skill> getItems() {
        return items;
    }

    public Skill getSkill(String key){
        return items.get(key);
    }


    public static String SKILL_HEAVY_STRIKE = "a5f2cd73-7ade-4577-8d7b-862299baf774";
    public static String SKILL_HEAVIER_STRIKE = "d92aa63f-1f4d-40bf-9dca-344cebd78d31";


}
