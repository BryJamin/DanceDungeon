package com.bryjamin.dancedungeon.ecs.components.actions.interfaces;

public abstract class QueuedInstantAction implements QueuedAction {

    @Override
    public abstract void act();

    @Override
    public boolean isComplete(){
        return true;
    };
}
