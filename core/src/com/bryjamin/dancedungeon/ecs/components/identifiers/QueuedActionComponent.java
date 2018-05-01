package com.bryjamin.dancedungeon.ecs.components.identifiers;

import com.artemis.Component;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;

/**
 * Created by BB on 23/12/2017.
 *
 * Used an identifier to see if an Entity has a queued Action or not.
 *
 * For the {@link com.bryjamin.dancedungeon.ecs.systems.battle.ActionQueueSystem}
 *
 */

public class QueuedActionComponent extends Component{
    public QueuedActionComponent(){}
}
