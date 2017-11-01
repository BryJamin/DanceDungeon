package com.bryjamin.dancedungeon.ecs.components.battle;

import com.artemis.Component;
import com.bryjamin.dancedungeon.utils.math.Coordinates;

/**
 * Created by BB on 21/10/2017.
 *
 *
 *
 */

public class CoordinateComponent extends Component {

    public Coordinates coordinates = new Coordinates();

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
