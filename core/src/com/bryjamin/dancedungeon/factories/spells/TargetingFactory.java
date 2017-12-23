package com.bryjamin.dancedungeon.factories.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
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

        // player.getComponent(StatComponent.class).attackRange)

        for (Entity e : getTargetsInRange(world, player.getComponent(CoordinateComponent.class).coordinates, targetComponent, range)) {

            final Coordinates attackC = tileSystem.getOccupiedMap().findKey(e, true);

            Entity redBox = BagToEntity.bagToEntity(world.createEntity(), highlightBox(tileSystem.getRectangleUsingCoordinates(attackC), new Color(Color.RED)));
            entityArray.add(redBox);

            redBox.edit().add(new ActionOnTapComponent(new WorldAction() {
                @Override
                public void performAction(World world, final Entity e) {
                    spell.cast(world, player, attackC);
                    world.getSystem(SelectedTargetSystem.class).clearTargeting();
                }
            }));

        }

        return entityArray;
    }


    //You want MovementTile to Move, And then You want to also generate the attack tiles based on
    //All possible squares as well as the square you stand on


    public Array<Entity> createMovementTiles(World world, final Entity player, int movementRange) {

        Array<Entity> entityArray = new Array<Entity>();

        CoordinateComponent coordinateComponent = player.getComponent(CoordinateComponent.class);

        TileSystem tileSystem = world.getSystem(TileSystem.class);


        //This Map establishes which paths have been generated and may be used to target enemies
        final OrderedMap<Coordinates, Queue<Coordinates>> coordinatesWithPathMap = new OrderedMap<Coordinates, Queue<Coordinates>>();
        coordinatesWithPathMap.put(coordinateComponent.coordinates, new Queue<Coordinates>());

        for (Coordinates c : CoordinateMath.getCoordinatesInMovementRange(coordinateComponent.coordinates, movementRange)) {

            if (!tileSystem.getCoordinateMap().containsKey(c))
                continue; //If gathered co-ordinates do not exist on the map continue.

            final Queue<Coordinates> coordinatesPath = new Queue<Coordinates>();

            boolean isPathValid = tileSystem.findShortestPath(coordinatesPath, coordinateComponent.coordinates, c);

            if (!(coordinatesPath.size <= movementRange && isPathValid))
                continue; //If the path is larger than the movement range, ignore and move on.

            //Add this path to the list of available paths (for attack scan).
            coordinatesWithPathMap.put(c, coordinatesPath);

            if (player.getComponent(TurnComponent.class).movementActionAvailable) {

                Rectangle r = tileSystem.getRectangleUsingCoordinates(c);

                Entity box = BagToEntity.bagToEntity(world.createEntity(), highlightBox(r, new Color(Color.WHITE)));
                entityArray.add(box);
                box.edit().add(new ActionOnTapComponent(new WorldAction() {
                    @Override
                    public void performAction(World world, Entity entity) {

                        world.getSystem(ActionCameraSystem.class).pushLastAction(player, createMovementAction(player, coordinatesPath));
                        //pushLastAction
                        player.getComponent(TurnComponent.class).movementActionAvailable = false;


                        //If no enemies are in range at the end of the potential movement, set attack action avaliable to false
                        if (new TargetingFactory().getTargetsInRange(world, coordinatesPath.last(), player.getComponent(TargetComponent.class),
                                player.getComponent(StatComponent.class).attackRange).size <= 0) {
                            player.getComponent(TurnComponent.class).attackActionAvailable = false;
                        }

                    }
                }));

            }

        }


        if (player.getComponent(TurnComponent.class).attackActionAvailable) {

            final OrderedMap<Coordinates, Queue<Coordinates>> targetCoordinateMovementQueueMap = new OrderedMap<Coordinates, Queue<Coordinates>>();


            for (final Coordinates c : coordinatesWithPathMap.keys()) {

                TargetComponent targetComponent = player.getComponent(TargetComponent.class);

                for (Entity e : getTargetsInRange(world, c, targetComponent, player.getComponent(StatComponent.class).attackRange)) {

                    final Coordinates targetCoordinate = tileSystem.getOccupiedMap().findKey(e, true);

                    if (targetCoordinateMovementQueueMap.get(targetCoordinate) != null) {
                        if (coordinatesWithPathMap.get(c).size < targetCoordinateMovementQueueMap.get(targetCoordinate).size) {
                            targetCoordinateMovementQueueMap.put(targetCoordinate, coordinatesWithPathMap.get(c));
                        }
                    } else {
                        targetCoordinateMovementQueueMap.put(targetCoordinate, coordinatesWithPathMap.get(c));
                    }


                    Entity redBox = BagToEntity.bagToEntity(world.createEntity(), highlightBox(tileSystem.getRectangleUsingCoordinates(targetCoordinate), new Color(Color.RED)));

                    entityArray.add(redBox);

                    redBox.edit().add(new ActionOnTapComponent(new WorldAction() {
                        @Override
                        public void performAction(World world, final Entity e) {
                            world.getSystem(ActionCameraSystem.class).pushLastAction(player, createMovementAction(player, targetCoordinateMovementQueueMap.get(targetCoordinate)));

                            world.getSystem(ActionCameraSystem.class).pushLastAction(player, new WorldConditionalAction() {
                                @Override
                                public boolean condition(World world, Entity entity) {
                                    return entity.getComponent(MoveToComponent.class).isEmpty();
                                }

                                @Override
                                public void performAction(World world, Entity entity) {
                                    entity.getComponent(SkillsComponent.class).basicAttack.cast(world, player, targetCoordinate);
                                }
                            });

                            player.getComponent(TurnComponent.class).movementActionAvailable = false;
                        }
                    }));

                }

            }

        }


        return entityArray;

    }


    public WorldConditionalAction createMovementAction(final Entity entity, final Queue<Coordinates> coordinatesQueue) {
        return new WorldConditionalAction() {
            @Override
            public boolean condition(World world, Entity entity) {
                return entity.getComponent(MoveToComponent.class).isEmpty();
            }

            @Override
            public void performAction(World world, Entity entity) {
                for (Coordinates c : coordinatesQueue) {
                    entity.getComponent(MoveToComponent.class).movementPositions.add(
                            world.getSystem(TileSystem.class).getPositionUsingCoordinates(
                                    c, entity.getComponent(CenteringBoundaryComponent.class).bound));
                }
            }
        };
    }


    /**
     * Gets All targets of an entity's target component that are in range of a given co-ordinates
     *
     * @param world
     * @param startCoordinates - The co-ordinate the scan orignates from
     * @param targetComponent  - Target Component of the entity
     * @param range            - The distance of the scan
     * @return - All entities within range of the target co-ordinate
     */
    public Array<Entity> getTargetsInRange(World world, Coordinates startCoordinates, TargetComponent targetComponent, int range) {

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
        bag.add(new FadeComponent(new FadeComponent.FadeBuilder()
                .fadeIn(true)
                .alpha(0.17f)
                .minAlpha(0.15f)
                .maxAlpha(0.55f)
                .maximumTime(2.0f)));
        bag.add(new HitBoxComponent(new HitBox(r)));
        bag.add(new CenteringBoundaryComponent());
        bag.add(new UITargetingComponent());

        return bag;
    }


}
