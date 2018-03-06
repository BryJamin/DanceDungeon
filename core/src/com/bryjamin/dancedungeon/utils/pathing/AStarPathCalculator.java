package com.bryjamin.dancedungeon.utils.pathing;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Queue;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

import java.util.Comparator;

/**
 * Created by BB on 28/10/2017.
 */

public class AStarPathCalculator {


    public OrderedMap<CoordinateComponent, Node> componentNodeOrderedMap = new OrderedMap<CoordinateComponent, Node>();

    public Array<Coordinates> unavailableCoordinates = new Array<Coordinates>();

    public Array<Coordinates> availableCoordinates = new Array<Coordinates>();

    //Coordinates occupied by allies
    public Array<Coordinates> alliedCoordinates = new Array<Coordinates>();

    private static final int HORIZONTAL_COST = 10;

    private boolean strictMaxRange;

    public void setStrictMaxRange(boolean strictMaxRange) {
        this.strictMaxRange = strictMaxRange;
    }

    private static final Comparator<Node> nodeFValueComparator = new Comparator<Node>() {
        @Override
        public int compare(Node node, Node t1) {
            Integer n = node.fValue;
            Integer n2 = t1.fValue;

            return n.compareTo(n2);
        }
    };



    public AStarPathCalculator(Array<Coordinates> availableCoordinates, Array<Coordinates> unavailableCoordinates){
        this.availableCoordinates = availableCoordinates;
        this.unavailableCoordinates = unavailableCoordinates;
    }

    public AStarPathCalculator(Array<Coordinates> availableCoordinates, Array<Coordinates> unavailableCoordinates, Array<Coordinates> alliedCoordinates){
        this.availableCoordinates = availableCoordinates;
        this.unavailableCoordinates = unavailableCoordinates;
        this.alliedCoordinates = alliedCoordinates;

        for(Coordinates c : alliedCoordinates){
            this.unavailableCoordinates.removeValue(c, false);
        }

    }


    /**
     * Given a set of different target Coordinates find the shortest path to one of them.
     * @param fillQueue - Queue to be filled
     * @param start - Start Coordinate
     * @param targets - Target Coordinates
     * @param maxRange - Max range of the final queue. (Set to -1 for infinite range)
     * @return - True if a path can be found to at least one of the targets
     */
    public boolean findShortestPathMultipleChoice(Queue<Coordinates> fillQueue, Coordinates start, Array<Coordinates> targets, int maxRange){


        Array<Queue<Coordinates>> queueArray = new Array<Queue<Coordinates>>();

        for(Coordinates c : targets){
            Queue<Coordinates> coordinatesQueue = new Queue<Coordinates>();
            if(findShortestPath(coordinatesQueue, start, c, maxRange)) {//If a path is found, (if there is a max range evalued if the path is valid)
                queueArray.add(coordinatesQueue);
            }
        }

        if(queueArray.size == 0) return false;


        queueArray.sort(new Comparator<Queue<Coordinates>>() {
            @Override
            public int compare(Queue<Coordinates> q1, Queue<Coordinates> q2) {
                return q1.size < q2.size ? -1 : q1.size == q2.size ? 0 : 1;
            }
        });

        for(Coordinates c : queueArray.first()){
            fillQueue.addLast(c);
        }

        //queueArray.first();

        return true;

    }


    public boolean findShortestPath(Queue<Coordinates> fillQueue, Coordinates start, Coordinates end, int maxRange){

        Array<Node> openList = new Array<Node>();
        Array<Node> closedList = new Array<Node>();

        OrderedMap<Coordinates, Node> allNodeMap = setUpNodes(new OrderedMap<Coordinates, Node>(), start);

        Node firstNode = allNodeMap.get(start);

        closedList.add(allNodeMap.get(start));

        //Could place this inside the Node set up.
        for(Node n: allNodeMap.values().toArray()) n.setHeuristic(n.coordinates, end);

        //If the final coordinate is occupied by either an ally or enemy return false
        if(unavailableCoordinates.contains(end, false) || alliedCoordinates.contains(end, false)) {
            return false;
        }

        for(Coordinates c : returnSurroundingCoordinates(firstNode.coordinates)){

            if(c.equals(end)){
                fillQueue.addLast(end);
                return true;
            }

            //TODO test what happens if null
            Node potentialOpenListNode = allNodeMap.get(c);
            if(!closedList.contains(potentialOpenListNode, false) && potentialOpenListNode != null) {
                potentialOpenListNode.parent = firstNode;
                potentialOpenListNode.gValue = HORIZONTAL_COST;
                openList.add(potentialOpenListNode);
                potentialOpenListNode.calcF();
            }
        }

        int count = 0;

        while(openList.size != 0){

            count++;

            Node nextNode = getNextNode(openList, closedList);
            Array<Coordinates> surroundingCoordinates = returnSurroundingCoordinates(nextNode.coordinates);

            //TODO convert this is use an array? So you can have a range of coordinates you are trying to get it instead of the one?
            //TODO this would mean you wouldn't need this 'isDestinationNextTo' boolean
            if(surroundingCoordinates.contains(end, false)) {
                createCoordinateSequence(nextNode, fillQueue);
                fillQueue.addLast(end);

                if(maxRange >= 0){

                    //Because you scan all coordinates within a certain range,
                    //It is possible to find a route to a coordinate that is greater than the maximum range
                    //For targeting purposes these are removed

                    //For enemy purposes it simply reduces the length of the path, since the AI, needs
                    //The shortest path, it just can't reach all the way yet.

                    if(strictMaxRange) { //This is for players and targeting
                        if (fillQueue.size > maxRange) return false;
                    } else {
                        while (fillQueue.size > maxRange) { //This is for players
                            fillQueue.removeLast();
                        }
                    }

                    if(alliedCoordinates.contains(fillQueue.last(), false)) //prevents paths ending on allied coordinates
                        return false;
                }





                return true;
            }

            for (Coordinates c : returnSurroundingCoordinates(nextNode.coordinates)) {

                //This is to prevent a stack over flow error (Most likely due to the way return surrounding coordinates
                //Does not account for nodes of the closed list. A way to prevent this would be to
                //check if the coordinate selected belongs to any node on the closed list possibly?

                /*               if(c.equals(start)){
                    continue;
                }
*/
                Node potentialOpenListNode = allNodeMap.get(c);
                if(closedList.contains(potentialOpenListNode, true)) continue;


                if (potentialOpenListNode != null) {
                    if (potentialOpenListNode.gValue == 0) {
                        potentialOpenListNode.gValue = nextNode.gValue + HORIZONTAL_COST;
                        potentialOpenListNode.parent = nextNode;
                        openList.add(potentialOpenListNode);
                    } else if(openList.contains(potentialOpenListNode, false)){

                        if (potentialOpenListNode.gValue > nextNode.gValue + HORIZONTAL_COST) {
                            potentialOpenListNode.gValue = nextNode.gValue + HORIZONTAL_COST;
                            //   potentialOpenListNode.parent = nextNode;
                        }
                    }
                    potentialOpenListNode.calcF();

                }

            }
        }

        return false;
    }


    /**
     * Gets the next node to be evaluated by the A* algorithm.
     *
     * This node selected if the one with the smallest F Value.
     *
     * This node is added to the closed list and removed from the open list.
     * @param openList
     * @param closedList
     * @return
     */
    private Node getNextNode(Array<Node> openList, Array<Node> closedList){

        openList.sort(nodeFValueComparator);
        Node nextNode = openList.first();
        closedList.add(nextNode);
        openList.removeValue(nextNode, false);

        return nextNode;
    }


    private OrderedMap<Coordinates, Node> setUpNodes(OrderedMap<Coordinates, Node> fillNodeMap, Coordinates start){

        for(Coordinates coordinates : availableCoordinates) {
            if(!unavailableCoordinates.contains(coordinates, false) || coordinates.equals(start)) {
                fillNodeMap.put(coordinates, new Node(coordinates));
            }
        }

        return fillNodeMap;

    }


    public Queue<Coordinates> createCoordinateSequence(Node node, Queue<Coordinates> coordinatesQueue){

        if(node.parent != null){
            coordinatesQueue.addFirst(node.coordinates);
            return createCoordinateSequence(node.parent, coordinatesQueue);
        }

        return coordinatesQueue;

    }



    private Array<Coordinates> returnSurroundingCoordinates(Coordinates coordinates){

        Array<Coordinates> array = new Array<Coordinates>();

        array.add(new Coordinates(coordinates.getX(), coordinates.getY() + 1));
        array.add(new Coordinates(coordinates.getX(), coordinates.getY() - 1));
        array.add(new Coordinates(coordinates.getX() + 1, coordinates.getY()));
        array.add(new Coordinates(coordinates.getX() - 1, coordinates.getY()));

        return array;

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


    }



}
