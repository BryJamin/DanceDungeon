package com.bryjamin.dancedungeon.factories.map.event.objectives;

import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.Observer;

public abstract class AbstractObjective implements Observer {

    public enum UpdateOn {
        ENEMY_DEATH,
        END_TURN,
        MORALE_HIT
    }

    private Array<Observer> observerArray = new Array<Observer>();


    private UpdateOn[] updateOnArray;

    public AbstractObjective(UpdateOn... updateOns){
        this.updateOnArray = updateOns;
    }


    public abstract String getDescription();
    public abstract boolean isComplete(World world);

    public UpdateOn[] getUpdateOnArray() {
        return updateOnArray;
    }

    public void addObserver(Observer o){
        observerArray.add(o);
    }

    @Override
    public void onNotify() {
        for(Observer o : observerArray){
            o.onNotify();
        }
    }

}
