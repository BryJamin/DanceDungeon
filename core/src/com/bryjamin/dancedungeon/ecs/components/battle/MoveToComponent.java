package com.bryjamin.dancedungeon.ecs.components.battle;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.utils.Measure;

/**
 * Created by BB on 21/10/2017.
 */

public class MoveToComponent extends Component {

    public Array<Vector3> movementPositions = new Array<Vector3>();

    public float speed = Measure.units(20f);

    public boolean isInPosition;

    public boolean isComplete;

    public MoveToComponent(){

    }

    public MoveToComponent(float speed, Vector3... movementPositions){
        this.speed = speed;
        this.movementPositions.addAll(movementPositions);
    }

    public boolean isEmpty(){
        return movementPositions.size == 0;
    }


}
