package com.bryjamin.dancedungeon.ecs.components.actions;

import com.artemis.Component;
import com.bryjamin.dancedungeon.ecs.ai.UtilityAiCalculator;

/**
 * Created by BB on 04/11/2017.
 */

public class UtilityAiComponent extends Component {

    public UtilityAiCalculator utilityAiCalculator;

    public UtilityAiComponent(){}

    public UtilityAiComponent(UtilityAiCalculator utilityAiCalculator){
        this.utilityAiCalculator = utilityAiCalculator;
    }

}
