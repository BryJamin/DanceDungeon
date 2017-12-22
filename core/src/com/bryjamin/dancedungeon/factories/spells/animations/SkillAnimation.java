package com.bryjamin.dancedungeon.factories.spells.animations;

import com.artemis.Entity;
import com.artemis.World;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 13/11/2017.
 */

public interface SkillAnimation {

    void cast(World world, Entity entity, Coordinates target);


}
