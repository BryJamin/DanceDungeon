package com.bryjamin.dancedungeon.factories.map;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.factories.map.event.EventManager;
import com.bryjamin.dancedungeon.factories.map.event.MapEvent;
import com.bryjamin.dancedungeon.factories.map.event.MapSection;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.random.WeightedObject;
import com.bryjamin.dancedungeon.utils.random.WeightedRoll;

import java.util.Comparator;
import java.util.Random;

/**
 * Created by BB on 16/01/2018.
 *
 * Class used for generating the Node Map used for navigation on the Map Screen and though the game.
 *
 */

public class MapGenerator {

    private float mapSectionWidth = Measure.units(9f);
    private float mapSectionHeight = Measure.units(32.5f);
    private float mapSectionGap = Measure.units(22.5f);

    private float mapStartX = Measure.units(5f);
    private float mapStartY = Measure.units(12.5f);
    private float minimumSpacing = Measure.units(7.5f);

    private static final int numberOfSections = 12;

    EventManager eventManager = new EventManager();

    private WeightedRoll<String> battleEventRoller = new WeightedRoll<String>(MathUtils.random);

    public GameMap generateGameMap(){
        Array<MapSection> mapSections = calculateMapNodeConnections(generateMapSections());
        setupMapEventTypes(mapSections);
        return new GameMap(mapSections);
    }






    /**
     * Given the Set of MapSection sets up what each Node will be, Either a shop, a battle or a rest.
     * @param mapSections - Set of MapSections
     */
    private void setupMapEventTypes(Array<MapSection> mapSections){

        for(String s : eventManager.getKeys()){
            battleEventRoller.addWeightedObjects(new WeightedObject<String>(s, 20));
        }


        WeightedRoll<MapEvent.EventType> eventRoller = new WeightedRoll<MapEvent.EventType>(new Random());

        WeightedObject<MapEvent.EventType> battleEvent =
                new WeightedObject<MapEvent.EventType>(MapEvent.EventType.BATTLE, 20);

        WeightedObject<MapEvent.EventType> shopEvent =
                new WeightedObject<MapEvent.EventType>(MapEvent.EventType.SHOP, 20);

        WeightedObject<MapEvent.EventType> restEvent =
                new WeightedObject<MapEvent.EventType>(MapEvent.EventType.REST, 20);

        eventRoller.addWeightedObjects(battleEvent, shopEvent, restEvent);

        Array<MapNode> flippableNodes = new Array<MapNode>();

        for(int i = 0; i < mapSections.size; i++){
            Array<MapNode> sectionNodes = mapSections.get(i).getMapNodes();
            for(int j = 0 ; j < sectionNodes.size; j++){
                if(i == 0) setEventType(sectionNodes.get(j), MapEvent.EventType.SHOP);
                else if(i == mapSections.size -1 ) sectionNodes.get(j).setEventType(MapEvent.EventType.BOSS);
                else {
                    setEventType(sectionNodes.get(j), MapEvent.EventType.BATTLE);
                    flippableNodes.add(sectionNodes.get(j));
                }
            }
        }

        flippableNodes.shuffle();
        flipNodesToEvent(flippableNodes, MapEvent.EventType.REST, 5);
        flipNodesToEvent(flippableNodes, MapEvent.EventType.SHOP, 5);

    }


    /**
     * Sets the event type of the map node, as well as set up it's id depending on which event type
     * is chosen.
     */
    public void setEventType(MapNode mapNode, MapEvent.EventType eventType){


        mapNode.setEventType(eventType);

        switch (eventType){

            case BATTLE:

                //TODO verify if the ids of the parent and successor, is not the same

                WeightedObject<String> roll = battleEventRoller.rollForWeight();
                mapNode.setEventId(roll.obj());

                int n = roll.getWeight() / 2;
                if(n <= 0) n = 1;

                roll.setWeight(n); //Lowers the chances of the event being picked

                break;


        }



    }











    private void flipNodesToEvent(Array<MapNode> flippableNodes, MapEvent.EventType eventType, int count){

        int i = 0;

        while(flippableNodes.size > 0 && i < count){
            MapNode potentialFlip = flippableNodes.removeIndex(0);
            if(calculateChanceOfPlacement(potentialFlip, eventType) > MathUtils.random.nextInt(100) + 1) {
                potentialFlip.setEventType(eventType);
            }

            i++;
        };

    }


    public float calculateChanceOfPlacement(MapNode node, MapEvent.EventType eventType){

        int secondaryNodeSize = 0;

        for(MapNode child : node.getSuccessors()){
            secondaryNodeSize += child.getSuccessors().size;
           // System.out.println("childe size " + child.getSuccessors().size);
        }

        for(MapNode parent : node.getParents()){
            secondaryNodeSize += parent.getParents().size;
          //  System.out.println("p size " + parent.getParents().size);
        }

        float number;

        if(secondaryNodeSize == 0) return -100; //TODO decide how to handle this, since technically this should never happen
        else {
            number = 100.0f / secondaryNodeSize;
        }

        float score = 0;

        for(MapNode child : node.getSuccessors()){
            if(child.getEventType() == eventType) return -100;
            if(child.getSuccessors().size > 0)
                score += calculateLevel2Score(child.getSuccessors(), eventType, number);
        }

        for(MapNode parent : node.getParents()){
            if(parent.getEventType() == eventType) return -100;
            if(parent.getParents().size > 0)
                score += calculateLevel2Score(parent.getParents(), eventType, number);
        }

        return score;


    }

    public float calculateLevel2Score(Array<MapNode> nodes, MapEvent.EventType eventType, float number){

        float score = 0;

        for(MapNode node : nodes){
            if(node.getEventType() != eventType) score += number;
        }

        return score;

    }



    private Array<MapSection> generateMapSections(){

        Array<MapSection> mapSections = new Array<MapSection>();

        for(int i = 0; i < numberOfSections; i++){

            mapSections.add(new MapSection(mapStartX + (i * mapSectionGap),
                    mapStartY,
                    mapSectionWidth,
                    mapSectionHeight,
                    minimumSpacing,
                    (i == numberOfSections - 1) ? 1 : MathUtils.random(2, 4)));


        }

        return mapSections;

    }


    /**
     * Walks through all sections in the array and links all nodes from the left section to
     * the right section.
     *
     * This method verifies that there are no overlaps with lines.
     *
     * Two paths should not cross each other.
     *
     * @return
     */
    private Array<MapSection> calculateMapNodeConnections(Array<MapSection> sections){

        Array<MapSection> walkThroughSectionArray = new Array<MapSection>(sections);

        while (walkThroughSectionArray.size > 1){

            MapSection leftSection = walkThroughSectionArray.get(0);
            MapSection rightSection = walkThroughSectionArray.get(1);

            Array<Line> connectedLines = new Array<Line>();
            Array<MapNode> leftMapNodeArray = leftSection.getMapNodes();
            Array<MapNode> rightMapNodeArray = rightSection.getMapNodes();

            firstPass(leftMapNodeArray, rightMapNodeArray, connectedLines);
            secondPass(leftMapNodeArray, rightMapNodeArray, connectedLines);

            //Remove first section and move onto the next
            walkThroughSectionArray.removeIndex(0);
        }

        return sections;
    }




    /**
     * Looks at the LeftMost set of nodes and connects them to the right most set of nodes, sorted by checking the nearest
     * nodes first.
     *
     * In future, may be wise to check for potential intersections, but as the generations stand this is not neccessary
     *
     * //TODO create a test which checks each section to ensure no connected lines, cause intersections
     *
     */
    private void firstPass(Array<MapNode> leftMapNodeArray, Array<MapNode> rightMapNodeArray, Array<Line> connectedLines){

        for(int i = 0; i < leftMapNodeArray.size; i++) {

            final MapNode current = leftMapNodeArray.get(i);

            //Sort the right Node array by the nearest node, to the currently selected left Node
            rightMapNodeArray.sort(NEAREST_NODE(current));

            MapNode nearest = rightMapNodeArray.first();
            current.addSuccessors(rightMapNodeArray.first());
            connectedLines.add(new Line(current.getPosition(), nearest.getPosition()));
        }

    }

    private Comparator<MapNode> NEAREST_NODE (final MapNode node){

        return new Comparator<MapNode>() {
            @Override
            public int compare(MapNode n1, MapNode n2) {
                return Float.compare(n1.getPosition().dst(node.getPosition()),
                        n2.getPosition().dst(node.getPosition()));
            }
        };

    }


    /**
     * Checks the child nodes in the second section, to see if they have any parents connected to them
     * If they do not the child looks for the nearest parent, it can connect to, that also doesn't cause any intersections,
     * with other lines
     */
    private void secondPass(Array<MapNode> leftMapNodeArray, Array<MapNode> rightMapNodeArray, Array<Line> connectedLines){

        for(int i = 0; i < rightMapNodeArray.size; i++){

            final MapNode currentChild = rightMapNodeArray.get(i);

            if(currentChild.getParentSize() > 0) continue; //Ignore nodes that have a parent

            leftMapNodeArray.sort(NEAREST_NODE(currentChild));

            for(MapNode node : leftMapNodeArray){

                int intersectionCount = 0;

                //Check for each connected Line, if a line going to the current node would cause an intersection or not
                for(Line line : connectedLines){
                    if(line.intersect(new Line(node.getPosition(), currentChild.getPosition()))){
                        intersectionCount++;
                    };
                }

                //If two line segments end at the same point, that counts as an intersection

                //This is the only time an intersection, is allowed within the Map.
                //The number of lines currently connected to a node, would be equivalent to it's children

                //TODO it may be more prudent to generate the connected lines again within this method, instead
                //TODO of relying on the ones from outside the method?
                if(intersectionCount <= node.getSuccessors().size){
                    node.addSuccessors(currentChild);
                    connectedLines.add(new Line(node.getPosition(), currentChild.getPosition()));
                    break;
                }
            }
        }
    }




    public class Line {

        public Vector2 v1;
        public Vector2 v2;

        public Line(Vector2 v1, Vector2 v2){
            this.v1 = v1;
            this.v2 = v2;
        }

        public boolean intersect(Line line){
            return Intersector.intersectSegments(v1, v2, line.v1, line.v2, new Vector2());
            //return Intersector.intersectLines(v1, v2, line.v1, line.v2, vector2);
        }

    }


}
