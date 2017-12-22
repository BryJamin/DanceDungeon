package com.bryjamin.dancedungeon.factories.player;

import com.badlogic.gdx.utils.OrderedMap;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;

/**
 * Created by BB on 22/12/2017.
 */

public class UnitFactory {

    public static final String UNIT_MAGE = "Mage";
    public static final String UNIT_WARRIOR = "Warrior";

    public interface Command {
        public ComponentBag getUnit(Unit unit);
    }

    public OrderedMap<String, Command> playerUnits = new OrderedMap<String, Command>();

    public UnitFactory(){
        setUpMap();
    }

    public void setUpMap(){

        playerUnits.put(UNIT_MAGE, new Command() {
            @Override
            public ComponentBag getUnit(Unit unit) {
                return new PlayerFactory().mage(unit);
            }
        });

        playerUnits.put(UNIT_WARRIOR, new Command() {
            @Override
            public ComponentBag getUnit(Unit unit) {
                return new PlayerFactory().player(unit);
            }
        });


    }

    public ComponentBag getUnit(Unit unit){
        return playerUnits.get(unit.getId()).getUnit(unit);
    }


}
