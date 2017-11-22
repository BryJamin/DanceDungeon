package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.ecs.components.BoundComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.math.CoordinateSorter;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.pathing.AStarPathCalculator;

/**
 * Created by BB on 18/10/2017.
 *
 * System that keeps track of the co-ordinates all entities occupy.
 *
 * Is also used to place and locate entities using their co-ordinates
 *
 */

public class TileSystem extends EntityProcessingSystem {


    private ComponentMapper<PlayerControlledComponent> pcm;

    private float originX;
    private float originY;

    private int rows;
    private int columns;

    private float width;
    private float height;

    private float tileWidthSize;
    private float tileHeightSize;


    private Array<Rectangle> movementRectangles = new Array<Rectangle>();

    //Map for all spaces
    private OrderedMap<Coordinates, Array<Entity>> coordinateMap = new OrderedMap<Coordinates,  Array<Entity>>();

    private OrderedMap<Coordinates, Entity> playerControlledMap = new OrderedMap<Coordinates,  Entity>();

    private OrderedMap<Coordinates, Rectangle> rectangleMap = new OrderedMap<Coordinates,  Rectangle>();

    //Mpa used to show if a space is occupied
    private OrderedMap<Coordinates, Entity> occupiedMap = new OrderedMap<Coordinates,  Entity>();

    public OrderedMap<Coordinates, Array<Entity>> getCoordinateMap() {
        return coordinateMap;
    }

    @SuppressWarnings("unchecked")
    public TileSystem(float originX, float originY, float width, float height, int rows, int columns) {
        super(Aspect.all(CoordinateComponent.class, BoundComponent.class, PositionComponent.class));

        this.originX = originX;
        this.originY = originY;

        tileWidthSize = width / columns;
        tileHeightSize = height / rows;
/*
        DrawableComponent drawableComponent = new DrawableComponent(Layer.BACKGROUND_LAYER_FAR);
        bag.add(drawableComponent);*/

        for(int i = 0; i < columns; i++) {
            for(int j = 0; j < rows; j++) {

                coordinateMap.put(new Coordinates(i, j), new Array<Entity>());

                Rectangle r = new Rectangle(originX + i * tileWidthSize,
                        originY + j * tileHeightSize,
                        tileWidthSize,
                        tileHeightSize);

                movementRectangles.add(r);

                rectangleMap.put(new Coordinates(i, j), r);


              //  if(i == columns - 1){
                    movementRectangles.add(new Rectangle(originX + i * tileWidthSize,
                            originY + j * tileHeightSize,
                            tileWidthSize,
                            tileHeightSize));
               // }
            }
        }


    }

    public OrderedMap<Coordinates, Entity> getOccupiedMap() {
        return occupiedMap;
    }



    @Override
    protected void begin() {
        super.begin();
    }

    @Override
    public void inserted(Entity e) {

        CoordinateComponent coordinateComponent = e.getComponent(CoordinateComponent.class);

        if(occupiedMap.containsKey(e.getComponent(CoordinateComponent.class).coordinates) || !coordinateMap.containsKey(coordinateComponent.coordinates)){

            Array<Coordinates> coordinatesArray = coordinateMap.keys().toArray();

            coordinatesArray.sort(CoordinateSorter.SORT_BY_NEAREST(coordinateComponent.coordinates));

            for(int i = 0; i < coordinatesArray.size; i++){

                if(!occupiedMap.containsKey(coordinatesArray.get(i))){
                    e.getComponent(CoordinateComponent.class).coordinates.set(coordinatesArray.get(i));
                    occupiedMap.put(coordinateComponent.coordinates, e);
                    coordinateMap.get(coordinateComponent.coordinates).add(e);
                    break;
                }
            }

        } else {

            if(pcm.has(e)) playerControlledMap.put(coordinateComponent.coordinates, e);
            occupiedMap.put(coordinateComponent.coordinates, e);
            coordinateMap.get(coordinateComponent.coordinates).add(e);

        }

        if(!coordinateComponent.freePlacement) {
            placeUsingCoordinates(e.getComponent(CoordinateComponent.class).coordinates, e.getComponent(PositionComponent.class), e.getComponent(BoundComponent.class));
        }
    }


    public void updateCoordinates(Entity e){

        CoordinateComponent coordinateComponent = e.getComponent(CoordinateComponent.class);

        occupiedMap.remove(coordinateComponent.coordinates);
        coordinateMap.get(coordinateComponent.coordinates).removeValue(e, true);

        if(pcm.has(e)) playerControlledMap.remove(coordinateComponent.coordinates);


        BoundComponent boundComponent = e.getComponent(BoundComponent.class);
        coordinateComponent.coordinates = getCoordinatesUsingPosition(boundComponent.bound);

        occupiedMap.put(coordinateComponent.coordinates, e);
        coordinateMap.get(coordinateComponent.coordinates).add(e);
        if(pcm.has(e)) playerControlledMap.put(coordinateComponent.coordinates, e);


    }

    @Override
    public void removed(Entity e) {
        playerControlledMap.remove(e.getComponent(CoordinateComponent.class).coordinates);
        occupiedMap.remove(e.getComponent(CoordinateComponent.class).coordinates);
        coordinateMap.get(e.getComponent(CoordinateComponent.class).coordinates).removeValue(e, true);
    }



    @Override
    protected void process(Entity e) {

        //TODO might be better to do this after a turn instead of all the time
        updateCoordinates(e);


    }


    /**
     * Uses the given coordinates to place an entity centered within the bounds
     *
     * @param coordinates - Coordinates to where the entity will be placed
     * @param pc - Position Component of Entity
     * @param bc - Bound Component
     */
    public void placeUsingCoordinates(Coordinates coordinates, PositionComponent pc, BoundComponent bc){

        float x = originX + ((coordinates.getX()) * tileWidthSize);
        float y = originY + ((coordinates.getY()) * tileHeightSize);

        bc.bound.x = x + CenterMath.offsetX(tileWidthSize, bc.bound.getWidth());
        bc.bound.y = y + CenterMath.offsetY(tileHeightSize, bc.bound.getHeight());

        pc.position.set(bc.bound.x, bc.bound.y, pc.position.z);

    }

    /**
     * Gets the position an entity would be at if moved to the given coordinates
     * @param coordinates - The coordinates used to generate a position
     * @param rectangle - The rectangle of the entity that will be used to determine position
     * @return - Returns the x and y position.
     */
    public Vector3 getPositionUsingCoordinates(Coordinates coordinates, Rectangle rectangle){

        float x = originX + ((coordinates.getX()) * tileWidthSize);
        float y = originY + ((coordinates.getY()) * tileHeightSize);

        return new Vector3(x + CenterMath.offsetX(tileWidthSize, rectangle.getWidth()),
        y + CenterMath.offsetY(tileHeightSize, rectangle.getHeight()), 0);

    }

    /**
     * Returns the rectangle located at the given coordinates
     * @param coordinates - The coordinates used to check if the rectangle exists
     * @return - True, if a rectangle can be found, false if otherwise
     */
    public Rectangle getRectangleUsingCoordinates(Coordinates coordinates) {
        if(rectangleMap.containsKey(coordinates)){
            return new Rectangle(rectangleMap.get(coordinates));
        }
        return null;
    }

    /**
     * Given a rectangle returns the co-ordinate position of it.
     * @param rectangle - The rectangle
     * @return - The Coordinate position of the rectangle provided
     */
    public Coordinates getCoordinatesUsingPosition(Rectangle rectangle){

        for(Rectangle r : rectangleMap.values().toArray()){
            if(r.contains(rectangle)){
                return rectangleMap.findKey(r, false);
            }
        }
        return new Coordinates();

    }

    /**
     * Given an x and y value returns the coordinates
     */
    public Coordinates getCoordinatesUsingPosition(float x, float y){

        Coordinates coordinates = new Coordinates();

        coordinates.setX((int)((x - originX) / tileWidthSize));
        coordinates.setY((int)((y - originY) / tileHeightSize));

        //when negative
        if(x - originX < 0) coordinates.addX(-1);
        if(y - originY < 0) coordinates.addY(-1);

        return coordinates;

    }

    public boolean findShortestPath(Queue<Coordinates> fillQueue, Coordinates start, Array<Coordinates> targets){
        AStarPathCalculator aStarPathCalculator = new AStarPathCalculator(coordinateMap.keys().toArray(), occupiedMap.keys().toArray());
        return aStarPathCalculator.findShortestPathMultiple(fillQueue,start, targets);
    }

    public OrderedMap<Coordinates, Entity> getPlayerControlledMap() {
        return playerControlledMap;
    }
}
