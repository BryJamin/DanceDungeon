package com.bryjamin.dancedungeon.utils.observer;

import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem;

/**
 * Custom implementation of observer pattern.
 *
 * This class keeps track of an array of observers
 */
public class Observable {


    private Array<Observer> observers = new Array<>();

    public void addObserver(Observer o){
        this.observers.add(o);
    }

    public void removeObserver(Observer o){
        observers.removeValue(o, true);
    }

    public void notifyObservers(Object object) {
        for(Observer o : observers){
            o.update(object);
        }
    }
}
