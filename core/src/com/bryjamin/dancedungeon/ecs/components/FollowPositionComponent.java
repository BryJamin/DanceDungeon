package com.bryjamin.dancedungeon.ecs.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by BB on 18/12/2017.
 */

public class FollowPositionComponent extends Component {

    public Vector3 trackedPosition;

    public float offsetX;
    public float offsetY;

    public FollowPositionComponent() {
        trackedPosition = new Vector3();
    }

    public FollowPositionComponent(Vector3 trackedPosition) {
        this(trackedPosition, 0, 0);
    }

    public FollowPositionComponent(Vector3 trackedPosition, float offsetX, float offsetY) {
        this.trackedPosition = trackedPosition;
        this.offsetX = offsetX;
        this.offsetY = offsetY;


    }

}
