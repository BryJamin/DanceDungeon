package com.bryjamin.dancedungeon.factories.spells.animations;

import com.artemis.Entity;
import com.artemis.World;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

public interface SpellAnimation {

    void cast(World world, final Entity caster, final Skill skill, final Coordinates casterCoordinates, final Coordinates target);

}
