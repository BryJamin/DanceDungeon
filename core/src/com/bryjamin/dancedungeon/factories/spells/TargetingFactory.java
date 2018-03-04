package com.bryjamin.dancedungeon.factories.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.FadeComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.UITargetingComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.ReselectEntityComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.SelectedEntityComponent;
import com.bryjamin.dancedungeon.ecs.systems.BattleStageUISystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionCameraSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.enums.Direction;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.math.CoordinateMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.pathing.AStarPathCalculator;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 18/11/2017.
 */

public class TargetingFactory {

    //Melee Component

    //Range Targeting


    /**
     * Creates Enemy Targeting Tiles, Then when clicked cast the allocated spell.
     * The range depicts how far this method scans for enemies.
     */
    public Array<Entity> createTargetTiles(World world, final Entity player, final Skill spell, int range) {

        Array<Entity> entityArray = new Array<Entity>();

        TargetComponent targetComponent = player.getComponent(TargetComponent.class);

        for (Entity e : getTargetsInRange(world, player.getComponent(CoordinateComponent.class).coordinates, targetComponent.getTargets(world), range)) {
            entityArray.add(createTargetingBox(world, player, e.getComponent(CoordinateComponent.class).coordinates, spell, true));
        }

        return entityArray;
    }

    /**
     * Creates Ally Targeting Tiles, Then when clicked cast the allocated spell.
     * The range depicts how far this method scans for allies.
     */
    public Array<Entity> createAllyTargetTiles(World world, final Entity player, final Skill spell, int range) {

        Array<Entity> entityArray = new Array<Entity>();

        TargetComponent targetComponent = player.getComponent(TargetComponent.class);

        for (Entity e : getTargetsInRange(world, player.getComponent(CoordinateComponent.class).coordinates, targetComponent.getAllies(world), range)) {
            entityArray.add(createTargetingBox(world, player, e.getComponent(CoordinateComponent.class).coordinates, spell, false));
        }

        return entityArray;
    }


    /**
     * Creates Ally Targeting Tiles, Then when clicked cast the allocated spell.
     * The range depicts how far this method scans for allies.
     */
    public Array<Entity> createSelfTargetTiles(World world, final Entity player, final Skill spell, int range) {
        Array<Entity> entityArray = new Array<Entity>();
        entityArray.add(createTargetingBox(world, player, player.getComponent(CoordinateComponent.class).coordinates, spell, false));
        return entityArray;
    }

    /**
     * Free targeting tiles only use the range as a guide, and are not restricted to allies or enemies.
     * Any tile can be attacked with this skill selected.
     */
    public Array<Entity> createFreeAimTargetTiles(World world, final Entity player, final Skill spell, int range) {

        Array<Entity> entityArray = new Array<Entity>();

        for (Coordinates c : CoordinateMath.getCoordinatesInSquareRange(player.getComponent(CoordinateComponent.class).coordinates, range)) {
            entityArray.add(createTargetingBox(world, player, c, spell, true));
        }

        return entityArray;
    }

    public Entity createTargetingBox(World world, final Entity player, final Coordinates coordinates, final Skill skill, boolean isRed){

        TileSystem tileSystem = world.getSystem(TileSystem.class);

        Entity redBox = BagToEntity.bagToEntity(world.createEntity(), highlightBox(tileSystem.createRectangleUsingCoordinates(coordinates), isRed ? new Color(Color.RED) : new Color(Color.CYAN)));

        redBox.edit().add(new ActionOnTapComponent(new WorldAction() {
            @Override
            public void performAction(World world, final Entity e) {
                skill.cast(world, player, coordinates);
                world.getSystem(BattleStageUISystem.class).reset();
            }
        }));

        return redBox;

    }

    public Array<Entity> createStraightShotTargetTiles(World world, Entity player, Skill skill) {

        Array<Entity> entityArray = new Array<Entity>();

        Coordinates current = player.getComponent(CoordinateComponent.class).coordinates;

        OrderedMap<Coordinates, Array<Entity>> om = world.getSystem(TileSystem.class).getCoordinateMap();

        //Most Left
        Direction[] d = {Direction.DOWN, Direction.UP, Direction.LEFT, Direction.RIGHT};


        for(int i = 0; i < d.length; i++) {

            Coordinates shotCoords = new Coordinates(current);
            increaseCoordinatesByOneUsingDirection(d[i], shotCoords, current);

            Coordinates future = new Coordinates();


            while (true) {

                if(om.get(shotCoords) == null){
                    break;
                } else if (om.get(shotCoords).size > 0) {
                    entityArray.add(createTargetingBox(world, player, shotCoords, skill, true));
                    break;
                } else {

                    increaseCoordinatesByOneUsingDirection(d[i], future, shotCoords);

                    if (!om.containsKey(future)) {

                        boolean b = (d[i] == Direction.DOWN || d[i] == Direction.UP) ? Math.abs(future.getY() - current.getY()) > 1 : Math.abs(future.getX() - current.getX()) > 1;

                        if (b) {
                            entityArray.add(createTargetingBox(world, player, shotCoords, skill, true));
                        }
                        break;
                    } else {
                        entityArray.add(whiteSquareMarker(world, shotCoords));
                        shotCoords.set(future);
                    }

                }

            }

        }


        return entityArray;



    }


    public void increaseCoordinatesByOneUsingDirection(Direction d, Coordinates c1, Coordinates c2){

        switch (d) {
            case DOWN:
                c1.set(c2.getX(), c2.getY() - 1);
                break;
            case UP:
                c1.set(c2.getX(), c2.getY() + 1);
                break;
            case LEFT:
                c1.set(c2.getX() - 1, c2.getY());
                break;
            case RIGHT:
                c1.set(c2.getX() + 1, c2.getY());
                break;
        }


    }



    //You want MovementTile to Move, And then You want to also generate the attack tiles based on
    //All possible squares as well as the square you stand on

    public OrderedMap<Coordinates, Queue<Coordinates>> createPathsToCoordinatesInMovementRange(TileSystem tileSystem, Entity player, Coordinates start, int movementRange){

        OrderedMap<Coordinates, Queue<Coordinates>> coordinatesWithPathMap = new OrderedMap<Coordinates, Queue<Coordinates>>();

        for (Coordinates c : CoordinateMath.getCoordinatesInMovementRange(start, movementRange)) {

            if (!tileSystem.getCoordinateMap().containsKey(c))
                continue; //If gathered co-ordinates do not exist on the map continue.

            Queue<Coordinates> coordinatesPath = new Queue<Coordinates>();

            AStarPathCalculator asp = tileSystem.createAStarPathCalculator(player);
            asp.setStrictMaxRange(true);
            boolean isPathValid = asp.findShortestPath(coordinatesPath, start, c, movementRange);


            if (!isPathValid)
                continue;

            coordinatesWithPathMap.put(c, coordinatesPath);

        }

        return coordinatesWithPathMap;

    }



    public Array<Entity> createMovementTiles(World world, final Entity player, int movementRange) {

        Array<Entity> entityArray = new Array<Entity>();

        CoordinateComponent coordinateComponent = player.getComponent(CoordinateComponent.class);

        TileSystem tileSystem = world.getSystem(TileSystem.class);


        //This Map establishes which paths have been generated and may be used to target enemies
        final OrderedMap<Coordinates, Queue<Coordinates>> coordinatesWithPathMap = createPathsToCoordinatesInMovementRange(tileSystem, player,
                coordinateComponent.coordinates, movementRange);

        for(final Coordinates c : coordinatesWithPathMap.keys()){

            Rectangle r = tileSystem.createRectangleUsingCoordinates(c);

            Entity box = BagToEntity.bagToEntity(world.createEntity(), highlightBox(r, new Color(Color.WHITE)));
            entityArray.add(box);
            box.edit().add(new ActionOnTapComponent(new WorldAction() {
                @Override
                public void performAction(World world, Entity entity) {
                    world.getSystem(BattleStageUISystem.class).reset();
                    player.edit().add(new ReselectEntityComponent());
                    player.edit().remove(SelectedEntityComponent.class);
                    world.getSystem(ActionCameraSystem.class).pushLastAction(player, createMovementAction(player, coordinatesWithPathMap.get(c)));
                }
            }));
        }

        coordinatesWithPathMap.put(coordinateComponent.coordinates, new Queue<Coordinates>());
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


                //pushLastAction
                entity.getComponent(TurnComponent.class).movementActionAvailable = false;

                //If no enemies are in range at the end of the potential movement, set attack action availiable to false
                //TODO but what if you can use a skill surely this makes not sense right?
                //TODO
                if (new TargetingFactory().getTargetsInRange(world, coordinatesQueue.last(), entity.getComponent(TargetComponent.class).getTargets(world),
                        entity.getComponent(StatComponent.class).attackRange).size <= 0) {



                    //entity.getComponent(TurnComponent.class).attackActionAvailable = false;
                }



            }
        };
    }


    /**
     * Gets All targets of an entity's target component that are in range of a given co-ordinates
     *
     * @param world
     * @param startCoordinates - The co-ordinate the scan orignates from
     * @param targetEntities  - Target Component of the entity
     * @param range            - The distance of the scan
     * @return - All entities within range of the target co-ordinate
     */
    public Array<Entity> getTargetsInRange(World world, Coordinates startCoordinates, Array<Entity> targetEntities, int range) {

        TileSystem tileSystem = world.getSystem(TileSystem.class);
        Array<Entity> entityArray = new Array<Entity>();

        for (Entity e : targetEntities) {

            Coordinates targetCoordinates = e.getComponent(CoordinateComponent.class).coordinates;
            //Checks if the Map contains the target

            //TODO maybe change occupied Map to a different check, (Like just check the entity is there)
            if (tileSystem.getOccupiedMap().containsKey(e)
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
                .maximumTime(1.5f)));
        bag.add(new HitBoxComponent(new HitBox(r)));
        bag.add(new CenteringBoundaryComponent());
        bag.add(new UITargetingComponent());

        return bag;
    }

    public ComponentBag whiteMarkerBox(Rectangle r) {
        ComponentBag bag = new ComponentBag();

        float size = Measure.units(2.5f);

        Vector2 center = r.getCenter(new Vector2());

        bag.add(new PositionComponent(CenterMath.centerOnPositionX(size, center.x),
                CenterMath.centerOnPositionY(size, center.y)));
        bag.add(new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE,
                new TextureDescription.Builder(TextureStrings.BLOCK)
                        .color(new Color(Color.WHITE))
                        .width(size)
                        .height(size)
                        .build()));
    /*    bag.add(new FadeComponent(new FadeComponent.FadeBuilder()
                .fadeIn(true)
                .alpha(0.17f)
                .minAlpha(0.15f)
                .maxAlpha(0.55f)
                .maximumTime(2.0f)));*/
        bag.add(new CenteringBoundaryComponent());
        bag.add(new UITargetingComponent());

        return bag;
    }


    public Entity whiteSquareMarker(World world, Coordinates coordinates){

        TileSystem tileSystem = world.getSystem(TileSystem.class);

        Entity redBox = BagToEntity.bagToEntity(world.createEntity(), whiteMarkerBox(tileSystem.createRectangleUsingCoordinates(coordinates)));

        return redBox;
    }


}
