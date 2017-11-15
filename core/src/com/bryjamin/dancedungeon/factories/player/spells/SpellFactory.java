package com.bryjamin.dancedungeon.factories.player.spells;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.assets.AssetManager;
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
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.ParentComponent;
import com.bryjamin.dancedungeon.ecs.systems.FindPlayerSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.DeathSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.PlayerGraphicalTargetingSystem;
import com.bryjamin.dancedungeon.factories.AbstractFactory;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;

/**
 * Created by BB on 28/10/2017.
 */

public class SpellFactory extends AbstractFactory {

    private static final float SIZE = Measure.units(10f);

    public SpellFactory(AssetManager assetManager) {
        super(assetManager);
    }

    public ComponentBag endTurnButton(float x, float y){

        return defaultButton(x, y, new WorldAction() {
            @Override
            public void performAction(World world, Entity entity) {
                world.getSystem(TurnSystem.class).setUp(TurnSystem.TURN.ENEMY);
            }
        });

    }


    public ComponentBag moveToButton(float x, float y){

        ComponentBag bag = defaultButton(x, y, new WorldAction() {

            @Override
            public void performAction(World world, Entity entity) {
                if(entity.getComponent(ParentComponent.class).children.size > 0){
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


    public ComponentBag fireBallButton(float x, float y){

        ComponentBag bag = defaultButton(x, y, new WorldAction() {

            @Override
            public void performAction(World world, Entity entity) {


                TileSystem tileSystem = world.getSystem(TileSystem.class);


                IntBag intBag = world.getAspectSubscriptionManager().get(Aspect.all(EnemyComponent.class, CoordinateComponent.class, HealthComponent.class)).getEntities();

                for(int i = 0; i < intBag.size(); i++){
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





    public ComponentBag defaultButton(float x, float y, WorldAction action){

        ComponentBag bag = new ComponentBag();
        bag.add(new PositionComponent(x, y));
        bag.add(new HitBoxComponent(new HitBox(new Rectangle(x, y, SIZE, SIZE))));
        bag.add(new ActionOnTapComponent(action));

        return bag;

    }






    public class CastSpellConditionalAction implements WorldConditionalAction {

        private int cost = 0;

        boolean enabled;

        public CastSpellConditionalAction(int cost){
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
