package com.bryjamin.dancedungeon.factories.player.spells;

import com.artemis.Entity;
import com.artemis.World;

/**
 * Created by BB on 18/11/2017.
 */

public abstract class SkillDescription {

    protected Spell spell;

    public abstract void createTargeting(World world, Entity player);

    public abstract boolean canCast(World world, Entity entity);

    public Spell getSpell(){
        return spell;
    }


    public abstract String getIcon();


}





