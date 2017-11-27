package com.bryjamin.dancedungeon.factories.player.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;

/**
 * Created by BB on 20/11/2017.
 */

    public class SlashDescription extends CooldownSpellDescription {

    public SlashDescription(){
        skill = new com.bryjamin.dancedungeon.factories.player.spells.animations.Slash();
    }

    @Override
    public Array<Entity> createTargeting(World world, final Entity player) {
        Array<Entity> entityArray = new TargetingFactory().createTargetTiles(world, player, this, 1);
        return entityArray;
    }

    @Override
    public String getIcon() {
        return "skills/Slash";
    }

}
