package com.bryjamin.dancedungeon.ecs.components.battle;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;

/**
 * Created by BB on 23/12/2017.
 *
 * Used to pause the next action within the UsesAttackAction Camera System
 *
 * If an entity exists with this, the system will not continue processing
 *
 */

public class QueuedActionComponent extends Component{

    public QueuedActionComponent(){}


    public Array<WorldConditionalAction> queuedActions = new Array<WorldConditionalAction>();

    public QueuedActionComponent(WorldConditionalAction... queuedActions){
        this.queuedActions.addAll(queuedActions);
    }

}
