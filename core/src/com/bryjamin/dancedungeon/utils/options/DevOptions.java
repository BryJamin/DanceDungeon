package com.bryjamin.dancedungeon.utils.options;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class DevOptions {


    public final static String DEV_PREFS_KEY = "dev_settings";
    public final static String UTILITY_SCORE_DEBUG = "score_debug";

    public static void toggleUtilityInfo(){
        Preferences preferences = Gdx.app.getPreferences(DEV_PREFS_KEY);
        preferences.putBoolean(UTILITY_SCORE_DEBUG, !preferences.getBoolean(UTILITY_SCORE_DEBUG, false));
        preferences.flush();
    }


    public static boolean getUtilityScoreSetting(){
        Preferences preferences = Gdx.app.getPreferences(DEV_PREFS_KEY);
        return preferences.getBoolean(UTILITY_SCORE_DEBUG, false);
    }

}
