package com.bryjamin.dancedungeon.ecs.components.battle;

import com.artemis.Component;
import com.bryjamin.dancedungeon.factories.unit.UnitLibrary;

public class SpawnerComponent extends Component{

    private String unitID = UnitLibrary.MELEE_BLOB;

    public SpawnerComponent(){}

    public SpawnerComponent(String unitID){
        this.unitID = unitID;
    }

    public String getUnitID() {
        return unitID;
    }
}
