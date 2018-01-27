package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.factories.decor.FloorFactory;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
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

public class TileSystem extends EntitySystem {


    private ComponentMapper<PlayerControlledComponent> pcm;
    private ComponentMapper<EnemyComponent> enemym;


    private float originX = Measure.units(12.5f);
    private float originY = Measure.units(17.5f);
    private float width = Measure.units(75f);
    private float height = Measure.units(40f);

    private int rows = 5;
    private int columns = 10;

    private int maxX;
    private int maxY;

    private float tileWidthSize;
    private float tileHeightSize;


    private Array<Rectangle> movementRectangles = new Array<Rectangle>();

    //Map for all spaces
    private OrderedMap<Coordinates, Array<Entity>> coordinateMap = new OrderedMap<Coordinates, Array<Entity>>();

    private OrderedMap<Entity, Coordinates> playerControlledMap = new OrderedMap<Entity, Coordinates>();
    private OrderedMap<Entity, Coordinates> enemyMap = new OrderedMap<Entity, Coordinates>();

    //Map used to show if a space is occupied
    private OrderedMap<Entity, Coordinates> occupiedMap = new OrderedMap<Entity, Coordinates>();

    private OrderedMap<Coordinates, Rectangle> rectangleMap = new OrderedMap<Coordinates, Rectangle>();

    public OrderedMap<Coordinates, Array<Entity>> getCoordinateMap() {
        return coordinateMap;
    }

    @SuppressWarnings("unchecked")
    public TileSystem() {
        super(Aspect.all(CoordinateComponent.class, CenteringBoundaryComponent.class, PositionComponent.class));


    }

    @Override
    protected void initialize() {

        tileWidthSize = width / columns;
        tileHeightSize = height / rows;

        this.maxX = columns;
        this.maxY = rows;

        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                coordinateMap.put(new Coordinates(i, j), new Array<Entity>());
                rectangleMap.put(new Coordinates(i, j), createRectangleUsingCoordinates(new Coordinates(i, j)));
            }
        }

        new FloorFactory().createFloor(world, originX, originY, width, height, rows, columns);
    }

    public OrderedMap<Entity, Coordinates> getOccupiedMap() {
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


    private void addEntityToMaps(Entity e, Coordinates coordinates) {

        occupiedMap.put(e, coordinates);

        if(coordinateMap.get(coordinates) != null) {
            coordinateMap.get(coordinates).add(e);
        }

        if (pcm.has(e)) playerControlledMap.put(e, coordinates);
        if (enemym.has(e)) enemyMap.put(e, coordinates);

    }


    @Override
    public void inserted(Entity e) {

        CoordinateComponent coordinateComponent = e.getComponent(CoordinateComponent.class);

        if (occupiedMap.containsValue(e.getComponent(CoordinateComponent.class).coordinates, false) || !coordinateMap.containsKey(coordinateComponent.coordinates)) {
            if (!relocateEntity(e))
                e.deleteFromWorld(); //TODO decide what to do if a there is no space to place something
        } else {
            addEntityToMaps(e, coordinateComponent.coordinates);
        }

        if (!coordinateComponent.freePlacement) {
            placeUsingCoordinates(e.getComponent(CoordinateComponent.class).coordinates, e.getComponent(PositionComponent.class), e.getComponent(CenteringBoundaryComponent.class));
        }
    }


    public void updateCoordinates(Entity e) {

        CoordinateComponent coordinateComponent = e.getComponent(CoordinateComponent.class);

/*
        occupiedMap.remove(coordinateComponent.coordinates);
        coordinateMap.get(coordinateComponent.coordinates).removeValue(e, true);

        if (pcm.has(e)) playerControlledMap.remove(coordinateComponent.coordinates);
        if(enemym.has(e)) enemyMap.remove(coordinateComponent.coordinates);*/

        CenteringBoundaryComponent centeringBoundaryComponent = e.getComponent(CenteringBoundaryComponent.class);

        //coordinateComponent.coordinates = getCoordinatesUsingPosition(centeringBoundaryComponent.bound);

        coordinateComponent.coordinates = getCoordinatesUsingPosition(
                centeringBoundaryComponent.bound.getCenter(new Vector2()).x,
                centeringBoundaryComponent.bound.getCenter(new Vector2()).y);

        addEntityToMaps(e, coordinateComponent.coordinates);
    }

    @Override
    public void removed(Entity e) {
/*        playerControlledMap.remove(e.getComponent(CoordinateComponent.class).coordinates);
        enemyMap.remove(e.getComponent(CoordinateComponent.class).coordinates);
        occupiedMap.remove(e.getComponent(CoordinateComponent.class).coordinates);
        coordinateMap.get(e.getComponent(CoordinateComponent.class).coordinates).removeValue(e, true);*/
    }
/*

    @Override
    protected void process(Entity e) {

        //TODO might be better to do this after a turn instead of all the time
        updateCoordinates(e);


    }*/


    /**
     * Uses the given coordinates to place an entity centered within the bounds
     *
     * @param coordinates - Coordinates to where the entity will be placed
     * @param pc          - Position Component of Entity
     * @param bc          - Bound Component
     */
    public void placeUsingCoordinates(Coordinates coordinates, PositionComponent pc, CenteringBoundaryComponent bc) {

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

    public Vector3 getPositionUsingCoordinates(int cX, int cY, Rectangle rectangle) {

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
     * Given a rectangle returns the co-ordinate position of it.
     *
     * @param rectangle - The rectangle
     * @return - The Coordinate position of the rectangle provided
     */
    public Coordinates getCoordinatesUsingPosition(Rectangle rectangle) {

        for (Rectangle r : rectangleMap.values().toArray()) {
            if (r.contains(rectangle)) {
                return rectangleMap.findKey(r, false);
            }
        }
        //this is why it is zero.

        return new Coordinates();

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

    public boolean findShortestPath(Entity e, Queue<Coordinates> fillQueue, Coordinates c, int maxDistance) {

        Array<Coordinates> coordinatesArray = new Array<Coordinates>();
        coordinatesArray.add(c);
        return findShortestPath(e, fillQueue, coordinatesArray, maxDistance);
    }

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

        if(pcm.has(e)) {
            aStarPathCalculator = new AStarPathCalculator(coordinateMap.keys().toArray(), occupiedMap.values().toArray(),
                    playerControlledMap.values().toArray());

        } else { //TODO what to with walls and etc? If there even are walls.
            aStarPathCalculator = new AStarPathCalculator(coordinateMap.keys().toArray(), occupiedMap.values().toArray(),
                    enemyMap.values().toArray());
        }

        return aStarPathCalculator;
    }


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


    public float getOriginX() {
        return originX;
    }

    public void setOriginX(float originX) {
        this.originX = originX;
    }

    public float getOriginY() {
        return originY;
    }

    public void setOriginY(float originY) {
        this.originY = originY;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
