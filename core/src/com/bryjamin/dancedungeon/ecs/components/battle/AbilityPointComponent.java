package com.bryjamin.dancedungeon.ecs.components.battle;

import com.artemis.Component;

/**
 * Created by BB on 02/11/2017.
 */

public class AbilityPointComponent extends Component {

    public int abilityPointsPerTurn = 2;
    public int abilityPoints = 2;

    public AbilityPointComponent(){}

    public AbilityPointComponent(int abilityPoints){
        this.abilityPoints = abilityPoints;
        this.abilityPointsPerTurn = abilityPoints;
    }


}
