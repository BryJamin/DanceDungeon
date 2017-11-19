package com.bryjamin.dancedungeon.factories.player.spells;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.Rectangle;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ConditionalActionsComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.ecs.components.battle.AbilityPointComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.ParentComponent;
import com.bryjamin.dancedungeon.ecs.systems.FindPlayerSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.DeathSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.SelectedTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.PlayerGraphicalTargetingSystem;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;
import com.bryjamin.dancedungeon.utils.texture.Layer;

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


    public ComponentBag moveToButton(float x, float y) {

        ComponentBag bag = defaultButton(x, y, new WorldAction() {

            @Override
            public void performAction(World world, Entity entity) {
                if (entity.getComponent(ParentComponent.class).children.size > 0) {
                    world.getSystem(DeathSystem.class).killChildComponents(entity.getComponent(ParentComponent.class));
                } else {
                    world.getSystem(PlayerGraphicalTargetingSystem.class).createMovementTiles(entity, 3);
                }
            }
        });
        bag.add(new ParentComponent());

        bag.add(new ConditionalActionsComponent(new CastSpellConditionalAction(2)));

        return bag;

    }


    public ComponentBag fireBallButton(float x, float y) {

        ComponentBag bag = defaultButton(x, y, new WorldAction() {

            @Override
            public void performAction(World world, Entity entity) {


                TileSystem tileSystem = world.getSystem(TileSystem.class);


                IntBag intBag = world.getAspectSubscriptionManager().get(Aspect.all(EnemyComponent.class, CoordinateComponent.class, HealthComponent.class)).getEntities();

                for (int i = 0; i < intBag.size(); i++) {
                    Entity e = world.getEntity(intBag.get(i));
                    new Fireball().cast(world.getSystem(FindPlayerSystem.class).getPlayerEntity(), world, e.getComponent(CoordinateComponent.class).coordinates);
                }

                world.getSystem(FindPlayerSystem.class).getPlayerEntity().getComponent(AbilityPointComponent.class).abilityPoints -= 1;
            }
        });


        bag.add(new ConditionalActionsComponent(new CastSpellConditionalAction(1)));


        bag.add(new ParentComponent());

        return bag;

    }


    public ComponentBag skillButton(float x, float y, final SkillDescription skillDescription, final Entity player) {

        System.out.println("x: " + x + " y: " + y);

        ComponentBag bag = new ComponentBag();
        bag.add(new PositionComponent(x, y));
        bag.add(new HitBoxComponent(new HitBox(new Rectangle(x, y, SIZE, SIZE))));
        bag.add(new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE, new DrawableDescription.DrawableDescriptionBuilder(skillDescription.getIcon())
                .size(SIZE)
                .build()));

        bag.add(new ActionOnTapComponent(new WorldAction() {
            @Override
            public void performAction(World world, Entity entity) {
                skillDescription.createTargeting(world, player);
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

        System.out.println("x: " + x + " y: " + y);

        ComponentBag bag = new ComponentBag();
        bag.add(new PositionComponent(x, y));
        bag.add(new HitBoxComponent(new HitBox(new Rectangle(x, y, SIZE, SIZE))));
        bag.add(new ActionOnTapComponent(action));

        return bag;

    }


    public class CastSpellConditionalAction implements WorldConditionalAction {

        private int cost = 0;

        boolean enabled;

        public CastSpellConditionalAction(int cost) {
            this.cost = cost;
        }


        @Override
        public boolean condition(World world, Entity entity) {
            enabled = world.getSystem(FindPlayerSystem.class).getPlayerComponent(AbilityPointComponent.class).abilityPoints >= cost;
            return true;
        }

        @Override
        public void performAction(World world, Entity entity) {
            entity.getComponent(ActionOnTapComponent.class).enabled = enabled;
        }
    }


}
