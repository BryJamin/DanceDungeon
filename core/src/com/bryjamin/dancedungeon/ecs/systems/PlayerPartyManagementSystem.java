package com.bryjamin.dancedungeon.ecs.systems;

import com.artemis.BaseSystem;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.utils.observer.Observer;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;


/**
 * Created by BB on 30/01/2018.
 */

public class PlayerPartyManagementSystem extends BaseSystem {

    private PartyDetails partyDetails;
    private Array<Observer> observerArray = new Array<Observer>();

    public PlayerPartyManagementSystem(PartyDetails partyDetails){
        this.partyDetails = partyDetails;
    }


    @Override
    protected void processSystem() {

    }

    public PartyDetails getPartyDetails() {
        return partyDetails;
    }


    public void editMorale(int morale){
        partyDetails.changeMorale(morale);
        notifyObservers();
    }

    public void editMoney(int money){
        partyDetails.changeMoney(money);
        notifyObservers();
    }

    public void notifyObservers(){
        for(Observer o : observerArray){
            o.update(this);
        }
    }

    public void addObserver(Observer o){
        observerArray.add(o);
    }
}
