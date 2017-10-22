package com.bryjamin.dancedungeon.ecs.components.actions.interfaces;

import com.artemis.Entity;
import com.artemis.World;

/**
 * Created by BB on 22/10/2017.
 */

public interface WorldCondition {
    boolean condition(World world, Entity entity);
}
