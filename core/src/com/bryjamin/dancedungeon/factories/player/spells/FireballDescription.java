package com.bryjamin.dancedungeon.factories.player.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.AbilityPointComponent;

/**
 * Created by BB on 18/11/2017.
 */

public class FireballDescription extends SkillDescription {


    public FireballDescription(){
        spell = new Fireball(6);
    }

    @Override
    public void createTargeting(World world, final Entity player) {
        Array<Entity> entityArray = new TargetingFactory().createTargetTiles(world, player, spell, 3);

        for (Entity e : entityArray) {
            e.getComponent(ActionOnTapComponent.class).actions.add(new WorldAction() {
                @Override
                public void performAction(World world, Entity entity) {
                    player.getComponent(AbilityPointComponent.class).abilityPoints -= 1;
                }
            });
        }
    }


    @Override
    public boolean canCast(World world, Entity entity) {
        return entity.getComponent(AbilityPointComponent.class).abilityPoints >= 1;
    }

    @Override
    public String getIcon() {
        return "skills/Fire";
    }


}
