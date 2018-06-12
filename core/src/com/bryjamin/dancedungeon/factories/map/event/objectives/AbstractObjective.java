package com.bryjamin.dancedungeon.factories.map.event.objectives;

import com.artemis.World;
import com.bryjamin.dancedungeon.utils.observer.Observer;
import com.bryjamin.dancedungeon.utils.observer.Observable;

public abstract class AbstractObjective implements Observer, Cloneable {

    public enum UpdateOn { //Used to show which events objectives should be listening for.
        ENEMY_DEATH,
        END_TURN,
        MORALE_HIT
    }

    public enum Reward { //Describes what reward is given upon the completion of an objective. s
        MONEY, MORALE, SKILL_POINT, RANDOM;
    }

    protected Reward reward = Reward.RANDOM;

    private transient Observable observable = new Observable(); //Array of observers that listen to the Objective.

    private transient UpdateOn[] updateOnArray;

    public AbstractObjective(UpdateOn... updateOns){
        this.updateOnArray = updateOns;
    }


    public abstract String getDescription();
    public abstract boolean isComplete(World world);

    /**
     * This is mainly used for bonus objectives.
     * @param world
     * @return
     */
    public boolean isFailed(World world){
        return false;
    }


    public UpdateOn[] getUpdateOnArray() {
        return updateOnArray;
    }

    public void addObserver(Observer o){
        observable.addObserver(o);
    }

    @Override
    public void update(Object o) {
        observable.notifyObservers(this);
    }

    public Reward getReward() {
        return reward;
    }

    @Override
    public abstract AbstractObjective clone();
}
