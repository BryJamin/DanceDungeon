package com.bryjamin.dancedungeon.factories.decor;

import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;


/**
 * Created by BB on 18/10/2017.
 */

public class FloorFactory  {




    public FloorFactory() {
    }



    public void createFloor(World world, float x, float y, float width, float height, int rows, int columns){

        float tileWidthSize = width / columns;
        float tileHeightSize = height / rows;


        TextureDescription.Builder descriptionBuilder = new TextureDescription.Builder(TextureStrings.FLOOR_TEXTURE_BRICK)
                .height(tileHeightSize)
                .width(tileWidthSize);

        DrawableComponent drawableComponent = new DrawableComponent(Layer.BACKGROUND_LAYER_FAR);

        for(int i = 0; i < columns; i++) {

            for(int j = 0; j < rows; j++){

                Color color1 = j % 2 != 0 ? new Color(Color.LIGHT_GRAY) : new Color(Color.SKY);
                Color color2 = j % 2 != 0 ? new Color(Color.SKY) : new Color(Color.LIGHT_GRAY);


                int index1 = j % 2 != 0 ? 0 : 6;
                int index2 = j % 2 != 0 ? 6 : 0;

                drawableComponent.drawables.add(descriptionBuilder
                        //.index(i % 2 != 0 ? index1 : index2)
                        .index(MathUtils.random(11))
                        .offsetX(i * tileWidthSize)
                        .offsetY(j * tileHeightSize)
                        .color(new Color(Color.WHITE))
                        .build());

            }
        }

        final int e = world.create();
        world.edit(e)
                .add(new PositionComponent(x, y))
                .add(drawableComponent);

    }







}
