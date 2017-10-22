package com.bryjamin.dancedungeon.factories.decor;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.factories.AbstractFactory;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;
import com.bryjamin.dancedungeon.utils.texture.Layer;


/**
 * Created by BB on 18/10/2017.
 */

public class FloorFactory extends AbstractFactory {


    public FloorFactory(AssetManager assetManager) {
        super(assetManager);
    }



    public ComponentBag createFloor(float x, float y, float width, float height, int rows, int columns){

        ComponentBag bag = new ComponentBag();
        bag.add(new PositionComponent(x, y));


        float tileWidthSize = width / columns;
        float tileHeightSize = height / rows;


        DrawableDescription.DrawableDescriptionBuilder descriptionBuilder = new DrawableDescription.DrawableDescriptionBuilder(TextureStrings.BLOCK)
                .height(tileHeightSize)
                .width(tileWidthSize);

        DrawableComponent drawableComponent = new DrawableComponent(Layer.BACKGROUND_LAYER_FAR);
        bag.add(drawableComponent);

        for(int i = 0; i < columns; i++) {

            for(int j = 0; j < rows; j++){

                Color color1 = j % 2 != 0 ? new Color(Color.LIGHT_GRAY) : new Color(Color.FIREBRICK);
                Color color2 = j % 2 != 0 ? new Color(Color.FIREBRICK) : new Color(Color.LIGHT_GRAY);


                drawableComponent.drawables.add(descriptionBuilder
                        .offsetX(i * tileWidthSize)
                        .offsetY(j * tileHeightSize)
                        .color(i % 2 != 0 ? color1 : color2)
                        .build());

            }
        }





        return bag;


    }







}
