package com.bryjamin.dancedungeon.utils.options;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.bryjamin.dancedungeon.factories.map.GameMap;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;

public class PlayerSave {
    public final static String PLAYER_SAVE_PREFS_KEY = "player";


    private final static String FIRST_TIME_PLAYER = "First_Time_Tutorial_Prompt";

    public final static String UTILITY_SCORE_DEBUG = "score_debug";
    public final static String PARTY_DATA = "partyData";

    public static QuickSave.SavedData savedData;


    public static boolean isFirstTimePlayer(){
        Preferences preferences = Gdx.app.getPreferences(PLAYER_SAVE_PREFS_KEY);
        return preferences.getBoolean(FIRST_TIME_PLAYER, true);
    }




    public static void toggleUtilityInfo(){
        Preferences preferences = Gdx.app.getPreferences(PLAYER_SAVE_PREFS_KEY);
        preferences.putBoolean(UTILITY_SCORE_DEBUG, !preferences.getBoolean(UTILITY_SCORE_DEBUG, true));
        preferences.flush();
    }


    public static boolean getUtilityScoreSetting(){
        Preferences preferences = Gdx.app.getPreferences(PLAYER_SAVE_PREFS_KEY);
        return preferences.getBoolean(UTILITY_SCORE_DEBUG, true);
    }


    public static QuickSave.SavedData loadSave(){
        return savedData;
    }


    public static class SavedData {

        public SavedData(GameMap gameMap, PartyDetails partyDetails) {
            this.gameMap = gameMap;
            this.partyDetails = partyDetails;
        }

        GameMap gameMap;
        PartyDetails partyDetails;

        public GameMap getGameMap() {
            return gameMap;
        }

        public PartyDetails getPartyDetails() {
            return partyDetails;
        }
    }









}
