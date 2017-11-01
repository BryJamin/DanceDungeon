package com.bryjamin.dancedungeon.ecs.systems.graphical;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.Bag;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.BoundComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.VelocityComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ConditionalActionsComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.OnDeathActionsComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.FadeComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeadComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.systems.FindPlayerSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.math.CoordinateMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;
import com.bryjamin.dancedungeon.utils.texture.Layer;

/**
 * Created by BB on 29/10/2017.
 */

public class PlayerGraphicalTargetingSystem extends BaseSystem {



    private Bag<Entity> trackedEntities = new Bag<Entity>();



    @Override
    protected void processSystem() {

    }



    public void createTargetTile(final Entity entity, int range){

        clearTrackedEntites();

        IntBag intBag = world.getAspectSubscriptionManager().get(Aspect.all(EnemyComponent.class, HealthComponent.class, CoordinateComponent.class)).getEntities();

        TileSystem tileSystem = world.getSystem(TileSystem.class);

        for(int i = 0; i < intBag.size(); i++) {


            Coordinates c1 = world.getSystem(FindPlayerSystem.class).getPlayerComponent(CoordinateComponent.class).coordinates;
            Coordinates c2 = world.getEntity(intBag.get(i)).getComponent(CoordinateComponent.class).coordinates;

            if (tileSystem.getOccupiedMap().containsValue(world.getEntity(intBag.get(i)), true)
                    && CoordinateMath.isWithinRange(c1, c2, range)) {


                final Rectangle r = new Rectangle();
                Coordinates c = tileSystem.getOccupiedMap().findKey(world.getEntity(intBag.get(i)), true);

                if(tileSystem.getRectangleUsingCoordinates(c, r)) {

                    Entity box = BagToEntity.bagToEntity(world.createEntity(), highlightBox(r));

                    box.edit().add(new ActionOnTapComponent(new WorldAction() {
                        @Override
                        public void performAction(World world, final Entity e) {

                            PositionComponent positionComponent = entity.getComponent(PositionComponent.class);

                            float size = Measure.units(5f);

                            float x = CenterMath.centerPositionX(size, positionComponent.getX() + Measure.units(2.5f));
                            float y = CenterMath.centerPositionY(size, positionComponent.getY() + Measure.units(2.5f));


                            Entity fireBall = world.createEntity();
                            fireBall.edit().add(new PositionComponent(x, y));
                            fireBall.edit().add(new CoordinateComponent(new Coordinates(), true));
                            fireBall.edit().add(new MoveToComponent(new Vector3(
                                    CenterMath.centerPositionX(size, r.getCenter(new Vector2()).x),
                                    CenterMath.centerPositionY(size, r.getCenter(new Vector2()).y),
                                    0)));

                            fireBall.edit().add((new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE,
                                    new DrawableDescription.DrawableDescriptionBuilder(TextureStrings.BLOCK)
                                            .color(new Color(Color.ORANGE))
                                            .width(size)
                                            .height(size)
                                            .build())));

                            fireBall.edit().add(new VelocityComponent());
                            fireBall.edit().add(new BoundComponent(new Rectangle(x, y, size, size)));

                            fireBall.edit().add(new ConditionalActionsComponent(new WorldConditionalAction() {
                                @Override
                                public boolean condition(World world, Entity entity) {
                                    return entity.getComponent(MoveToComponent.class).isEmpty();
                                }

                                @Override
                                public void performAction(World world, Entity entity) {
                                    entity.edit().remove(ConditionalActionsComponent.class);
                                    entity.edit().add(new DeadComponent());
                                }
                            }));


                            fireBall.edit().add(new OnDeathActionsComponent(new WorldAction() {
                                @Override
                                public void performAction(World world, Entity entity) {

                                    TileSystem tileSystem = world.getSystem(TileSystem.class);

                                    CoordinateComponent coordinateComponent = entity.getComponent(CoordinateComponent.class);

                                    System.out.println(coordinateComponent.coordinates);

                                    for(Entity e : tileSystem.getCoordinateMap().get(coordinateComponent.coordinates)){
                                        if(world.getMapper(EnemyComponent.class).has(e)){
                                            e.getComponent(HealthComponent.class).applyDamage(3);
                                        }
                                    };

                                }
                            }));

                            clearTrackedEntites();

                        }
                    }));

                    trackedEntities.add(box);

                }

            }

        }




    }

    public void clearTrackedEntites(){
        if(trackedEntities.size() > 0){
            for(Entity e : trackedEntities){
                e.edit().add(new DeadComponent());
            }
        }
        trackedEntities.clear();
    }



    public ComponentBag highlightBox(Rectangle r){
        ComponentBag bag = new ComponentBag();

        bag.add(new PositionComponent(r.x, r.y));
        bag.add(new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE,
                new DrawableDescription.DrawableDescriptionBuilder(TextureStrings.BLOCK)
                        .color(new Color(Color.WHITE))
                        .width(r.getWidth())
                        .height(r.getHeight())
                        .build()));
        bag.add(new FadeComponent(true, 1.0f, true));
        bag.add(new HitBoxComponent(new HitBox(r)));
        bag.add(new BoundComponent());

        return bag;
    }



    public void createMovementTiles(Entity entity, int movementRange){

        clearTrackedEntites();

        CoordinateComponent coordinateComponent = world.getSystem(FindPlayerSystem.class).getPlayerComponent(CoordinateComponent.class);

        Array<Coordinates> coordinatesArray = CoordinateMath.getCoordinatesInRange(coordinateComponent.coordinates, movementRange);

        TileSystem tileSystem = world.getSystem(TileSystem.class);

        for(Coordinates c : coordinatesArray){

            final Queue<Coordinates> coordinatesQueue = new Queue<Coordinates>();

            boolean bool = tileSystem.findShortestPath(coordinateComponent.coordinates, c, coordinatesQueue, false);

            if(coordinatesQueue.size <= movementRange && bool) {

                Rectangle r = new Rectangle();

                if(tileSystem.getRectangleUsingCoordinates(c, r)) {

                    Entity box = BagToEntity.bagToEntity(world.createEntity(), highlightBox(r));
                    trackedEntities.add(box);

                    box.edit().add(new ActionOnTapComponent(new WorldAction() {
                        @Override
                        public void performAction(World world, Entity entity) {

                            Entity player = world.getSystem(FindPlayerSystem.class).getPlayerEntity();

                            for(Coordinates c : coordinatesQueue){
                                player.getComponent(MoveToComponent.class).movementPositions.add(
                                        world.getSystem(TileSystem.class).getPositionUsingCoordinates(
                                                c, player.getComponent(BoundComponent.class).bound));
                            }

                            clearTrackedEntites();

                        }
                    }));

                }

            }

        }

    }



}
