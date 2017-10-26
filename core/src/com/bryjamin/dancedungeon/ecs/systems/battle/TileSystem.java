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

    //Mpa used to show if a space is occupied
    private OrderedMap<Coordinates, Entity> occupiedMap = new OrderedMap<Coordinates,  Entity>();


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

                System.out.println();

                if(i == columns - 1){
                    movementRectangles.add(new Rectangle(originX + i * tileWidthSize,
                            originY + j * tileHeightSize,
                            tileWidthSize,
                            tileHeightSize));
                }
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





    public Array<Coordinates> findShortestPath(Coordinates start, Coordinates end){

        Array<Node> openList = new Array<Node>();

        Array<Node> closedList = new Array<Node>();

        int horiCostOfMovement = 10;

        OrderedMap<Coordinates, Node> allNodeMap = new OrderedMap<Coordinates, Node>();

        for(Coordinates coordinates : coordinateMap.keys().toArray()) {
            allNodeMap.put(coordinates, new Node(coordinates));
        }

        for(Node n: allNodeMap.values().toArray()){
            n.setHeuristic(n.coordinates, end);
            if(n.coordinates.equals(start)){
                closedList.add(n);
            }
        }


        for(Node n : closedList){
            for(Coordinates c : returnSurroundingCoordinates(n.coordinates)){
                //TODO test what happens if null
                Node potentialOpenListNode = allNodeMap.get(c);
                if(!closedList.contains(potentialOpenListNode, false) && potentialOpenListNode != null) {
                    potentialOpenListNode.parent = n;
                    potentialOpenListNode.gValue = horiCostOfMovement;


                    openList.add(potentialOpenListNode);

                    if(potentialOpenListNode.parent == null){
                        potentialOpenListNode.parent = n;
                    }

                    potentialOpenListNode.calcF();

                }
            }

        }

        for(Node n : closedList){
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

/*            for (Node n : openList) {
                System.out.println(n.coordinates);
                System.out.println(n.gValue);
                System.out.println("f value" + n.fValue);
            }*/
            System.out.println("i is + " + i);

            Node nextNode = openList.first();
/*            System.out.println(nextNode.coordinates);
            System.out.println(nextNode.gValue);
            System.out.println("f value" + nextNode.fValue);*/

            closedList.add(nextNode);
            openList.removeValue(nextNode, false);

            for (Coordinates c : returnSurroundingCoordinates(nextNode.coordinates)) {

                if(c.equals(start)){
                    continue;
                }

                if(c.equals(end)) {
                    test = true;


                    Node potentialOpenListNode = allNodeMap.get(c);
                    potentialOpenListNode.parent = nextNode;
                    System.out.println(printNodeSequence(potentialOpenListNode));



                    break;
                }

                Node potentialOpenListNode = allNodeMap.get(c);


                if (potentialOpenListNode != null) {
                    if (potentialOpenListNode.gValue == 0) {
                        potentialOpenListNode.gValue = nextNode.gValue + horiCostOfMovement;
                        potentialOpenListNode.parent = nextNode;
                        openList.add(potentialOpenListNode);
                    } else if(openList.contains(potentialOpenListNode, false)){
                        System.out.println(potentialOpenListNode.gValue);

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




        return null;










    }



    public String printNodeSequence(Node node){

 /*       if(node.parent == node){
            return "";
        }*/

        if(node.parent != null){
            return node.coordinates.toString() + "\n" + printNodeSequence(node.parent);
        }

        return node.coordinates.toString();

    }

}
