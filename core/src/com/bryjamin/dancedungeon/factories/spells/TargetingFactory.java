package com.bryjamin.dancedungeon.factories.spells;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.assets.Colors;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.ecs.components.battle.AvailableActionsCompnent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UITargetingComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyIntentUIComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.ReselectEntityComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.SelectedEntityComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionQueueSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.BattleScreenUISystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
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

    //Default Component

    //Range Targeting


    private static final int TILE_LAYER = Layer.ENEMY_LAYER_MIDDLE;
    private static final int TILE_LINE_LAYER = Layer.ENEMY_LAYER_MIDDLE + 1;


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

        TileSystem tileSystem = world.getSystem(TileSystem.class);

        for (Coordinates c : CoordinateMath.getCoordinatesInSquareRange(player.getComponent(CoordinateComponent.class).coordinates, range)) {

            if(tileSystem.getCoordinateMap().containsKey(c)) {
                entityArray.add(createTargetingBox(world, player, c, spell, true));
            }
        }

        return entityArray;
    }


    public Entity createTargetingBox(World world, final Entity player, final Coordinates coordinates, final Skill skill, boolean isRed){

        TileSystem tileSystem = world.getSystem(TileSystem.class);

        Entity redBox = highlightBox(world, tileSystem.createRectangleUsingCoordinates(coordinates), isRed ? new Color(Colors.UI_ATTACK_TILE_COLOR) : new Color(Color.CYAN));

        redBox.edit().add(new ActionOnTapComponent(new WorldAction() {
            @Override
            public void performAction(World world, final Entity e) {
                skill.cast(world, player, coordinates);
                world.getSystem(BattleScreenUISystem.class).resetBottomContainer();
            }
        }));

        return redBox;

    }


    public Array<Entity> createWhiteTargetingMarkers(World world, Coordinates unitCoords, Coordinates target){

        Array<Entity> entityArray = new Array<Entity>();
        Coordinates markerCoords = new Coordinates(target);

        int i = 100;

        while(!unitCoords.equals(markerCoords) && i > 0) {

            if (unitCoords.getX() < target.getX() && unitCoords.getY() == target.getY()) {
                markerCoords.set(markerCoords.getX() - 1, markerCoords.getY());
            } else if (unitCoords.getX() > target.getX() && unitCoords.getY() == target.getY()) {
                markerCoords.set(markerCoords.getX() + 1, markerCoords.getY());
            } else if (unitCoords.getX() == target.getX() && unitCoords.getY() < target.getY()) {
                markerCoords.set(markerCoords.getX(), markerCoords.getY() - 1);
            } else if (unitCoords.getX() == target.getX() && unitCoords.getY() > target.getY()) {
                markerCoords.set(markerCoords.getX(), markerCoords.getY() + 1);
            } else {
                System.out.println("ERROR");
                break;
            }
            if(!unitCoords.equals(markerCoords)) {
                entityArray.add(whiteSquareMarker(world, markerCoords));
            }
            i--;

        }

        return entityArray;


    }


    public Array<Entity> createRedTargetingMarkers(World world, Coordinates unitCoords, Coordinates target){

        Array<Entity> entityArray = new Array<Entity>();
        Coordinates markerCoords = new Coordinates(target);

        int i = 100;

        while(!unitCoords.equals(markerCoords) && i > 0) {

            if (unitCoords.getX() < target.getX() && unitCoords.getY() == target.getY()) {
                markerCoords.set(markerCoords.getX() - 1, markerCoords.getY());
            } else if (unitCoords.getX() > target.getX() && unitCoords.getY() == target.getY()) {
                markerCoords.set(markerCoords.getX() + 1, markerCoords.getY());
            } else if (unitCoords.getX() == target.getX() && unitCoords.getY() < target.getY()) {
                markerCoords.set(markerCoords.getX(), markerCoords.getY() - 1);
            } else if (unitCoords.getX() == target.getX() && unitCoords.getY() > target.getY()) {
                markerCoords.set(markerCoords.getX(), markerCoords.getY() + 1);
            } else {
                System.out.println("ERROR");
                break;
            }
            if(!unitCoords.equals(markerCoords)) {
                entityArray.add(redMarkerBox(world, markerCoords));
            }
            i--;

        }

        return entityArray;


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







        for(final Coordinates c : coordinatesWithPathMap.orderedKeys()){

            Rectangle r = tileSystem.createRectangleUsingCoordinates(c);

            Entity box = highlightBox(world, r, new Color(Colors.UI_MOVEMENT_TILE_COLOR));
            entityArray.add(box);
            box.edit().add(new ActionOnTapComponent(new WorldAction() {
                @Override
                public void performAction(World world, Entity entity) {
                    world.getSystem(BattleScreenUISystem.class).resetBottomContainer();
                    player.edit().add(new ReselectEntityComponent());
                    player.edit().remove(SelectedEntityComponent.class);
                    world.getSystem(ActionQueueSystem.class).pushLastAction(player, createMovementAction(player, coordinatesWithPathMap.get(c)));
                    world.getSystem(ActionQueueSystem.class).createUpdateIntentAction(world.createEntity());
                }
            }));

            //Create the line Borders for the Movement Tiles

            Coordinates c2 = new Coordinates();
            c2.set(c.getX() + 1, c.getY()); //Right Line

            float LINE_THICKNESS = Measure.units(0.5f);

            Color lineColor = new Color(Colors.UI_MOVEMENT_TILE_BORDER_COLOR);

            //Right Line
            if(!coordinatesWithPathMap.containsKey(c2)){

                float y = r.y;
                float height = r.getHeight();

                if(coordinatesWithPathMap.containsKey(new Coordinates(c.getX(), c.getY() + 1)))
                    height += LINE_THICKNESS;

                if(coordinatesWithPathMap.containsKey(new Coordinates(c.getX(), c.getY() - 1))) {
                    y -= LINE_THICKNESS;
                    height += LINE_THICKNESS;
                }

                createLineEntity(world, r.x + r.getWidth() - LINE_THICKNESS, y, LINE_THICKNESS, height, lineColor);

            }

            c2.set(c.getX() - 1, c.getY());

            //Left Line
            if(!coordinatesWithPathMap.containsKey(c2)){

                float y = r.y;
                float height = r.getHeight();

                if(coordinatesWithPathMap.containsKey(new Coordinates(c.getX(), c.getY() + 1)))
                    height += LINE_THICKNESS;

                if(coordinatesWithPathMap.containsKey(new Coordinates(c.getX(), c.getY() - 1))) {
                    y -= LINE_THICKNESS;
                    height += LINE_THICKNESS;
                }


                createLineEntity(world, r.x, y, LINE_THICKNESS, height, lineColor);
            }


            c2.set(c.getX(), c.getY() + 1);
            //Top Line
            if(!coordinatesWithPathMap.containsKey(c2)){
                createLineEntity(world, r.x, r.y + r.getHeight() - LINE_THICKNESS, r.getWidth(), LINE_THICKNESS, lineColor);
            }


            c2.set(c.getX(), c.getY() - 1);
            //Bottom Line
            if(!coordinatesWithPathMap.containsKey(c2)){
                createLineEntity(world, r.x, r.y, r.getWidth(), LINE_THICKNESS, lineColor);
            }











        }

        coordinatesWithPathMap.put(coordinateComponent.coordinates, new Queue<Coordinates>());
        return entityArray;

    }


    private void createLineEntity(World world, float x, float y, float width, float height, Color color){

        Entity e = world.createEntity();
        e.edit().add(new UITargetingComponent());
        e.edit().add(new PositionComponent(x, y));
        e.edit().add(new DrawableComponent(TILE_LINE_LAYER,
                new TextureDescription.Builder(TextureStrings.BLOCK)
                        .color(color)
                        .width(width)
                        .height(height)
                        .build()));

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
                                    c, entity.getComponent(CenteringBoundComponent.class).bound));
                }


                //pushLastAction
                entity.getComponent(AvailableActionsCompnent.class).movementActionAvailable = false;

                //If no enemies are in range at the end of the potential movement, set attack action availiable to false
                if (new TargetingFactory().getTargetsInRange(world, coordinatesQueue.last(), entity.getComponent(TargetComponent.class).getTargets(world),
                        entity.getComponent(UnitComponent.class).getUnitData().getAttackRange()).size <= 0) {



                    //entity.getComponent(AvailableActionsCompnent.class).attackActionAvailable = false;
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

            if (tileSystem.getOccupiedMap().containsKey(e)
                    && CoordinateMath.isWithinRange(startCoordinates, targetCoordinates, range)) {
                entityArray.add(e);
            }

        }

        return entityArray;

    }


    public Entity highlightBox(World world, Rectangle r, Color color) {

        Entity e = world.createEntity();

        e.edit().add(new PositionComponent(r.x, r.y));
        e.edit().add(new DrawableComponent(TILE_LAYER,
                 new TextureDescription.Builder(TextureStrings.BLOCK)
                         .color(color)
                         .width(r.getWidth())
                         .height(r.getHeight())
                         .build()));
        e.edit().add(new HitBoxComponent(new HitBox(r)));
        e.edit().add(new CenteringBoundComponent());
        e.edit().add(new UITargetingComponent());

        return e;
    }


    public Entity whiteMarkerBox(World world, Rectangle r, Color c) {

        Entity e = world.createEntity();

        float size = Measure.units(1.5f);

        Vector2 center = r.getCenter(new Vector2());

        e.edit().add(new PositionComponent(CenterMath.centerOnPositionX(size, center.x),
                CenterMath.centerOnPositionY(size, center.y)));
        e.edit().add(new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE,
                new TextureDescription.Builder(TextureStrings.BLOCK)
                        .color(c)
                        .width(size)
                        .height(size)
                        .build()));
    /*    bag.add(new FadeComponent(new FadeComponent.FadeBuilder()
                .fadeIn(true)
                .alpha(0.17f)
                .minAlpha(0.15f)
                .maxAlpha(0.55f)
                .maximumDuration(2.0f)));*/
        e.edit().add(new CenteringBoundComponent());
        e.edit().add(new UITargetingComponent());

        return e;
    }


    public Entity whiteSquareMarker(World world, Coordinates coordinates){

        TileSystem tileSystem = world.getSystem(TileSystem.class);

        Entity redBox = whiteMarkerBox(world,tileSystem.createRectangleUsingCoordinates(coordinates), new Color(Color.WHITE));

        return redBox;
    }

    public Entity redMarkerBox(World world, Coordinates coordinates){

        TileSystem tileSystem = world.getSystem(TileSystem.class);

        Entity redBox = whiteMarkerBox(world, tileSystem.createRectangleUsingCoordinates(coordinates), new Color(Color.RED));

        redBox.edit().remove(UITargetingComponent.class);
        redBox.edit().add(new EnemyIntentUIComponent());

        return redBox;

    }


}
