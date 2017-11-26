package com.bryjamin.dancedungeon.utils;

import com.bryjamin.dancedungeon.MainGame;

/**
 * Created by BB on 09/10/2017.
 *
 * Used for all size values in the game for scalability
 *
 */

public class Measure {


    private static final float unit = MainGame.GAME_UNITS;

    /**
     * Multiples the given number by the standard unit of measurement being used in the application
     * @param i - Number to be multiplied
     * @return - The given number multiplied by the unit of measurement
     */
    public static float units(float i){
        return unit * i;
    }

}



