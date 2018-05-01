package com.bryjamin.dancedungeon.ecs.components.actions;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;

/**
 * Created by BB on 19/10/2017.
 *
 * Component that when attached to an Entity will allow the Entity to perform actions when tapped.
 *
 * Provided the Entity has a HitboxComponent
 */

public class ActionOnTapComponent extends Component {

    public Array<WorldAction> actions = new Array<WorldAction>();

    public boolean enabled = true;

    public ActionOnTapComponent(){};

    public ActionOnTapComponent(WorldAction... actions){
        this.actions.addAll(actions);
    }

}
