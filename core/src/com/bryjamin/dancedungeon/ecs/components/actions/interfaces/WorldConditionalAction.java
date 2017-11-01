package com.bryjamin.dancedungeon.ecs.components.actions.interfaces;


import com.artemis.Entity;
import com.artemis.World;

/**
 * Created by BB on 01/11/2017.
 */

public interface WorldConditionalAction {

    boolean condition(World world, Entity entity);

    void performAction(World world, Entity entity);

}
