package com.bryjamin.dancedungeon.ecs.components.identifiers;

import com.artemis.Component;
import com.bryjamin.dancedungeon.factories.enemy.UnitLibrary;
import com.bryjamin.dancedungeon.factories.player.UnitData;

/**
 * Created by BB on 11/02/2018.
 *
 * Contains all information of a Unit.
 */

public class UnitComponent extends Component {

    private UnitData unitData = UnitLibrary.getUnitData(UnitLibrary.MELEE_BLOB);

    public UnitComponent(){}

    public UnitComponent(UnitData unitData){
        this.unitData = unitData;
    }

    public UnitData getUnitData() {
        return unitData;
    }
}
