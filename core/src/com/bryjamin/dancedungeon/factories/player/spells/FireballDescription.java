package com.bryjamin.dancedungeon.factories.player.spells;

import com.artemis.Entity;
import com.artemis.World;

/**
 * Created by BB on 18/11/2017.
 */

public class FireballDescription extends SkillDescription {


    public FireballDescription(){
        spell = new Fireball();
    }

    @Override
    public void createTargeting(World world, Entity entity) {
        new TargetingFactory().createTargetTile(world, entity, spell, 3);
    }





}
