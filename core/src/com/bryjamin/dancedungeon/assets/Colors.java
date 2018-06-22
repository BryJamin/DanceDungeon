package com.bryjamin.dancedungeon.assets;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by BB on 17/10/2017.
 */

public class Colors {

    public static final Color AMOEBA_FAST_PURPLE = RGBtoColor(98, 59, 206, 1);
    public static final Color ENEMY_BULLET_COLOR = new Color(Color.RED);

    public static final Color PLAYER_BULLET_COLOR = new Color(Color.WHITE);

    public static final Color BOMB_RED = RGBtoColor(246, 45f, 45f, 1);
    public static final Color BOMB_ORANGE = RGBtoColor(255f, 124f, 0f, 1);
    public static final Color BOMB_YELLOW = RGBtoColor(249f, 188f, 4f, 1);

    public static final Color UI_ATTACK_TILE_COLOR = RGBtoColor(221, 0, 72, 0.3f);
    public static final Color UI_ATTACK_TILE_BORDER_COLOR = RGBtoColor(221, 0, 72, 1); //RGBtoColor(255f, 0f, 0f, 1);

    public static final Color UI_MOVEMENT_TILE_COLOR = RGBtoColor(64, 224, 208, 0.1f); //RGBtoColor(255f, 0f, 0f, 1);
    public static final Color UI_MOVEMENT_TILE_BORDER_COLOR = RGBtoColor(0, 255, 255, 1); //RGBtoColor(255f, 0f, 0f, 1);

    public static final Color UI_DEPLOYMENT_TILE_COLOR = RGBtoColor(39, 250, 0, 0.1f); //RGBtoColor(255f, 0f, 0f, 1);
    public static final Color UI_DEPLOYMENT_TILE_BORDER_COLOR = RGBtoColor(57, 255, 20, 1); //RGBtoColor(255f, 0f, 0f, 1);

    public static final Color ENEMY_INTENT_ARROW_COLOR = RGBtoColor(255f, 0f, 0f, 1);;
    public static final Color ENEMY_INTENT_HIGHLIGHT_BOX_COLOR = RGBtoColor(255, 69, 0f, 1); //RGBtoColor(255f, 0f, 0f, 1);

    public static final Color HEATH_BAR_COLOR = RGBtoColor(50,205,50, 1); //RGBtoColor(255f, 0f, 0f, 1);

    public static final Color TUTORIAL_TABLE_OUTLINE = RGBtoColor(57,255,20, 1); //RGBtoColor(255f, 0f, 0f, 1);


    public static final Color TABLE_BORDER_COLOR = RGBtoColor(39, 226, 255, 1);
    public static final Color TABLE_BORDER_COLOR_HIGHLIGHTED = RGBtoColor(1, 1, 1, 1);


    public static final Color DAMAGE_TEXT_COLOR = RGBtoColor(237, 67, 55, 1);


    public static Color RGBtoColor(float r, float g, float b, float a){
        return new Color(r / 255f, g / 255f, b / 255f, a);
    }



}
