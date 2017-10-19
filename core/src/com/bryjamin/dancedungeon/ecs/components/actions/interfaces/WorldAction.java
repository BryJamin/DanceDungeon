package com.bryjamin.dancedungeon.ecs.components.actions.interfaces;

import com.artemis.Entity;
import com.artemis.World;

/**
 * Created by BB on 19/10/2017.
 */

public interface WorldAction {

    void performAction(World world, Entity entity);



}

