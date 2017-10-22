package com.bryjamin.dancedungeon.ecs.components.battle;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Created by BB on 21/10/2017.
 */

public class MoveToComponent extends Component {

    public Array<Vector3> movementPositions = new Array<Vector3>();

    public boolean isInPosition;

    public boolean isComplete;

    public MoveToComponent(){}

    public MoveToComponent(Vector3... movementPositions){
        this.movementPositions.addAll(movementPositions);
    }


}
