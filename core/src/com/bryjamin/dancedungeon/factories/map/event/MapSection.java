package com.bryjamin.dancedungeon.factories.map.event;

import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.factories.map.MapNode;
import com.bryjamin.dancedungeon.utils.Measure;

import java.util.Random;

/**
 * Created by BB on 16/01/2018.
 */

public class MapSection {


    private int nodeNumber;
    private Array<MapNode> mapNodes = new Array<MapNode>();

    private float startX;
    private float startY;

    private float width;
    private float height;

    private float minimumSpacing = Measure.units(10f);

    public MapSection(float startX, float startY, float width, float height, int nodeNumber) {
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
        this.nodeNumber = nodeNumber;
        this.generateNodePositionsWithinSection();
    }

    /**
     * Generates and randomly places 'nodes' within the given section based on nodeNumber
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
}



