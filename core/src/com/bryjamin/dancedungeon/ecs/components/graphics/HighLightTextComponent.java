package com.bryjamin.dancedungeon.ecs.components.graphics;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.utils.texture.Highlight;

/**
 * Created by BB on 24/01/2018.
 */

public class HighLightTextComponent extends Component {

    public Array<Highlight> highLights = new Array<Highlight>();

    public HighLightTextComponent(){}

    public HighLightTextComponent(Highlight... highlights){
        this.highLights.addAll(highlights);
    }




}
