package com.bryjamin.dancedungeon.factories.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Rectangle;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ConditionalActionsComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.GreyScaleComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.SelectedTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 28/10/2017.
 */

public class SpellFactory {

    private static final float SIZE = Measure.units(10f);

    public ComponentBag endTurnButton(float x, float y) {

        ComponentBag bag = new ComponentBag();

        bag.add(new PositionComponent(x, y));
        bag.add(new HitBoxComponent(new HitBox(new Rectangle(x, y, SIZE, SIZE))));

        bag.add(new ConditionalActionsComponent(new WorldConditionalAction() {

            private boolean isDisabled;

            @Override
            public boolean condition(World world, Entity entity) {

                if(isDisabled){
                    isDisabled = false;
                    return world.getSystem(TurnSystem.class).getTurn() == TurnSystem.TURN.ALLY;
                } else {
                    isDisabled = true;
                    return world.getSystem(TurnSystem.class).getTurn() == TurnSystem.TURN.ENEMY;
                }
            }

            @Override
            public void performAction(World world, Entity entity) {

                if(isDisabled){
                    entity.edit().add(new GreyScaleComponent());
                    entity.edit().remove(ActionOnTapComponent.class);
                } else {
                    entity.edit().remove(GreyScaleComponent.class);
                    entity.edit().add(new ActionOnTapComponent(new WorldAction() {
                        @Override
                        public void performAction(World world, Entity entity) {
                            world.getSystem(TurnSystem.class).setUp(TurnSystem.TURN.ENEMY);
                            world.getSystem(SelectedTargetSystem.class).reset();
                        }
                    }));
                }


            }
        }));

        bag.add(new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE, new TextureDescription.Builder(TextureStrings.END_TURN_BUTTON)
                .size(SIZE)
                .build()));


        return bag;

    }



}
