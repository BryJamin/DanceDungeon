package com.bryjamin.dancedungeon.factories.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 19/11/2017.
 */

public class MovementDescription extends CooldownSpellDescription {

    public MovementDescription(){
    }

    @Override
    public Array<Entity> createTargeting(World world, final Entity player) {

        Array<Entity> entityArray = new TargetingFactory().createMovementTiles(world, player, 3);

        final CooldownSpellDescription csd = this;

        for(Entity e : entityArray){
            e.getComponent(ActionOnTapComponent.class).actions.add(new WorldAction() {
                @Override
                public void performAction(World world, Entity entity) {
                    csd.cast(world, entity, new Coordinates());
                }
            });
        }

        return entityArray;
    }

    @Override
    public void cast(World world, Entity entity, Coordinates target) {
        ready = false;
    }

    @Override
    public boolean canCast(World world, Entity entity) {
        return entity.getComponent(TurnComponent.class).movementActionAvailable;
    }

    @Override
    public String getIcon() {
        return "skills/QuickStep";
    }

}
