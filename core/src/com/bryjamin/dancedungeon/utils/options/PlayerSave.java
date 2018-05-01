package com.bryjamin.dancedungeon.utils.options;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.bryjamin.dancedungeon.factories.map.GameMap;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;

import static com.bryjamin.dancedungeon.utils.options.DevOptions.DEV_PREFS_KEY;

public class PlayerSave {

    public final static String PLAYER_SAVE_PREFS_KEY = "player";
    private final static String FIRST_TIME_PLAYER = "First_Time_Tutorial_Prompt";


    public static boolean isFirstTimePlayer(){
        Preferences preferences = Gdx.app.getPreferences(PLAYER_SAVE_PREFS_KEY);
        return preferences.getBoolean(FIRST_TIME_PLAYER, true);
    }


    public static void toggleFirstTimePlayer(){
        Preferences preferences = Gdx.app.getPreferences(PLAYER_SAVE_PREFS_KEY);
        preferences.putBoolean(FIRST_TIME_PLAYER, !preferences.getBoolean(FIRST_TIME_PLAYER, true));
        preferences.flush();
    }


    public void getFirstTimePlayer(){
        Preferences preferences = Gdx.app.getPreferences(PLAYER_SAVE_PREFS_KEY);
        preferences.putBoolean(FIRST_TIME_PLAYER, !preferences.getBoolean(FIRST_TIME_PLAYER, true));
        preferences.flush();
    }

    public static void turnOffFirstTimePlayer(){
        Preferences preferences = Gdx.app.getPreferences(PLAYER_SAVE_PREFS_KEY);
        preferences.putBoolean(FIRST_TIME_PLAYER, false);
        preferences.flush();
    }









}
