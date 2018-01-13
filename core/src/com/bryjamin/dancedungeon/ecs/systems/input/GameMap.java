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

            System.out.println("section height = " +(height / nodeNumber));
            System.out.println("minimum spacing is " + minimumSpacing);

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

                System.out.println("i is: " + i);

                if(i != 0){
                    MapNode prev = mapNodes.get(i - 1);
                    System.out.println("prev minY is: "+ minY);
                    System.out.println("prev posY is: " + prev.getPosY());
                    System.out.println("min spacing: " + minimumSpacing);
                    if(minY < prev.getPosY() + minimumSpacing)
                        minY = prev.getPosY() + minimumSpacing;
                }


                System.out.println("minY: + " + minY);
                System.out.println("maxY: + " + maxY + "\n");

                if(maxY < minY) throw new RuntimeException("Generation failed minimum Y larger than maximum Y");

                //TODO callibrate for

                float y = random.nextFloat() * (maxY - minY) + minY;

                System.out.println("chosen y: for " + i + ", is: " + y);
                node.setPosY(y);

                mapNodes.add(node);

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
