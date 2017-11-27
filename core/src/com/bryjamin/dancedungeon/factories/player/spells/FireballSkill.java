package com.bryjamin.dancedungeon.factories.player.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.systems.battle.BattleMessageSystem;

/**
 * Created by BB on 18/11/2017.
 */

public class FireballSkill extends CooldownSpellDescription {


    public FireballSkill(){
        skill = new com.bryjamin.dancedungeon.factories.player.spells.animations.Fireball(6);
    }

    @Override
    public void createTargeting(World world, final Entity player) {
        Array<Entity> entityArray = new TargetingFactory().createTargetTiles(world, player, this, 3);

        if(entityArray.size <= 0){
            world.getSystem(BattleMessageSystem.class).createWarningMessage();
        }


    }

    @Override
    public String getIcon() {
        return "skills/Fire";
    }


}
