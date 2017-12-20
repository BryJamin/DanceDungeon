package com.bryjamin.dancedungeon.factories.player.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.TurnActionMonitorComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.FadeComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.UITargetingComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionCameraSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.SelectedTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.math.CoordinateMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 18/11/2017.
 */

public class TargetingFactory {

    //Melee Component

    //Range Targeting


    public Array<Entity> createTargetTiles(World world, final Entity player, final SkillDescription spell, int range) {

        Array<Entity> entityArray = new Array<Entity>();

        TargetComponent targetComponent = player.getComponent(TargetComponent.class);

        TileSystem tileSystem = world.getSystem(TileSystem.class);

        for (Entity e : getTargetsInRange(world, player.getComponent(CoordinateComponent.class).coordinates, targetComponent, player.getComponent(StatComponent.class).attackRange)){

            final Coordinates attackC = tileSystem.getOccupiedMap().findKey(e, true);

            Entity redBox = BagToEntity.bagToEntity(world.createEntity(), highlightBox(tileSystem.getRectangleUsingCoordinates(attackC), new Color(Color.RED)));
            entityArray.add(redBox);

            redBox.edit().add(new ActionOnTapComponent(new WorldAction() {
                @Override
                public void performAction(World world, final Entity e) {
                    new FireballSkill().cast(world, player, attackC);
                    world.getSystem(SelectedTargetSystem.class).clearTargeting();
                    player.getComponent(TurnActionMonitorComponent.class).attackActionAvailable = false;
                    player.getComponent(TurnActionMonitorComponent.class).movementActionAvailable = false;
                }
            }));

        }

        return entityArray;
    }


    public Array<Entity> createMovementTiles(World world, final Entity player, int movementRange) {

        Array<Entity> entityArray = new Array<Entity>();

        CoordinateComponent coordinateComponent = player.getComponent(CoordinateComponent.class);

        TileSystem tileSystem = world.getSystem(TileSystem.class);

        for (Coordinates c : CoordinateMath.getCoordinatesInMovementRange(coordinateComponent.coordinates, movementRange)) {

            if (!tileSystem.getCoordinateMap().containsKey(c)) continue;

            final Queue<Coordinates> coordinatesQueue = new Queue<Coordinates>();

            Array<Coordinates> coordinatesArray = new Array<Coordinates>();
            coordinatesArray.add(c);

            boolean bool = tileSystem.findShortestPath(coordinatesQueue, coordinateComponent.coordinates, coordinatesArray);

            if (!(coordinatesQueue.size <= movementRange && bool)) continue;


            if (player.getComponent(TurnActionMonitorComponent.class).movementActionAvailable) {

                if (coordinatesQueue.size <= movementRange && bool) {

                    Rectangle r = tileSystem.getRectangleUsingCoordinates(c);

                    //Create movement box

                    Entity box = BagToEntity.bagToEntity(world.createEntity(), highlightBox(r, new Color(Color.WHITE)));
                    entityArray.add(box);
                    box.edit().add(new ActionOnTapComponent(new WorldAction() {
                        @Override
                        public void performAction(World world, Entity entity) {

                            world.getSystem(ActionCameraSystem.class).pushLastAction(player, new WorldConditionalAction() {
                                @Override
                                public boolean condition(World world, Entity entity) {
                                    return entity.getComponent(MoveToComponent.class).isEmpty();
                                }

                                @Override
                                public void performAction(World world, Entity entity) {
                                    for (Coordinates c : coordinatesQueue) {
                                        player.getComponent(MoveToComponent.class).movementPositions.add(
                                                world.getSystem(TileSystem.class).getPositionUsingCoordinates(
                                                        c, player.getComponent(CenteringBoundaryComponent.class).bound));
                                    }
                                }
                            });
                            //pushLastAction
                            player.getComponent(TurnActionMonitorComponent.class).movementActionAvailable = false;


                            if(new TargetingFactory().getTargetsInRange(world, coordinatesQueue.last(), player.getComponent(TargetComponent.class),
                                    player.getComponent(StatComponent.class).attackRange).size <= 0){
                                player.getComponent(TurnActionMonitorComponent.class).attackActionAvailable = false;
                            }


                            world.getSystem(SelectedTargetSystem.class).clearTargeting();

                        }
                    }));
                };


                //Create Attacking box

                if (player.getComponent(TurnActionMonitorComponent.class).attackActionAvailable) {

                    TargetComponent targetComponent = player.getComponent(TargetComponent.class);

                    for (Entity e : getTargetsInRange(world, c, targetComponent, player.getComponent(StatComponent.class).attackRange)){

                        final Coordinates attackC = tileSystem.getOccupiedMap().findKey(e, true);

                        Entity redBox = BagToEntity.bagToEntity(world.createEntity(), highlightBox(tileSystem.getRectangleUsingCoordinates(attackC), new Color(Color.RED)));
                        entityArray.add(redBox);

                        redBox.edit().add(new ActionOnTapComponent(new WorldAction() {
                            @Override
                            public void performAction(World world, final Entity e) {

                                world.getSystem(ActionCameraSystem.class).pushLastAction(player, new WorldConditionalAction() {
                                    @Override
                                    public boolean condition(World world, Entity entity) {
                                        return entity.getComponent(MoveToComponent.class).isEmpty();
                                    }

                                    @Override
                                    public void performAction(World world, Entity entity) {
                                        for (Coordinates c : coordinatesQueue) {
                                            player.getComponent(MoveToComponent.class).movementPositions.add(
                                                    world.getSystem(TileSystem.class).getPositionUsingCoordinates(
                                                            c, player.getComponent(CenteringBoundaryComponent.class).bound));
                                        }
                                    }
                                });


                                world.getSystem(ActionCameraSystem.class).pushLastAction(player, new WorldConditionalAction() {
                                    @Override
                                    public boolean condition(World world, Entity entity) {
                                        return entity.getComponent(MoveToComponent.class).isEmpty();
                                    }

                                    @Override
                                    public void performAction(World world, Entity entity) {
                                        new FireballSkill().cast(world, player, attackC);
                                    }
                                });


                                world.getSystem(SelectedTargetSystem.class).clearTargeting();
                                player.getComponent(TurnActionMonitorComponent.class).attackActionAvailable = false;
                                player.getComponent(TurnActionMonitorComponent.class).movementActionAvailable = false;
                            }
                        }));

                    }

                }


            }


        }

        return entityArray;

    }

    /**
     * Gets All targets of an entity's target component that are in range of a given co-ordinates
     * @param world
     * @param startCoordinates - The co-ordinate the scan orignates from
     * @param targetComponent - Target Component of the entity
     * @param range - The distance of the scan
     * @return - All entities within range of the target co-ordinate
     */
    public Array<Entity> getTargetsInRange(World world, Coordinates startCoordinates, TargetComponent targetComponent, int range){

        TileSystem tileSystem = world.getSystem(TileSystem.class);
        Array<Entity> entityArray = new Array<Entity>();

        for (Entity e : targetComponent.getTargets(world)) {

            Coordinates targetCoordinates = e.getComponent(CoordinateComponent.class).coordinates;

            //Checks if the Map contains the target
            //TODO maybe change occupied Map to a different check, (Like just check the entity is there)
            if (tileSystem.getOccupiedMap().containsValue(e, true)
                    && CoordinateMath.isWithinRange(startCoordinates, targetCoordinates, range)) {

                entityArray.add(e);
            }

        }

        return entityArray;

    }



    public ComponentBag highlightBox(Rectangle r, Color color) {
        ComponentBag bag = new ComponentBag();

        bag.add(new PositionComponent(r.x, r.y));
        bag.add(new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE,
                new TextureDescription.Builder(TextureStrings.BLOCK)
                        .color(color)
                        .width(r.getWidth())
                        .height(r.getHeight())
                        .build()));
        bag.add(new FadeComponent(true, 1.0f, true));
        bag.add(new HitBoxComponent(new HitBox(r)));
        bag.add(new CenteringBoundaryComponent());
        bag.add(new UITargetingComponent());

        return bag;
    }


}
