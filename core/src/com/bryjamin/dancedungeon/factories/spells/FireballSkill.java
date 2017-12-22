package com.bryjamin.dancedungeon.factories.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.factories.spells.animations.Fireball;

/**
 * Created by BB on 18/11/2017.
 */

public class FireballSkill extends CooldownSpellDescription {


    public FireballSkill(){
        skillAnimation = new Fireball(6);
    }

    @Override
    public Array<Entity> createTargeting(World world, final Entity player) {
        Array<Entity> entityArray = new com.bryjamin.dancedungeon.factories.spells.TargetingFactory().createTargetTiles(world, player, this, 3);
        return entityArray;
    }

    @Override
    public String getIcon() {
        return "skills/Fire";
    }


}
