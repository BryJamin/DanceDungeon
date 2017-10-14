package com.bryjamin.dancedungeon.utils;

import com.artemis.World;

/**
 * Created by BB on 11/10/2017.
 */

public class GameDelta {

    public static void delta(World world, float delta){
        world.setDelta(delta < 0.030f ? delta : 0.030f);
        world.process();
    }

}
