package com.bryjamin.dancedungeon.factories.map.event.objectives;

import com.artemis.World;
import com.bryjamin.dancedungeon.Observer;

public abstract class AbstractObjective implements Observer {

    public enum UpdateOn {
        ENEMY_DEATH,
        END_TURN,
        MORALE_HIT
    }


    private UpdateOn[] updateOnArray;

    public AbstractObjective(UpdateOn... updateOns){
        this.updateOnArray = updateOns;
    }


    public abstract String getDescription();
    public abstract boolean isComplete(World world);

    public UpdateOn[] getUpdateOnArray() {
        return updateOnArray;
    }
}
