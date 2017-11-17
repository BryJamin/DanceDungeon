package com.bryjamin.dancedungeon.utils.math;


import com.artemis.Entity;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;

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


    public static Comparator<Entity> SORT_BY_NEAREST(final Entity origin){

        final Comparator<Coordinates> coordSorter = SORT_BY_NEAREST(origin.getComponent(CoordinateComponent.class).coordinates);

        return new Comparator<Entity>() {
            @Override
            public int compare(Entity coords, Entity coords2) {
                return  coordSorter.compare(coords.getComponent(CoordinateComponent.class).coordinates, coords2.getComponent(CoordinateComponent.class).coordinates);
            }
        };
    }

    private static int getNearestDistance(Coordinates coordinates, Coordinates origin){
        int x = Math.abs(coordinates.getX() - origin.getX());
        int y = Math.abs(coordinates.getY() - origin.getY());

        return x <= y  ? x : y;

    }





}
