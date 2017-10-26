package com.bryjamin.dancedungeon.factories.enemy;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.bryjamin.dancedungeon.assets.Colors;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.BoundComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.VelocityComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldCondition;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldTask;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.DispellableComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.BlinkOnHitComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.factories.AbstractFactory;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;
import com.bryjamin.dancedungeon.utils.texture.Layer;

/**
 * Created by BB on 15/10/2017.
 */

public class DummyFactory extends AbstractFactory {

    public static final float width = Measure.units(5f);
    public static final float height = Measure.units(5f);


    public static final DrawableDescription.DrawableDescriptionBuilder player = new DrawableDescription.DrawableDescriptionBuilder(TextureStrings.BLOB)
            .index(2)
            .size(height)
            .color(Colors.BLOB_RED);

    public DummyFactory(AssetManager assetManager) {
        super(assetManager);
    }



    public ComponentBag targetDummy(float x, float y){

        ComponentBag bag = new ComponentBag();
        bag.add(new PositionComponent(x,y));
        bag.add(new HealthComponent(10));
        bag.add(new EnemyComponent());

        TurnComponent turnComponent = new TurnComponent();

        turnComponent.turnAction = new WorldTask() {
            @Override
            public void performAction(World world, Entity entity) {

                TileSystem tileSystem = world.getSystem(TileSystem.class);

                CoordinateComponent coordinateComponent = entity.getComponent(CoordinateComponent.class);

                int x = MathUtils.random.nextInt(10);
                int y = MathUtils.random.nextInt(5);


                System.out.println("perform action");
                entity.getComponent(MoveToComponent.class).movementPositions.addAll(
                        tileSystem.getPositionUsingCoordinates(new Coordinates(x, y), entity.getComponent(BoundComponent.class).bound),
                        tileSystem.getPositionUsingCoordinates(new Coordinates(x, y + 1), entity.getComponent(BoundComponent.class).bound)

                        );
            }

            @Override
            public void cleanUpAction(World world, Entity e) {

            }
        };

        turnComponent.turnOverCondition = new WorldCondition() {
            @Override
            public boolean condition(World world, Entity entity) {
                return entity.getComponent(MoveToComponent.class).movementPositions.size <= 0;
            }
        };

        bag.add(turnComponent);
        bag.add(new CoordinateComponent(new Coordinates(4, 2)));
        bag.add(new MoveToComponent());
        bag.add(new VelocityComponent(0, 0));
        bag.add(new BlinkOnHitComponent());
        bag.add(new BoundComponent(new Rectangle(x,y, width, height)));
        bag.add(new HitBoxComponent(new HitBox(new Rectangle(x,y, width, height))));
        //bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, player.build()));




        return bag;


    }

    public ComponentBag targetDummyLeft(float x, float y) {

        ComponentBag bag = targetDummy(x, y);
        bag.add(new DispellableComponent(DispellableComponent.Type.HORIZONTAL));
        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, player.build()));
        return bag;

    }


    public ComponentBag targetDummyVert(float x, float y) {

        ComponentBag bag = targetDummy(x, y);
        bag.add(new DispellableComponent(DispellableComponent.Type.VERTICAL));
        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, player.color(Colors.BOMB_ORANGE).build()));
        return bag;

    }


    public ComponentBag targetDummyFrontSlash(float x, float y) {

        ComponentBag bag = targetDummy(x, y);
        bag.add(new DispellableComponent(DispellableComponent.Type.FRONT_SLASH));
        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, player.color(Colors.AMOEBA_BLUE).build()));
        return bag;

    }


    public ComponentBag targetDummyBackSlash(float x, float y) {

        ComponentBag bag = targetDummy(x, y);
        bag.add(new DispellableComponent(DispellableComponent.Type.BACK_SLASH));
        bag.add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, player.color(Colors.AMOEBA_FAST_PURPLE).build()));
        return bag;

    }


}

