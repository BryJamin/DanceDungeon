package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.bryjamin.dancedungeon.ecs.components.BoundComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.OverlappableComponent;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.math.CoordinateSorter;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

import java.util.Comparator;

/**
 * Created by BB on 18/10/2017.
 */

public class TileSystem extends EntityProcessingSystem {


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


    private OrderedMap<Coordinates, Rectangle> rectangleMap = new OrderedMap<Coordinates,  Rectangle>();

    //Mpa used to show if a space is occupied
    private OrderedMap<Coordinates, Entity> occupiedMap = new OrderedMap<Coordinates,  Entity>();


    //TODO this is jsut for testing and isn't going to be used.
    public Coordinates playerCoordinates = new Coordinates();

    public Coordinates getPlayerCoordinates() {
        return playerCoordinates;
    }

    public void setPlayerCoordinates(Coordinates playerCoordinates) {
        this.playerCoordinates = playerCoordinates;
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

                System.out.println();

              //  if(i == columns - 1){
                    movementRectangles.add(new Rectangle(originX + i * tileWidthSize,
                            originY + j * tileHeightSize,
                            tileWidthSize,
                            tileHeightSize));
               // }
            }
        }


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

            occupiedMap.put(coordinateComponent.coordinates, e);
            coordinateMap.get(coordinateComponent.coordinates).add(e);

        }

        placeUsingCoordinates(e.getComponent(CoordinateComponent.class).coordinates, e.getComponent(PositionComponent.class), e.getComponent(BoundComponent.class));
    }


    public void updateCoordinates(Coordinates newCoordinates, Entity e){


        CoordinateComponent coordinateComponent = e.getComponent(CoordinateComponent.class);

        occupiedMap.remove(e.getComponent(CoordinateComponent.class).coordinates);
        coordinateMap.get(e.getComponent(CoordinateComponent.class).coordinates).removeValue(e, true);


        coordinateComponent.coordinates = newCoordinates;


        occupiedMap.put(coordinateComponent.coordinates, e);
        coordinateMap.get(coordinateComponent.coordinates).add(e);




    }


    @Override
    public void removed(Entity e) {
        occupiedMap.remove(e.getComponent(CoordinateComponent.class).coordinates);
        coordinateMap.get(e.getComponent(CoordinateComponent.class).coordinates).removeValue(e, true);
    }

    public boolean isSpaceOccupied(Entity e, Coordinates coordinates){

        if(coordinateMap.containsKey(e.getComponent(CoordinateComponent.class).coordinates)){

            for(Entity entity :  coordinateMap.get(e.getComponent(CoordinateComponent.class).coordinates)){
                if(entity.getComponent(OverlappableComponent.class) == null){
                    return true;
                }
            }

        }

        return false;

    }

    @Override
    protected void process(Entity e) {

       // if(coordinateMap.containsKey(e.getComponent(CoordinateComponent.class).coordinates));

     //   placeUsingCoordinates(e.getComponent(CoordinateComponent.class).coordinates, e.getComponent(PositionComponent.class), e.getComponent(BoundComponent.class));


    }



    public void placeUsingCoordinates(Coordinates coordinates, PositionComponent pc, BoundComponent bc){

        float x = originX + ((coordinates.getX()) * tileWidthSize);
        float y = originY + ((coordinates.getY()) * tileHeightSize);

        bc.bound.x = x + CenterMath.offsetX(tileWidthSize, bc.bound.getWidth());
        bc.bound.y = y + CenterMath.offsetY(tileHeightSize, bc.bound.getHeight());

        pc.position.set(bc.bound.x, bc.bound.y, pc.position.z);

    }

    public Vector3 getPositionUsingCoordinates(Coordinates coordinates, Rectangle rectangle){

        float x = originX + ((coordinates.getX()) * tileWidthSize);
        float y = originY + ((coordinates.getY()) * tileHeightSize);

        return new Vector3(x + CenterMath.offsetX(tileWidthSize, rectangle.getWidth()),
        y + CenterMath.offsetY(tileHeightSize, rectangle.getHeight()), 0);

    }

    public Coordinates getCoordinatesUsingPosition(Rectangle rectangle){


        System.out.println("GET COO USE POS");

        for(Rectangle r : rectangleMap.values().toArray()){
            if(r.contains(rectangle)){

                System.out.println("CONTAINS BRUH");

                return rectangleMap.findKey(r, false);
            }
        }

        return new Coordinates();

    }





    public boolean isMovementSquare(float x, float y, PositionComponent pc, BoundComponent bc){

        for(Rectangle r : movementRectangles){
            if(r.contains(x, y)){

                bc.bound.x = r.x + CenterMath.offsetX(r.getWidth(), bc.bound.getWidth());
                bc.bound.y = r.y + CenterMath.offsetY(r.getHeight(), bc.bound.getHeight());

                pc.position.set(bc.bound.x, bc.bound.y, pc.position.z);

                return true;
            }
        }

        return false;
    }



    private class Node {


        public Node(Coordinates coordinates){
            this.coordinates = coordinates;
        }

        public Node parent;

        public Coordinates coordinates;

        public int hValue;
        public int gValue;
        public int fValue;

        public void calcF(){
            fValue = gValue + hValue;
        }

        private void setHeuristic(Coordinates start, Coordinates goal) {

            int dx = Math.abs(start.getX() - goal.getX());
            int dy = Math.abs(start.getY() - goal.getY());

            int D = 1;

            this.hValue = D * (dx + dy);

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node node = (Node) o;

            return coordinates.equals(node.coordinates);

        }

        @Override
        public int hashCode() {
            return coordinates.hashCode();
        }

        // public int


    }


    public Array<Coordinates> returnSurroundingCoordinates(Coordinates coordinates){

        Array<Coordinates> array = new Array<Coordinates>();

        array.add(new Coordinates(coordinates.getX(), coordinates.getY() + 1));
        array.add(new Coordinates(coordinates.getX(), coordinates.getY() - 1));
        array.add(new Coordinates(coordinates.getX() + 1, coordinates.getY()));
        array.add(new Coordinates(coordinates.getX() - 1, coordinates.getY()));

        return array;

    }



    private OrderedMap<Coordinates, Node> setUpNodes(OrderedMap<Coordinates, Node> fillNodeMap, Coordinates start){

        for(Coordinates coordinates : coordinateMap.keys().toArray()) {
            if(!occupiedMap.keys().toArray().contains(coordinates, false) || coordinates.equals(start)) {
                fillNodeMap.put(coordinates, new Node(coordinates));
            }
        }

        return fillNodeMap;

    }


    public Array<Coordinates> findShortestPath(Coordinates start, Coordinates end){

        Array<Node> openList = new Array<Node>();
        Array<Node> closedList = new Array<Node>();

        int horiCostOfMovement = 10;

        OrderedMap<Coordinates, Node> allNodeMap = setUpNodes(new OrderedMap<Coordinates, Node>(), start);

        Node firstNode = allNodeMap.get(start);

        System.out.println(firstNode.coordinates);

        closedList.add(allNodeMap.get(start));

        //Could place this inside the Node set up.
        for(Node n: allNodeMap.values().toArray()) n.setHeuristic(n.coordinates, end);

        for(Coordinates c : returnSurroundingCoordinates(firstNode.coordinates)){
            //TODO test what happens if null
            Node potentialOpenListNode = allNodeMap.get(c);
            if(!closedList.contains(potentialOpenListNode, false) && potentialOpenListNode != null) {
                potentialOpenListNode.parent = firstNode;
                potentialOpenListNode.gValue = horiCostOfMovement;
                openList.add(potentialOpenListNode);
                potentialOpenListNode.calcF();
            }
        }



        boolean test = false;

        for(int i = 0; i < 100; i++) {

            //System.out.println(openList.size);

            //Find lowers f value
            openList.sort(new Comparator<Node>() {
                @Override
                public int compare(Node node, Node t1) {

                    Integer n = node.fValue;
                    Integer n2 = t1.fValue;

                    return n.compareTo(n2);
                }
            });


            if(openList.size == 0) return new Array<Coordinates>();

            Node nextNode = openList.first();

            closedList.add(nextNode);
            openList.removeValue(nextNode, false);

            for (Coordinates c : returnSurroundingCoordinates(nextNode.coordinates)) {


                //This is to prevent a stack over flow error (Most likely due to the way return surrounding coordinates
                //Does not account for nodes of the closed list. A way to prevent this would be to
                //check if the coordinate selected belongs to any node on the closed list possibly?
                if(c.equals(start)){
                    continue;
                }

                if(c.equals(end)) {
                    test = true;

                    System.out.println("C is " + c);
                    System.out.println("End is "+ end);

                  //  Node potentialOpenListNode = allNodeMap.get(c);
                  //  potentialOpenListNode.parent = nextNode;


                    Array<Coordinates> coordinatesArray = createCoordinateSequence(nextNode, new Array<Coordinates>());

                    for(Coordinates coordinates : coordinatesArray){
                        System.out.println(coordinates);
                    }

                    return coordinatesArray;

                }

                Node potentialOpenListNode = allNodeMap.get(c);


                if (potentialOpenListNode != null) {
                    if (potentialOpenListNode.gValue == 0) {
                        potentialOpenListNode.gValue = nextNode.gValue + horiCostOfMovement;
                        potentialOpenListNode.parent = nextNode;
                        openList.add(potentialOpenListNode);
                    } else if(openList.contains(potentialOpenListNode, false)){
                      //  System.out.println(potentialOpenListNode.gValue);

                        if (potentialOpenListNode.gValue > nextNode.gValue + horiCostOfMovement) {
                            potentialOpenListNode.gValue = nextNode.gValue + horiCostOfMovement;

                         //   potentialOpenListNode.parent = nextNode;
                        }
                    }
                    potentialOpenListNode.calcF();

                }

            }

            if(test) break;

        }


/*
        allNodeMap.sort(new Comparator<Node>() {
            @Override
            public int compare(Node node, Node t1) {
                return CoordinateSorter.SORT_BY_NEAREST(new Coordinates(0,0)).compare(node.coordinates, t1.coordinates);
            }
        });

*/

        return new Array<Coordinates>();






    }



    public Array<Coordinates> createCoordinateSequence(Node node, Array<Coordinates> coordinatesArray){

 /*       if(node.parent == node){
            return "";
        }*/

        if(node.parent != null){
            coordinatesArray.add(node.coordinates);
            return createCoordinateSequence(node.parent, coordinatesArray);
        }

        return coordinatesArray;

    }

}
