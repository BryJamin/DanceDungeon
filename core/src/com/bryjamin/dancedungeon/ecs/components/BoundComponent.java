package com.bryjamin.dancedungeon.ecs.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by BB on 19/10/2017.
 */

public class BoundComponent extends Component {

    public Rectangle bound = new Rectangle();

    public BoundComponent(){}

    public BoundComponent(Rectangle bound){
        this.bound = bound;
    }


}
