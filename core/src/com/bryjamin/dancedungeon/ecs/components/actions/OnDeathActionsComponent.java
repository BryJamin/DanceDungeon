package com.bryjamin.dancedungeon.ecs.components.actions;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;

/**
 * Created by BB on 01/11/2017.
 *
 * Component for allowing an Entity to perform an Action upon being deleted via the DeathSystem
 */

public class OnDeathActionsComponent extends Component {

    public Array<WorldAction> actions = new Array<WorldAction>();

    public OnDeathActionsComponent(){};

    public OnDeathActionsComponent(WorldAction... worldActions){
        this.actions.addAll(worldActions);
    }

}
