package com.bryjamin.dancedungeon.ecs.ai.actions;

import com.artemis.Entity;
import com.artemis.World;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;

/**
 * Created by BB on 04/11/2017.
 */

public class EndTurnAction implements WorldAction {
    @Override
    public void performAction(World world, Entity entity) {
        entity.getComponent(TurnComponent.class).state = TurnComponent.State.END;
    }
}
