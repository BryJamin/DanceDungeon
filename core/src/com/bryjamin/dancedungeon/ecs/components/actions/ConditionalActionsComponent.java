package com.bryjamin.dancedungeon.ecs.components.actions;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;

/**
 * Created by BB on 01/11/2017.
 */

public class ConditionalActionsComponent extends Component {

    public Array<WorldConditionalAction> conditionalActions = new Array<WorldConditionalAction>();

    public ConditionalActionsComponent(){};

    public ConditionalActionsComponent(WorldConditionalAction...  worldConditionalActions){
        conditionalActions.addAll(worldConditionalActions);
    }


}
