package com.bryjamin.dancedungeon.factories.player.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.AbilityPointComponent;

/**
 * Created by BB on 19/11/2017.
 */

public class MovementDescription extends SkillDescription {

    public MovementDescription(){

    }

    @Override
    public void createTargeting(World world, final Entity player) {

        Array<Entity> entityArray = new TargetingFactory().createMovementTiles(world, player, 3);

        for(Entity e : entityArray){
            e.getComponent(ActionOnTapComponent.class).actions.add(new WorldAction() {
                @Override
                public void performAction(World world, Entity entity) {
                    player.getComponent(AbilityPointComponent.class).abilityPoints -= 2;
                }
            });
        }

    }

    @Override
    public boolean canCast(World world, Entity entity) {
        return entity.getComponent(AbilityPointComponent.class).abilityPoints >= 2;
    }


    @Override
    public String getIcon() {
        return "skills/QuickStep";
    }

}
