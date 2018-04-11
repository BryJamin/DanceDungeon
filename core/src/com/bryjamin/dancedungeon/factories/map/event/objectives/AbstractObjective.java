package com.bryjamin.dancedungeon.factories.map.event.objectives;

import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.Observer;

public abstract class AbstractObjective implements Observer {

    public enum UpdateOn { //Used to show which events objectives should be listening for.
        ENEMY_DEATH,
        END_TURN,
        MORALE_HIT
    }

    public enum Reward { //Describes what reward is given upon the completion of an objective. s
        MONEY, MORALE, SKILL_POINT;

        int value = 1;

        Reward(){}

        Reward(int value){//If there is ever a case where you get multiple value of something for doing a task.
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    protected Reward reward = Reward.MONEY;

    private Array<Observer> observerArray = new Array<Observer>(); //Array of observers that listen to the Objective.

    private UpdateOn[] updateOnArray;

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
        observerArray.add(o);
    }

    @Override
    public void onNotify() {
        for(Observer o : observerArray){
            o.onNotify();
        }
    }

    public Reward getReward() {
        return reward;
    }
}
