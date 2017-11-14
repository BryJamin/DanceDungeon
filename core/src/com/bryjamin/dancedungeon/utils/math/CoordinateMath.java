package com.bryjamin.dancedungeon.utils.math;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedSet;

/**
 * Created by BB on 28/10/2017.
 */

public class CoordinateMath {


    public static boolean isNextTo(Coordinates c1, Coordinates c2){


        boolean nearByX = (c1.getX() + 1 == c2.getX() || c1.getX() - 1 == c2.getX()) && c1.getY() == c2.getY();
        boolean nearByY = (c1.getY() + 1 == c2.getY() || c1.getY() - 1 == c2.getY()) && c1.getX() == c2.getX();

        return nearByX || nearByY;

    }

    public static boolean isWithinRange(Coordinates c1, Coordinates c2, int range){

        boolean isWithinXRange = Math.abs(c1.getX() - c2.getX()) <= range;
        boolean isWithinYRange = Math.abs(c1.getY() - c2.getY()) <= range;

        return isWithinXRange && isWithinYRange;

    }


    public static Array<Coordinates> getCoordinatesInRange(Coordinates coordinates, int range){

        OrderedSet<Coordinates> coordinatesArray = new OrderedSet<Coordinates>();

        if(range <= 0) return coordinatesArray.orderedItems();

        for(int x = 0; x <= range; x++){
            for(int y = 0; y <= range; y++){

                if(x == 0 && y == 0) continue;
                if(x + y > range) continue;

                coordinatesArray.add(new Coordinates(coordinates.getX() + x, coordinates.getY() + y));
                coordinatesArray.add(new Coordinates(coordinates.getX() + x, coordinates.getY() - y));
                coordinatesArray.add(new Coordinates(coordinates.getX() - x, coordinates.getY() + y));
                coordinatesArray.add(new Coordinates(coordinates.getX() - x, coordinates.getY() - y));

            }
        }

        return coordinatesArray.orderedItems();

    }

    public static Array<Coordinates> getCoordinatesInLine(Coordinates coordinates, int range){

        OrderedSet<Coordinates> coordinatesArray = new OrderedSet<Coordinates>();
        if(range <= 0) return coordinatesArray.orderedItems();

        for(int i = 1; i <= range; i++){

            coordinatesArray.add(new Coordinates(coordinates.getX() + i, coordinates.getY()));
            coordinatesArray.add(new Coordinates(coordinates.getX() - i, coordinates.getY()));
            coordinatesArray.add(new Coordinates(coordinates.getX(), coordinates.getY() + i));
            coordinatesArray.add(new Coordinates(coordinates.getX(), coordinates.getY() - i));

        }

        return coordinatesArray.orderedItems();

    }

}
