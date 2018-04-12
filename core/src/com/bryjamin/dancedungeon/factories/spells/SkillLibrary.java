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
}
