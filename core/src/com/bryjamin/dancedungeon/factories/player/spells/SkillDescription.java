package com.bryjamin.dancedungeon.factories.player.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.factories.player.spells.animations.Skill;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 18/11/2017.
 */

public abstract class SkillDescription {

    protected Skill skill;

    public abstract Array<Entity> createTargeting(World world, Entity player);

    public abstract boolean canCast(World world, Entity entity);

    public abstract void cast(World world, Entity entity, Coordinates target);

    public Skill getSkill(){
        return skill;
    }

    public abstract void endTurnUpdate();

    public abstract String getIcon();


}





