package com.bryjamin.dancedungeon.ecs.components.actions;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;

/**
 * Created by BB on 19/10/2017.
 */

public class ActionOnTapComponent extends Component {

    public Array<WorldAction> actions = new Array<WorldAction>();

    public boolean enabled = true;


    private float touchX;
    private float touchY;

    public ActionOnTapComponent(){};

    public ActionOnTapComponent(WorldAction... actions){
        this.actions.addAll(actions);
    }

    public void setTouchX(float touchX) {
        this.touchX = touchX;
    }

    public void setTouchY(float touchY) {
        this.touchY = touchY;
    }

    public float getTouchX() {
        return touchX;
    }

    public float getTouchY() {
        return touchY;
    }
}
