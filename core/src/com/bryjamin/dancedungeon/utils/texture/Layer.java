package com.bryjamin.dancedungeon.utils.texture;

/**
 * Created by BB on 10/10/2017.
 *
 * The game utilises 12 different layers to draw entities onto
 *
 * These layers will also be used to draw entities beyond the ui
 *
 */

public class Layer {

    public final static int BACKGROUND_LAYER_FAR = -64;
    public final static int BACKGROUND_LAYER_MIDDLE = -32;
    public final static int BACKGROUND_LAYER_NEAR = -16;

    public final static int ENEMY_LAYER_FAR = -8;
    public final static int ENEMY_LAYER_MIDDLE = -4;
    public final static int ENEMY_LAYER_NEAR = -2;

    public final static int PLAYER_LAYER_FAR = 0;
    public final static int PLAYER_LAYER_MIDDLE = 2;
    public final static int PLAYER_LAYER_NEAR = 4;

    public final static int FOREGROUND_LAYER_FAR = 8;
    public final static int FOREGROUND_LAYER_MIDDLE = 16;
    public final static int FOREGROUND_LAYER_NEAR = 32;

}
