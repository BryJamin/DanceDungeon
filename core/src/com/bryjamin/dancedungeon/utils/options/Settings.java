package com.bryjamin.dancedungeon.utils.options;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.bryjamin.dancedungeon.ecs.systems.audio.MusicSystem;
import com.bryjamin.dancedungeon.ecs.systems.audio.SoundSystem;

public class Settings {

    private final static String SETTINGS_PREFS_KEY = "settings";
    private final static String MUSIC_SETTING = "music";
    private final static String SOUND_SETTING = "sound";


    public static boolean isMusicOn(){
        Preferences preferences = Gdx.app.getPreferences(SETTINGS_PREFS_KEY);
        return preferences.getBoolean(MUSIC_SETTING, true);
    }

    public static boolean isSoundOn(){
        Preferences preferences = Gdx.app.getPreferences(SETTINGS_PREFS_KEY);
        return preferences.getBoolean(SOUND_SETTING, true);
    }

    public static void toggleMusic(){
        Preferences preferences = Gdx.app.getPreferences(SETTINGS_PREFS_KEY);
        preferences.putBoolean(MUSIC_SETTING, !preferences.getBoolean(MUSIC_SETTING, true));
        preferences.flush();
        MusicSystem.MUSIC_ON = preferences.getBoolean(MUSIC_SETTING, true);
    }

    public static void toggleSound(){
        Preferences preferences = Gdx.app.getPreferences(SETTINGS_PREFS_KEY);
        preferences.putBoolean(SOUND_SETTING, !preferences.getBoolean(SOUND_SETTING, true));
        preferences.flush();
        SoundSystem.SOUNDON = preferences.getBoolean(SOUND_SETTING, true);
    }

}
