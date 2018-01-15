package com.bryjamin.dancedungeon.ecs.systems.input;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.bryjamin.dancedungeon.utils.Measure;

import java.util.Random;

/**
 * Created by BB on 07/01/2018.
 */

public class GameMap {

    private MapNode currentMapNode;

    private Array<MapEvent> mapEvents = new Array<MapEvent>();

    private OrderedMap<MapNode, MapEvent> nodeMap = new OrderedMap<MapNode, MapEvent>();

    public GameMap(){
        mapEvents = new Array<MapEvent>();
    }

    //For now
    public GameMap(MapEvent... mapEvents){
        this.mapEvents.addAll(mapEvents);
    }

    public Array<MapEvent> getMapEvents() {
        return mapEvents;
    }

/*    public Array<MapEvent> getNextMapEvents(){

        Array<MapEvent> relatedEvents = new Array<MapEvent>();

        for(MapNode mapNode : currentMapNode.successors){

        }



    }*/

    public Array<MapNode> getNextNodes(){
        return currentMapNode.successors;
    }

    public MapEvent setNodeAndGetNextEvent(MapNode mapNode){
        currentMapNode = mapNode;
        return nodeMap.get(mapNode);
    }


    public MapEvent getNextEvent() {
        if(mapEvents.size > 0){
            return mapEvents.removeIndex(0);
        }
        return new TestEvent();
    }



    public void calculateMap(Array<Section> sections){


        Array<Section> walkThroughSectionArray = new Array<Section>(sections);

        while (walkThroughSectionArray.size > 1){

            Section leftSection = walkThroughSectionArray.get(0);
            Section rightSection = walkThroughSectionArray.get(1);

            Array<MapNode> connectedNodes = new Array<MapNode>();

            Array<MapNode> leftMapNodeArray = leftSection.getMapNodes();


        }

        //Sort sections? Or assume they are the right way
        //Throw an error if not in order?


        //TODO note you, could when creating sections have sections of size 5 nodes, but then, also
        //TODO have the random chance of dropping between 2 and 3, to make the nodes appear more spractic
        //TODO similar to how I tested with the 4 nodes dropping 1 option


        //TODO Take a list of sections.

        //TODO it is assume these section origins go from left to right

        //TODO if one section startX is before the other section startX, Fail.

        //TODO Compare the sizes of the left section to the right section


        //TODO if left size is 2, and right size is 4. There need to be at minimum 2 connections
        //TODO each.


        //TODO it may be better to go top to bottom when creating links

        //TODO the number of 'potential' intersections determines whether ot not a link to a node is valid
        //TODO currently I'll say the number is 2.

        //TODO by 'potential' intersections it may be best to cycle through each node and  check.

        //TODO however the probability of even checking a node is increased based on 'relative distance?' (might not do this one)

        //TODO it may be better to pick nodes based on least number of potential intersections



    }



    public static class Section {


        private int nodeNumber;
        private Array<MapNode> mapNodes = new Array<MapNode>();

        private float startX;
        private float startY;

        private float width;
        private float height;

        private float minimumSpacing = Measure.units(10f);

        public Section(float startX, float startY, float width, float height, int nodeNumber){
            this.startX = startX;
            this.startY = startY;
            this.width = width;
            this.height = height;
            this.nodeNumber = nodeNumber;
        }


        public void generateNodePositions(){

            Random random = new Random();

            for(int i = 0; i < nodeNumber; i++){

                MapNode node = new MapNode();

                float maxX = startX + width;
                float minX = startX;

                node.setPosX(random.nextFloat() * (maxX - minX) + minX);

                //float nodeStartY = startY * i; //Nodes are set in different sections

                //


                //Separates the nodes into their appropiate sections
                float maxY = startY + ((height / nodeNumber) * (i + 1));
                float minY = startY + ((height / nodeNumber) * i); //Nodes are set in different sections


                if(i != 0){
                    MapNode prev = mapNodes.get(i - 1);
                    if(minY < prev.getPosY() + minimumSpacing)
                        minY = prev.getPosY() + minimumSpacing;
                }

                if(maxY < minY) throw new RuntimeException("Generation failed minimum Y larger than maximum Y");

                //TODO callibrate for

                float y = random.nextFloat() * (maxY - minY) + minY;

                node.setPosY(y);

                mapNodes.add(node);

            }


            if(mapNodes.size > 3){

                System.out.println("Was 4");

                if(random.nextInt(2) > 0){
                    mapNodes.removeIndex(random.nextInt(mapNodes.size));
                };
            }

        }

        public Array<MapNode> getMapNodes() {
            return mapNodes;
        }
    }







    //I'm trying to make a directed graph

    public static class MapNode {

        private float posX;
        private float posY;

       // Array<MapNode> previousNodes;
        public Array<MapNode> successors = new Array<MapNode>();

        public MapEvent mapEvent;

        public void addSuccessors(MapNode... mapNode){
            this.successors.addAll(mapNode);
        }

        public void addSuccessors(Array<MapNode> mapNodes){
            this.successors.addAll(mapNodes);
        }

        public float getPosX() {
            return posX;
        }

        public void setPosX(float posX) {
            //System.out.println("setPOsx " + posX);
            this.posX = posX;
        }

        public Array<MapNode> getSuccessors() {
            return successors;
        }

        public float getPosY() {
            return posY;
        }

        public void setPosY(float posY) {
            this.posY = posY;
        }

        public MapEvent getMapEvent() {
            return mapEvent;
        }

        public void setMapEvent(MapEvent mapEvent) {
            this.mapEvent = mapEvent;
        }
    }


}
