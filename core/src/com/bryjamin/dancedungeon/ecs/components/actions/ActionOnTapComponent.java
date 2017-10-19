package com.bryjamin.dancedungeon.ecs.components.actions;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;

/**
 * Created by BB on 19/10/2017.
 */

public class ActionOnTapComponent extends Component {

    public Array<WorldAction> actions = new Array<WorldAction>();

    public ActionOnTapComponent(){};

    public ActionOnTapComponent(WorldAction... actions){
        this.actions.addAll(actions);
    }


}
