package com.bryjamin.dancedungeon.utils.math;

/**
 * Created by BB on 28/10/2017.
 */

public class CoordinateMath {


    public static boolean isNextTo(Coordinates c1, Coordinates c2){


        boolean nearByX = (c1.getX() + 1 == c2.getX() || c1.getY() - 1 == c2.getX()) && c1.getY() == c2.getY();
        boolean nearByY = (c1.getY() + 1 == c2.getY() || c1.getY() - 1 == c2.getY()) && c1.getX() == c2.getX();

        return nearByX || nearByY;

    }

}
