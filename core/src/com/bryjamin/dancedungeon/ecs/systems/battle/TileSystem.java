package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.SolidComponent;
import com.bryjamin.dancedungeon.factories.decor.FloorFactory;
import com.bryjamin.dancedungeon.factories.map.event.BattleEvent;
import com.bryjamin.dancedungeon.factories.player.UnitFactory;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.enums.Direction;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.math.CoordinateMath;
import com.bryjamin.dancedungeon.utils.math.CoordinateSorter;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.pathing.AStarPathCalculator;

/**
 * Created by BB on 18/10/2017.
 * <p>
 * System that keeps track of the co-ordinates all entities occupy.
 * <p>
 * Is also used to place and locate entities using their co-ordinates
 */

//TODO make this an oberver for the turn system. Once a turn has been completed, this should be Notified and make updates to what is and isn't occupied

public class TileSystem extends EntitySystem {


    private ComponentMapper<PlayerControlledComponent> pcm;
    private ComponentMapper<CoordinateComponent> cm;
    private ComponentMapper<SolidComponent> sm;
    private ComponentMapper<EnemyComponent> enemym;

    private TiledMap map;
    private BattleEvent battleEvent;


    private static final float originX = Measure.units(7.5f);
    private static final float originY = Measure.units(14.5f);

    private static final int rows = 6;
    private static final int columns = 8;

    private static final float width = columns * Measure.units(6.5f);
    private static final float height = rows * Measure.units(6.5f);

    private int maxX;
    private int maxY;

    private static final float tileWidthSize = width / columns;;
    private static final float tileHeightSize = height / rows;

    public static final float CELL_SIZE = tileWidthSize;

    private Array<Coordinates> enemySpawningLocations = new Array<Coordinates>();
    private Array<Coordinates> allySpawningLocations = new Array<Coordinates>();

    //Map for all spaces
    private OrderedMap<Coordinates, Array<Entity>> coordinateMap = new OrderedMap<>();

    private OrderedMap<Entity, Coordinates> playerControlledMap = new OrderedMap<Entity, Coordinates>();
    private OrderedMap<Entity, Coordinates> enemyMap = new OrderedMap<Entity, Coordinates>();

    //Map used to show if a space is occupied
    private ArrayMap<Entity, Coordinates> occupiedMap = new ArrayMap<>();

    private OrderedMap<Coordinates, Rectangle> rectangleMap = new OrderedMap<Coordinates, Rectangle>();

    public OrderedMap<Coordinates, Array<Entity>> getCoordinateMap() {
        return coordinateMap;
    }

    @SuppressWarnings("unchecked")
    public TileSystem(BattleEvent battleEvent) {
        super(Aspect.all(CoordinateComponent.class, CenteringBoundComponent.class, PositionComponent.class));
        this.battleEvent = battleEvent;
    }

    @Override
    protected void initialize() {

        this.maxX = columns;
        this.maxY = rows;

        map = new TmxMapLoader(new InternalFileHandleResolver()).load(battleEvent.getMapLocation());

        TiledMapTileLayer objects =  (TiledMapTileLayer) map.getLayers().get("Object");
        TiledMapTileLayer background =  (TiledMapTileLayer) map.getLayers().get("Background");

        UnitFactory unitFactory = new UnitFactory();

        for(int i = 0; i < objects.getWidth(); i++){
            for(int j = 0; j < objects.getHeight(); j++){
                if(objects.getCell(i, j) != null) {
                    TiledMapTile tile = objects.getCell(i, j).getTile();
                    if (tile.getProperties().containsKey("Type")) {
                        String property = (String) tile.getProperties().get("Type");
                        switch (property){
                            case "Wall":
                                unitFactory.baseTileBag(world, new Coordinates(i, j));
                                break;
                            case "Ally":
                                unitFactory.baseAlliedTileBag(world, new Coordinates(i, j));
                                break;
                        }
                    }
                } else { //Create a deployment zone depending on

                    if(i < 3) {
                        allySpawningLocations.add(new Coordinates(i, j));
                        //unitFactory.baseDeploymentZone(world, createRectangleUsingCoordinates(new Coordinates(i, j)), new Coordinates(i, j));
                    } else if(i > 4){
                        //unitFactory.baseDeploymentZone(world, createRectangleUsingCoordinates(new Coordinates(i, j)), new Coordinates(i, j));
                        enemySpawningLocations.add(new Coordinates(i, j));
                    }
                }
            }

        }


        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                coordinateMap.put(new Coordinates(i, j), new Array<Entity>());
                rectangleMap.put(new Coordinates(i, j), createRectangleUsingCoordinates(new Coordinates(i, j)));
            }
        }

        new FloorFactory().createFloor(world, originX, originY, width, height, rows, columns);

    }


    /**
     * Used mainly within the spell class to remove any coordinates that do not fit on the map.
     *
     * This is to avoid players interacting with squ
     *
     * @param coordinatesArray
     */
    public void removeInvalidCoordinates(Array<Coordinates> coordinatesArray){

        Array<Coordinates> copyArray = new Array<Coordinates>(coordinatesArray);

        for(Coordinates c : copyArray){
            if(!coordinateMap.containsKey(c)){
                coordinatesArray.removeValue(c, false);
            }
        }
    }





    public ArrayMap<Entity, Coordinates> getOccupiedMap() {
        return occupiedMap;
    }


    @Override
    protected void begin() {
        super.begin();
    }

    @Override
    protected void processSystem() {

        occupiedMap.clear();
        playerControlledMap.clear();
        enemyMap.clear();

        for(Coordinates c : coordinateMap.keys()){
            coordinateMap.get(c).clear();
        }


        for(Entity e : this.getEntities()){
            updateCoordinates(e);
        }


    }


    public boolean relocateEntity(Entity e) {
        Array<Coordinates> coordinatesArray = coordinateMap.keys().toArray();

        CoordinateComponent coordinateComponent = e.getComponent(CoordinateComponent.class);

        coordinatesArray.sort(CoordinateSorter.SORT_BY_NEAREST(coordinateComponent.coordinates));

        for (int i = 0; i < coordinatesArray.size; i++) {

            if (!occupiedMap.containsValue(coordinatesArray.get(i), false)) {
                e.getComponent(CoordinateComponent.class).coordinates.set(coordinatesArray.get(i));
                addEntityToMaps(e, coordinateComponent.coordinates);
                return true;
            }
        }

        return false;

    }


    /**
     * Adds a given entity to the relevant maps.
     */
    private void addEntityToMaps(Entity e, Coordinates coordinates) {

        if(sm.has(e)) { //'Solid' entities are tracked in the occupied map
            occupiedMap.put(e, coordinates);
        }

        if(coordinateMap.get(coordinates) != null) {//Coordinate map tracks all entities in the coordinate
            coordinateMap.get(coordinates).add(e);
        }

        if (pcm.has(e)) playerControlledMap.put(e, coordinates); //Player and Enemy maps
        if (enemym.has(e)) enemyMap.put(e, coordinates);

    }


    @Override
    public void inserted(Entity e) {

        CoordinateComponent coordinateComponent = e.getComponent(CoordinateComponent.class);


        if(sm.has(e)){ //If the entity is 'solid' it can not be placed on top up another entity.
            if (occupiedMap.containsValue(e.getComponent(CoordinateComponent.class).coordinates, false) || !coordinateMap.containsKey(coordinateComponent.coordinates)) {
                if (!relocateEntity(e))
                    e.deleteFromWorld(); //TODO decide what to do if a there is no space to place something
            } else {
                addEntityToMaps(e, coordinateComponent.coordinates);
            }
        } else {
            addEntityToMaps(e, coordinateComponent.coordinates);
        }

        if (!coordinateComponent.freePlacement) {
            placeUsingCoordinates(e.getComponent(CoordinateComponent.class).coordinates, e.getComponent(PositionComponent.class), e.getComponent(CenteringBoundComponent.class));
        }
    }


    /**
     * Uses the bounds of an entity to update their co-ordinates
     * @param e
     */
    private void updateCoordinates(Entity e) {

        CoordinateComponent coordinateComponent = cm.get(e);
        CenteringBoundComponent centeringBoundComponent = e.getComponent(CenteringBoundComponent.class);

        Vector2 center = centeringBoundComponent.bound.getCenter(new Vector2());

        coordinateComponent.coordinates = getCoordinatesUsingPosition(
                center.x,
                center.y);

        addEntityToMaps(e, coordinateComponent.coordinates);
    }


    /**
     * Uses the given coordinates to place an entity centered within the bounds
     *
     * @param coordinates - Coordinates to where the entity will be placed
     * @param pc          - Position Component of Entity
     * @param bc          - Bound Component
     */
    public void placeUsingCoordinates(Coordinates coordinates, PositionComponent pc, CenteringBoundComponent bc) {

        float x = originX + ((coordinates.getX()) * tileWidthSize);
        float y = originY + ((coordinates.getY()) * tileHeightSize);

        bc.bound.x = x + CenterMath.offsetX(tileWidthSize, bc.bound.getWidth());
        bc.bound.y = y + CenterMath.offsetY(tileHeightSize, bc.bound.getHeight());

        pc.position.set(bc.bound.x, bc.bound.y, pc.position.z);

    }

    /**
     * Gets the position an entity would be at if moved to the given coordinates
     *
     * @param coordinates - The coordinates used to generate a position
     * @param rectangle   - The rectangle of the entity that will be used to determine position
     * @return - Returns the x and y position.
     */
    public Vector3 getPositionUsingCoordinates(Coordinates coordinates, Rectangle rectangle) {
        return getPositionUsingCoordinates(coordinates.getX(), coordinates.getY(), rectangle);

    }

    private Vector3 getPositionUsingCoordinates(int cX, int cY, Rectangle rectangle) {

        float x = originX + (cX * tileWidthSize);
        float y = originY + (cY * tileHeightSize);

        return new Vector3(x + CenterMath.offsetX(tileWidthSize, rectangle.getWidth()),
                y + CenterMath.offsetY(tileHeightSize, rectangle.getHeight()), 0);

    }

    /**
     * Returns the rectangle located at the given coordinates
     *
     * @param coordinates - The coordinates used to check if the rectangle exists
     * @return - True, if a rectangle can be found, false if otherwise
     */
    public Rectangle getRectangleUsingCoordinates(Coordinates coordinates) {
        if (rectangleMap.containsKey(coordinates)) {
            return new Rectangle(rectangleMap.get(coordinates));
        }
        return null;
    }


    public Rectangle createRectangleUsingCoordinates(Coordinates coordinates) {

        return new Rectangle(originX + coordinates.getX() * tileWidthSize,
                originY + coordinates.getY() * tileHeightSize,
                tileWidthSize,
                tileHeightSize);

    }


    /**
     * Given an x and y value returns the coordinates
     */
    public Coordinates getCoordinatesUsingPosition(float x, float y) {

        Coordinates coordinates = new Coordinates();

        coordinates.setX((int) ((x - originX) / tileWidthSize));
        coordinates.setY((int) ((y - originY) / tileHeightSize));

        //when negative
        if (x - originX < 0) coordinates.addX(-1);
        if (y - originY < 0) coordinates.addY(-1);

        return coordinates;

    }

    public boolean findShortestPath(Entity e, Queue<Coordinates> fillQueue, Coordinates target, int maxDistance) {

        Array<Coordinates> coordinatesArray = new Array<>();
        coordinatesArray.add(target);
        return findShortestPath(e, fillQueue, coordinatesArray, maxDistance);
    }



/*
    public OrderedMap<Coordinates, Queue<Coordinates>> findPathsToAllCoordinates(Entity e, Coordinates start) {

        OrderedMap<Coordinates, Queue<Coordinates>> queueOrderedMap = new OrderedMap<>();





        Array<Coordinates> coordinatesArray = new Array<Coordinates>();
        coordinatesArray.add(c);
        return findShortestPath(e, fillQueue, coordinatesArray, maxDistance);
    }
*/



    public boolean findShortestPath(Entity e, Queue<Coordinates> fillQueue, Array<Coordinates> targets, int maxDistance) {

        AStarPathCalculator aStarPathCalculator;

        if(pcm.has(e)) {
            aStarPathCalculator = new AStarPathCalculator(coordinateMap.keys().toArray(), occupiedMap.values().toArray(),
                    playerControlledMap.values().toArray());

        } else { //TODO what to with walls and etc? If there even are walls.

            aStarPathCalculator = new AStarPathCalculator(
                    coordinateMap.keys().toArray(),
                    occupiedMap.values().toArray(),
                    enemyMap.values().toArray());
        }
        return aStarPathCalculator.findShortestPathMultipleChoice(fillQueue,
                e.getComponent(CoordinateComponent.class).coordinates,
                targets,
                maxDistance);
    }


    public AStarPathCalculator createAStarPathCalculator(Entity e){

        AStarPathCalculator aStarPathCalculator;

        if(pcm.has(e)) {//TODO What happens if you have allied units?
            aStarPathCalculator = new AStarPathCalculator(coordinateMap.keys().toArray(), occupiedMap.values().toArray(),
                    playerControlledMap.values().toArray());

        } else { //TODO what to with walls and etc? If there even are walls.
            aStarPathCalculator = new AStarPathCalculator(coordinateMap.keys().toArray(), occupiedMap.values().toArray(),
                    enemyMap.values().toArray());
        }

        return aStarPathCalculator;
    }




    public Array<Coordinates> getFreeCoordinateInAGivenDirection(Coordinates c, Direction[] d){


        Array<Coordinates> coordinatesArray = new Array<Coordinates>();
        Array<Entity> entityArray = new Array<Entity>();

        Coordinates current = c;

        OrderedMap<Coordinates, Array<Entity>> om = world.getSystem(TileSystem.class).getCoordinateMap();

        //Most Left

        for(int i = 0; i < d.length; i++) {

            Coordinates shotCoords = new Coordinates(current);
            CoordinateMath.increaseCoordinatesByOneUsingDirection(d[i], shotCoords, current);

            Coordinates future = new Coordinates();

            while (true) {

                if(om.get(shotCoords) == null){
                    break;
                } else if (om.get(shotCoords).size > 0 && occupiedMap.containsValue(shotCoords, false)) {
                    coordinatesArray.add(shotCoords);
                    break;
                } else {
                    CoordinateMath.increaseCoordinatesByOneUsingDirection(d[i], future, shotCoords);
                    if (!om.containsKey(future)) {
                        boolean b = (d[i] == Direction.DOWN || d[i] == Direction.UP) ? Math.abs(future.getY() - current.getY()) > 1 : Math.abs(future.getX() - current.getX()) > 1;
                        if (b) {
                            coordinatesArray.add(shotCoords);
                        }
                        break;
                    } else {
                        shotCoords.set(future);
                    }

                }

            }

        }

        return coordinatesArray;

    }


/*    public Array<Coordinates> getUnoccupiedCoordinates(){

        Array<Coordinates> unoccupiedCoordinates = new Array<>();



    }*/


    public OrderedMap<Entity, Coordinates> getPlayerControlledMap() {
        return playerControlledMap;
    }

    public OrderedMap<Entity, Coordinates> getEnemyMap() {
        return enemyMap;
    }

    public int getMaxX() {
        return maxX - 1;
    }

    public int getMaxY() {
        return maxY - 1;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }



    public Rectangle getCellDimensions(){
        return new Rectangle(0,0, tileWidthSize, tileHeightSize);
    }

    public float getMinimumCellSize(){
        return tileHeightSize > tileWidthSize ? tileWidthSize : tileHeightSize;
    }


    public Array<Coordinates> getEnemySpawningLocations() {
        return enemySpawningLocations;
    }

    public Array<Coordinates> getAllySpawningLocations() {
        return new Array<>(allySpawningLocations);
    }
}
