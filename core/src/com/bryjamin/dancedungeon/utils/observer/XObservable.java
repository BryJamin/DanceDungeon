package com.bryjamin.dancedungeon.utils.observer;

import com.badlogic.gdx.utils.Array;

public class XObservable {


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
