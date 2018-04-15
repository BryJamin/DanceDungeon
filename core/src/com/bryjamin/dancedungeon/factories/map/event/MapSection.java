package com.bryjamin.dancedungeon.factories.map.event;

import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.factories.map.MapNode;

import java.util.Random;

/**
 * Created by BB on 16/01/2018.
 *
 * This class is used to split the Map into different 'sections'
 *
 * It stores a set of different MapNodes and tries to space them out evenly based on the information given
 *
 */

public class MapSection {


    private int nodeNumber;
    private Array<MapNode> mapNodes = new Array<MapNode>();

    private float startX;
    private float startY;

    private float width;
    private float height;

    private float minimumSpacing;

    /**
     * No Arg Constructor for Json
     */
    public MapSection(){}

    /**
     * Constructor for MapSection
     * @param startX - The x starting position of the sections
     * @param startY - The y starting position of the section
     * @param width - The width of the section
     * @param height - The height of the section
     * @param minimumSpacing - The minmum amount of the space that should be between each node in the section
     * @param nodeNumber - The number of nodes that needs to be put into the section
     */
    public MapSection(float startX, float startY, float width, float height, float minimumSpacing, int nodeNumber) {
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
        this.minimumSpacing = minimumSpacing;
        this.nodeNumber = nodeNumber;
        this.generateNodePositionsWithinSection();
    }

    /**
     * Generates and randomly places 'nodes' within the given section based on the number of nodes
     *
     * Throws a runtime error if the generation is incorrect
     */
    public void generateNodePositionsWithinSection() {

        Random random = new Random();

        for (int i = 0; i < nodeNumber; i++) {

            MapNode node = new MapNode();

            float maxX = startX + width;
            float minX = startX;

            //Creates a random position (X) between minimum and maximum value
            node.setPosX(random.nextFloat() * (maxX - minX) + minX);


            //Areas within sections are currently divided vertically.
            float maxY = startY + ((height / nodeNumber) * (i + 1));
            float minY = startY + ((height / nodeNumber) * i); //Nodes are set in different sections


            //Applies minimum spacing to avoid nodes overlapping each other visually.
            if (i != 0) {
                MapNode prev = mapNodes.get(i - 1);
                if (minY < prev.getPosY() + minimumSpacing)
                    minY = prev.getPosY() + minimumSpacing;
            }

            //TODO handle this a bit better
            if (maxY < minY)
                throw new RuntimeException("Generation failed minimum Y larger than maximum Y. Minimum Spacing may be too large");

            float y = random.nextFloat() * (maxY - minY) + minY;

            node.setPosY(y);

            mapNodes.add(node);

        }


        //TODO this makes it so with larger generation you can create more 'spaced out' looking setsups
        //TODO however, this should be a varaible or an option
        if (mapNodes.size > 3) {

            if (random.nextInt(2) > 0) {
                mapNodes.removeIndex(random.nextInt(mapNodes.size));
            }
        }

    }

    public Array<MapNode> getMapNodes() {
        return mapNodes;
    }


    public float getStartX() {
        return startX;
    }

    public float getStartY() {
        return startY;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}



