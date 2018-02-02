package com.bryjamin.dancedungeon.ecs.components.graphics;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextDescription;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 10/10/2017.
 * <p>
 * Used for entities that are drawn on screen
 */

public class DrawableComponent extends Component {

    public int layer = Layer.ENEMY_LAYER_MIDDLE;
    public DrawableDescription drawables;


    public DrawableComponent() {
        drawables = new TextureDescription.Builder(TextureStrings.BLOCK).build();
    }

    public DrawableComponent(int layer, DrawableDescription drawableDescriptions) {
        this.layer = layer;
        this.drawables = drawableDescriptions;
    }


    public DrawableComponent(DrawableComponent dc) {
        this.layer = dc.layer;
        if (dc.drawables instanceof TextureDescription) {
            this.drawables = new TextureDescription(new TextureDescription.Builder((TextureDescription) dc.drawables));
        } else if (dc.drawables instanceof TextDescription) {
            this.drawables = new TextDescription(new TextDescription.Builder((TextDescription) dc.drawables));
        }
    }


    public void setColor(Color color) {
        drawables.getColor().set(color);
    }


}

