package com.bryjamin.dancedungeon.ecs.ai.calculations;

import com.artemis.Entity;
import com.artemis.World;

/**
 * Created by BB on 04/11/2017.
 */

public interface ActionScoreCalculation {

    Float calculateScore(World world, Entity entity);

}
