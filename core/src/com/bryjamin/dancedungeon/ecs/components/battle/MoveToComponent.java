package com.bryjamin.dancedungeon.ecs.components.battle;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.utils.Measure;

/**
 * Created by BB on 21/10/2017.
 *
 * Used to create 'movement' sequences within the game.
 *
 * Add to the movement positions and the move to system will move the entity to each position
 * until the Array is empty.
 *
 */

public class MoveToComponent extends Component {

    public Array<Vector3> movementPositions = new Array<Vector3>();

    public float speed = Measure.units(20f);

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
