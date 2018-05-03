package com.bryjamin.dancedungeon.utils.math;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedSet;
import com.bryjamin.dancedungeon.utils.enums.Direction;

/**
 * Created by BB on 28/10/2017.
 */

public class CoordinateMath {


    public static boolean isNextTo(Coordinates c1, Coordinates c2){


        boolean nearByX = (c1.getX() + 1 == c2.getX() || c1.getX() - 1 == c2.getX()) && c1.getY() == c2.getY();
        boolean nearByY = (c1.getY() + 1 == c2.getY() || c1.getY() - 1 == c2.getY()) && c1.getX() == c2.getX();

        return nearByX || nearByY;

    }


    /**
     * Checks if the coordinate c1 is in range of coordinate c2,
     *
     * Range in this instance uses only horizontal and vertical movement.
     *
     * A range of 1 for coordinate (0,0), returns true for values (0,0) (1,0), (0,1), (-1,0), (0, -1)
     *
     * For coordinate (1,1) the range would need to be 2, for c1 = (0,0) to return true.
     */
    public static boolean isWithinRange(Coordinates c1, Coordinates c2, int range){

        //Convert c1 and c2 into 0 co-ordinates
        int normX = c2.getX() - c1.getX();
        int normY = c2.getY() - c1.getY();

        return Math.abs(normX) + Math.abs(normY) <= range;

    }


    /**
     * Returns an array of coordinates within the range given.
     *
     * The range used here assumes 1 coordinate step is equal to 1 range
     *
     */
    public static Array<Coordinates> getCoordinatesInMovementRange(Coordinates coordinates, int range){

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


    /**
     * Square Range assumes 1 range is within 1 square of the given co-ordinate.
     *
     * This means for (0,0) (1,1) is included in the square range.
     */
    public static Array<Coordinates> getCoordinatesInSquareRange(Coordinates coordinates, int range){

        OrderedSet<Coordinates> coordinatesArray = new OrderedSet<Coordinates>();

        if(range <= 0) return coordinatesArray.orderedItems();

        for(int x = 0; x <= range; x++){
            for(int y = 0; y <= range; y++){

                if(x == 0 && y == 0) continue;
                if(x > range || y > range) continue;

                coordinatesArray.add(new Coordinates(coordinates.getX() + x, coordinates.getY() + y));
                coordinatesArray.add(new Coordinates(coordinates.getX() + x, coordinates.getY() - y));
                coordinatesArray.add(new Coordinates(coordinates.getX() - x, coordinates.getY() + y));
                coordinatesArray.add(new Coordinates(coordinates.getX() - x, coordinates.getY() - y));
            }
        }

        return coordinatesArray.orderedItems();

    }


    public static Array<Coordinates> getCoordinatesInLine(Coordinates coordinates, int range){
        return getCoordinatesInLine(coordinates, 0, range);

    }

    /**
     * Gets co-ordinates in line with the given co-ordinate based on the range
     *
     * For(0,0), Coordinates (5,0) (0,5) and (-5, 0) would all be inline.
     *
     */
    public static Array<Coordinates> getCoordinatesInLine(Coordinates coordinates, int minRange, int maxRange){

        OrderedSet<Coordinates> coordinatesArray = new OrderedSet<Coordinates>();
        if(minRange <= 0) return coordinatesArray.orderedItems();

        for(int i = minRange; i <= maxRange; i++){

            coordinatesArray.add(new Coordinates(coordinates.getX() + i, coordinates.getY()));
            coordinatesArray.add(new Coordinates(coordinates.getX() - i, coordinates.getY()));
            coordinatesArray.add(new Coordinates(coordinates.getX(), coordinates.getY() + i));
            coordinatesArray.add(new Coordinates(coordinates.getX(), coordinates.getY() - i));

        }

        return coordinatesArray.orderedItems();

    }


    /**
     * Based on the direction increases the coordinate of c2 by one and sets c1, to those coordinates.
     */
    public static void increaseCoordinatesByOneUsingDirection(Direction d, Coordinates c1, Coordinates c2){

        switch (d) {
            case DOWN:
                c1.set(c2.getX(), c2.getY() - 1);
                break;
            case UP:
                c1.set(c2.getX(), c2.getY() + 1);
                break;
            case LEFT:
                c1.set(c2.getX() - 1, c2.getY());
                break;
            case RIGHT:
                c1.set(c2.getX() + 1, c2.getY());
                break;
        }

    }


    /**
     * Finds in what direction Coordinate two lies in regards to Coordinate one.
     * @return
     */
    public static Direction getDirectionOfCoordinate(Coordinates c1, Coordinates c2){
        if(c1.getX() == c2.getX()){
            return c1.getY() > c2.getY() ? Direction.DOWN : Direction.UP;
        } else {
            return c1.getX() > c2.getX() ? Direction.LEFT : Direction.RIGHT;
        }
    }

}
