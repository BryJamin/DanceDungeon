package com.bryjamin.dancedungeon.factories.player.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ConditionalActionsComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.BattleMessageSystem;
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

        return defaultButton(x, y, new WorldAction() {
            @Override
            public void performAction(World world, Entity entity) {
                world.getSystem(TurnSystem.class).setUp(TurnSystem.TURN.ENEMY);
            }
        });

    }


    public ComponentBag skillButton(float x, float y, final SkillDescription skillDescription, final Entity player) {

        ComponentBag bag = new ComponentBag();
        bag.add(new PositionComponent(x, y));
        bag.add(new HitBoxComponent(new HitBox(new Rectangle(x, y, SIZE, SIZE))));
        bag.add(new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE, new TextureDescription.Builder(skillDescription.getIcon())
                .size(SIZE)
                .build()));

        bag.add(new ActionOnTapComponent(new WorldAction() {
            @Override
            public void performAction(World world, Entity entity) {

                Array<Entity> entityArray = skillDescription.createTargeting(world, player);

                if(entityArray.size <= 0){
                    world.getSystem(BattleMessageSystem.class).createWarningMessage();
                }

                world.getSystem(SelectedTargetSystem.class).clearTargeting();
            }
        }));
        bag.add(new ConditionalActionsComponent(new WorldConditionalAction() {

            boolean isCanCastCondition = false;


            @Override
            public boolean condition(World world, Entity entity) {

                if (isCanCastCondition) {
                    isCanCastCondition = false;
                    return skillDescription.canCast(world, player);
                } else {
                    isCanCastCondition = true;
                    return !skillDescription.canCast(world, player);
                }

            }

            @Override
            public void performAction(World world, Entity entity) {

                if(!isCanCastCondition){
                    entity.getComponent(DrawableComponent.class).drawables.first().getColor().a = 1f;
                    entity.getComponent(ActionOnTapComponent.class).enabled = true;
                } else {
                    entity.getComponent(DrawableComponent.class).drawables.first().getColor().a = 0.1f;
                    entity.getComponent(ActionOnTapComponent.class).enabled = false;
                }
            }
        }));

        return bag;

    }


    public ComponentBag defaultButton(float x, float y, WorldAction action) {
        ComponentBag bag = new ComponentBag();
        bag.add(new PositionComponent(x, y));
        bag.add(new HitBoxComponent(new HitBox(new Rectangle(x, y, SIZE, SIZE))));
        bag.add(new ActionOnTapComponent(action));

        return bag;

    }

}
