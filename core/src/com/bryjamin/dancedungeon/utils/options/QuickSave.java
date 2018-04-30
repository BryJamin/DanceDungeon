package com.bryjamin.dancedungeon.utils.options;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.bryjamin.dancedungeon.assets.Prefs;
import com.bryjamin.dancedungeon.factories.map.GameMap;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;

public class QuickSave {

    public final static String MAP_DATA = "map";
    public final static String PARTY_DATA = "partyData";

    public static SavedData savedData;


    public static void quickSave(GameMap gameMap, PartyDetails partyDetails){

        Json json = new Json();
        Preferences prefs = Gdx.app.getPreferences(Prefs.QUICK_SAVE_PREF);
        prefs.putString(MAP_DATA, json.toJson(gameMap, GameMap.class));
        prefs.putString(PARTY_DATA, json.toJson(partyDetails, PartyDetails.class));
        prefs.flush();

    }


    public static void clear(){
        Preferences prefs = Gdx.app.getPreferences(Prefs.QUICK_SAVE_PREF);
        prefs.clear();
        prefs.flush();
    }

    public static boolean isThereAValidQuickSave(){

        Preferences prefs = Gdx.app.getPreferences(Prefs.QUICK_SAVE_PREF);
        Json json = new Json();

        if(prefs.contains(MAP_DATA) && prefs.contains(PARTY_DATA)){

            try {
                GameMap gameMap = json.fromJson(GameMap.class, prefs.getString(MAP_DATA));
                PartyDetails partyDetails = json.fromJson(PartyDetails.class, prefs.getString(PARTY_DATA));
                savedData = new SavedData(gameMap, partyDetails);
                return true;
            } catch (Exception e){
                e.printStackTrace();
            }


        }

        prefs.clear();
        prefs.flush();//Destroy invalid quick saves
        return false;
    }



    public static SavedData loadSave(){
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
