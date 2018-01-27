package com.bryjamin.dancedungeon.factories;

import com.artemis.Entity;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextDescription;


/**
 * Created by BB on 23/01/2018.
 */

public class TextFactory {



    public static Entity createCenteredText(Entity e, String font, String text, float x, float y, float width, float height){

        e.edit().add(new PositionComponent(x, y));
        e.edit().add(new DrawableComponent(Layer.ENEMY_LAYER_MIDDLE,
                new TextDescription.Builder(font)
                        .text(text)
                        .width(width)
                        .height(height)
                        .build()));
        return e;
    }








}
