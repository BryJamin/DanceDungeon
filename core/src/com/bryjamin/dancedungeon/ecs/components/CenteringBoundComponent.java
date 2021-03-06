package com.bryjamin.dancedungeon.ecs.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by BB on 19/10/2017.
 *
 * This component is used for centering calculations.
 *
 * E.G if you give a entity this boundary text can be centered ontop of the entity.
 */

public class CenteringBoundComponent extends Component {

    public Rectangle bound = new Rectangle();

    public CenteringBoundComponent(){}

    public CenteringBoundComponent(Rectangle bound){
        this.bound = bound;
    }

    public CenteringBoundComponent(CenteringBoundComponent cbc){
        this(new Rectangle(cbc.bound));
    }

    public CenteringBoundComponent(float width, float height){
        this.bound = new Rectangle(0, 0, width, height);
    }

}
