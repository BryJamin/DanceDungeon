package com.bryjamin.dancedungeon.utils.math;


import java.util.Comparator;

/**
 * Created by BB on 21/10/2017.
 */

public class CoordinateSorter {




    public static Comparator<Coordinates> SORT_BY_NEAREST(final Coordinates origin){

        return new Comparator<Coordinates>() {
            @Override
            public int compare(Coordinates coords, Coordinates coords2) {

                Double i1 = Math.sqrt(Math.pow(coords.getX() - origin.getX(), 2) + Math.pow(coords.getY() - origin.getY(), 2));
                Double i2 = Math.sqrt(Math.pow(coords2.getX() - origin.getX(), 2) + Math.pow(coords2.getY() - origin.getY(), 2));

                return i1 < i2 ? -1 : (i1.equals(i2) ? 0 : 1);
            }
        };
    }

    private static int getNearestDistance(Coordinates coordinates, Coordinates origin){
        int x = Math.abs(coordinates.getX() - origin.getX());
        int y = Math.abs(coordinates.getY() - origin.getY());

        return x <= y  ? x : y;

    }





}
