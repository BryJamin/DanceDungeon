package com.bryjamin.dancedungeon.utils;

import com.badlogic.gdx.math.Rectangle;

/**
 * Created by BB on 15/10/2017.
 */

public class HitBox {

    public Rectangle hitbox;
    public float offsetX;
    public float offsetY;

    public HitBox(Rectangle hitbox){
        this.hitbox = hitbox;
        this.offsetX = 0;
        this.offsetY = 0;
    }

    public HitBox(Rectangle hitbox, float offsetX, float offsetY){
        this.hitbox = hitbox;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

}
