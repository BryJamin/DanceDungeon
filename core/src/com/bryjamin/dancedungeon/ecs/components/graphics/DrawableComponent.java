package com.bryjamin.dancedungeon.ecs.components.graphics;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextDescription;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

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
    public OrderedMap<Integer, DrawableDescription> trackedDrawables = new OrderedMap<Integer, DrawableDescription>();


    public DrawableComponent(){

    }

    public DrawableComponent(int layer, DrawableDescription... drawableDescriptions){
        this.layer = layer;
        for(DrawableDescription drawableDescription : drawableDescriptions) {
            if(drawableDescription.getIdentifier() >= 0){
                trackedDrawables.put(drawableDescription.getIdentifier(), drawableDescription);
            }
            drawables.add(drawableDescription);
        }
    }



    public DrawableComponent(DrawableComponent dc){

        this.layer = dc.layer;
        for(DrawableDescription dd : dc.drawables){

            if(dd instanceof TextureDescription){
                this.drawables.add(new TextureDescription(new TextureDescription.Builder((TextureDescription) dd)));
            } else if(dd instanceof TextDescription){
                this.drawables.add(new TextDescription(new TextDescription.Builder((TextDescription) dd)));
            }
        }

    }


    public DrawableDescription getDrawableDescriptionById(int id){
        for(DrawableDescription drawableDescription : drawables){
            if(drawableDescription.getIdentifier() == id){
                return drawableDescription;
            }
        }
        return null;
    }

}

