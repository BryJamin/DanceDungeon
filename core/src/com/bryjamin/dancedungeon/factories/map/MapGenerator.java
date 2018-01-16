package com.bryjamin.dancedungeon.factories.map;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.factories.map.event.MapSection;
import com.bryjamin.dancedungeon.utils.Measure;

import java.util.Comparator;

/**
 * Created by BB on 16/01/2018.
 *
 * Class used for generating the Node Map used for navigation on the Map Screen and though the game.
 *
 */

public class MapGenerator {



    public GameMap generateGameMap(){
        Array<MapSection> mapSections = calculateMap(generateMapSections());
        return new GameMap(mapSections);
    }




    public Array<MapSection> generateMapSections(){

        float width = Measure.units(7.5f);
        float height = Measure.units(50f);
        float gap = Measure.units(10f);

        float x = Measure.units(7.5f);
        float y = Measure.units(5f);

        Array<MapSection> mapSections = new Array<MapSection>();

        int totalSections = 4;

        for(int i = 0; i < totalSections; i++){

            mapSections.add(new MapSection(x + (i * Measure.units(20f)),
                    y,
                    width,
                    height,
                    MathUtils.random(2, 5)));


        }

        return mapSections;

    }





    public Array<MapSection> calculateMap(Array<MapSection> sections){

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
