package com.bryjamin.dancedungeon.assets;

import com.badlogic.gdx.utils.Array;

/**
 * Created by BB on 10/10/2017.
 */

@SuppressWarnings("HardCodedStringLiteral")
public class TextureStrings {

    private static final Array<String> allTextures = new Array<>();

    static {

        BLOCK = add("blocktiny");
        SPAWNER = add("decor/teleporter");
        TARGETING = add("player/targeting");

        WALL = add("decor/wall");
        ROCK_TILE = add("decor/rock");

        BORDER = add("border");

        WORLD_MAP = add("map");

        SKILLS_SLASH = add("skills/slash");
        SKILLS_HEAL = add("skills/Heal");

        SETTINGS_MUSIC_ON = add("icons/music");
        SETTINGS_MUSIC_OFF = add("icons/music_off");

        SETTINGS_SOUND_ON = add("icons/sound");
        SETTINGS_SOUND_OFF = add("icons/sound_off");


        ICON_COMBAT = add("icons/icon_combat");
        ICON_REST = add("icons/icon_rest");
        ICON_MONEY = add("icons/icon_money");
        ICON_ARROW = add("icons/arrow");
        ICON_WARNING = add("icons/warning");


    }

    private static String add(String id){
        allTextures.add(id);
        return id;
    }

    public static final String BLOCK;
    public static final String SPAWNER;
    public static final String TARGETING;
    public static final String WALL;
    public static final String ROCK_TILE;
    public static final String BORDER;
    public static final String WORLD_MAP;
    public static final String SKILLS_SLASH;
    public static final String SKILLS_HEAL;


    public static final String SETTINGS_MUSIC_ON;
    public static final String SETTINGS_MUSIC_OFF;

    public static final String SETTINGS_SOUND_ON;
    public static final String SETTINGS_SOUND_OFF;

    public static final String ICON_COMBAT;
    public static final String ICON_REST;
    public static final String ICON_MONEY;
    public static final String ICON_ARROW;
    public static final String ICON_WARNING;

    public static Array<String> getAllTextures() {
        return allTextures;
    }
}
