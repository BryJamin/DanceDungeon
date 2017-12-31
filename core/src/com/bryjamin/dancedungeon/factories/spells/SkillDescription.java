package com.bryjamin.dancedungeon.factories.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.factories.spells.animations.SkillAnimation;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 18/11/2017.
 */

public abstract class SkillDescription {

    protected SkillAnimation skillAnimation;

    public abstract Array<Entity> createTargeting(World world, Entity player);

    public abstract boolean canCast(World world, Entity entity);

    public abstract void cast(World world, Entity entity, Coordinates target);

    public SkillAnimation getSkillAnimation(){
        return skillAnimation;
    }

    public abstract void endTurnUpdate();

    public abstract String getIcon();


}





