package com.bryjamin.dancedungeon.ecs.components.graphics;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;
import com.bryjamin.dancedungeon.utils.texture.Layer;

/**
 * Created by BB on 10/10/2017.
 *
 * Used for entities that are drawn on screen
 *
 *
 *
 */

public class DrawableComponent extends Component {

    public int layer = Layer.ENEMY_LAYER_MIDDLE;
    public Array<DrawableDescription> drawables = new Array<DrawableDescription>();

    public DrawableComponent(){

    }

    public DrawableComponent(int layer, DrawableDescription... drawableDescriptions){
        this.layer = layer;
        for(DrawableDescription drawableDescription : drawableDescriptions) drawables.add(drawableDescription);
    }


    public DrawableDescription getDrawableDescriptionById(int id){
        for(DrawableDescription drawableDescription : drawables){
            if(drawableDescription.getIdentifier() == id){
                return drawableDescription;
            }
        }
        return new DrawableDescription.DrawableDescriptionBuilder(TextureStrings.BLOCK).build();
    }

}
