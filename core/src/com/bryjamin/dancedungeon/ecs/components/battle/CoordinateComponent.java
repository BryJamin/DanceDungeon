package com.bryjamin.dancedungeon.ecs.components.battle;

import com.artemis.Component;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 21/10/2017.
 *
 * Track the Coordinates of An Entity
 *
 */

public class CoordinateComponent extends Component {

    public Coordinates coordinates = new Coordinates();

    /**
     * The Tile System automatically tries to place Entities that have the Coordinate Component
     * This turns off this functionality and the Entity can be placed anywhere but still keep the same coordinates
     */
    public boolean freePlacement = false;


    public CoordinateComponent(){}

    public CoordinateComponent(Coordinates coordinates){
        this.coordinates = coordinates;
    }


    public CoordinateComponent(Coordinates coordinates, boolean freePlacement){
        this.coordinates = coordinates;
        this.freePlacement = freePlacement;
    }

}
