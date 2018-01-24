package com.bryjamin.dancedungeon.factories.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 22/11/2017.
 */

public abstract class CooldownSpellDescription extends Skill {

    protected boolean ready = true;


    @Override
    public abstract Array<Entity> createTargeting(World world, Entity player);

    @Override
    public boolean canCast(World world, Entity entity) {
        return ready && entity.getComponent(TurnComponent.class).attackActionAvailable;
    }

    @Override
    public void cast(World world, Entity entity, Coordinates target) {
        ready = false;
        skillAnimation.cast(world, entity, target);
    }

    @Override
    public String getIcon() {
        return TextureStrings.BLOCK;
    }


    public void endTurnUpdate(){
        ready = true;
    };

}
