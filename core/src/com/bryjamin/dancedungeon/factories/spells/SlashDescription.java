package com.bryjamin.dancedungeon.factories.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.factories.spells.animations.Slash;

/**
 * Created by BB on 20/11/2017.
 */

    public class SlashDescription extends CooldownSpellDescription {

    public SlashDescription(){
        super(new Builder()
        .attack(Attack.Melee)
        .targeting(Targeting.Enemy)
        .icon("skills/Slash")
        .name("Slash"));

        skillAnimation = new Slash();
    }

    @Override
    public Array<Entity> createTargeting(World world, final Entity player) {
        Array<Entity> entityArray = new com.bryjamin.dancedungeon.factories.spells.TargetingFactory().createTargetTiles(world, player, this, 1);
        return entityArray;
    }

}
