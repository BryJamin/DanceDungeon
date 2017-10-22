package com.bryjamin.dancedungeon.ecs.components.actions.interfaces;

import com.artemis.Entity;
import com.artemis.World;

/**
 * Created by BB on 22/10/2017.
 */

public class EmptyTask implements WorldTask{
    @Override
    public void performAction(World world, Entity entity) {

    }

    @Override
    public void cleanUpAction(World world, Entity e) {

    }
}
