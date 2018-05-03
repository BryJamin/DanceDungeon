package com.bryjamin.dancedungeon.ecs.systems;

import com.artemis.BaseSystem;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.utils.observer.Observable;
import com.bryjamin.dancedungeon.utils.observer.Observer;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;


/**
 * Created by BB on 30/01/2018.
 */

public class PlayerPartyManagementSystem extends BaseSystem {

    private PartyDetails partyDetails;
    private Observable observable = new Observable();

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

    public void editReputation(int money){
        partyDetails.changeRep(money);
        notifyObservers();
    }

    public void notifyObservers(){
        observable.notifyObservers(this);
    }

    public Observable getObservable() {
        return observable;
    }

    public void addObserver(Observer o){
        observable.addObserver(o);
    }
}
