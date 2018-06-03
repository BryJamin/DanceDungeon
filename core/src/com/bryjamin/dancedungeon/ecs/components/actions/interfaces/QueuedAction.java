package com.bryjamin.dancedungeon.ecs.components.actions.interfaces;

/**
 * Used for the QueuedActionSystem
 */
public interface QueuedAction {

    void act();

    boolean isComplete();

}
