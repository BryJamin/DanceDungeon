package com.bryjamin.dancedungeon.ecs.systems.input;

import com.artemis.Entity;
import com.artemis.World;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeadComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.EndBattleSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.PlayerControlledSystem;
import com.bryjamin.dancedungeon.factories.ButtonFactory;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;

/**
 * Created by BB on 08/01/2018.
 */

public class RestEvent extends MapEvent {

    @Override
    public EventType getEventType() {
        return null;
    }

    @Override
    public void setUpEvent(World world) {

        

        BagToEntity.bagToEntity(world.createEntity(), new ButtonFactory.ButtonBuilder()
                .text("Rest")
                .posX(Measure.units(20f))
                .posY(Measure.units(30f))
                .width(Measure.units(10f))
                .height(Measure.units(10f))
                .buttonAction(new WorldAction() {
                    @Override
                    public void performAction(World world, Entity entity) {

                        entity.edit().remove(ActionOnTapComponent.class);
                        entity.edit().add(new DeadComponent());

                        world.getSystem(EndBattleSystem.class).next();

                        for(Entity e : world.getSystem(PlayerControlledSystem.class).getEntities()){
                            e.getComponent(HealthComponent.class).applyHealing(e.getComponent(HealthComponent.class).maxHealth * 0.3f);
                        }

                    }
                })
                .build());




    }

    @Override
    public boolean isComplete(World world) {
        return false;
    }

    @Override
    public void cleanUpEvent(World world) {

    }

    @Override
    public boolean cleanUpComplete(World world) {
        return true;
    }
}
