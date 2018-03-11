package com.bryjamin.dancedungeon.factories.player;

import com.badlogic.gdx.utils.OrderedMap;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;

/**
 * Created by BB on 22/12/2017.
 */

public class UnitMap {

    public static final String UNIT_MAGE = "Mage";
    public static final String UNIT_WARRIOR = "Warrior";
    public static final String UNIT_ARCHER = "Archer";


    public interface Command {
        public ComponentBag getUnit(UnitData unitData);
    }

    public OrderedMap<String, Command> playerUnits = new OrderedMap<String, Command>();

    public UnitMap(){
        setUpMap();
    }

    public void setUpMap(){

        playerUnits.put(UNIT_MAGE, new Command() {
            @Override
            public ComponentBag getUnit(UnitData unitData) {
                return new PlayerFactory().mage(unitData);
            }
        });

        playerUnits.put(UNIT_WARRIOR, new Command() {
            @Override
            public ComponentBag getUnit(UnitData unitData) {
                return new PlayerFactory().player(unitData);
            }
        });


        playerUnits.put(UNIT_ARCHER, new Command() {
            @Override
            public ComponentBag getUnit(UnitData unitData) {
                return new PlayerFactory().archer(unitData);
            }
        });


    }

    public ComponentBag getUnit(UnitData unitData){
        return playerUnits.get(unitData.getId()).getUnit(unitData);
    }


}
