package com.bryjamin.dancedungeon.ecs.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by BB on 11/10/2017.
 *
 * Keeps track of the Velocity of an Entity
 */

public class VelocityComponent extends Component {

    public Vector3 velocity;

    public VelocityComponent(float vlx, float vly) {
        velocity = new Vector3(vlx, vly, 0);
    }

    public VelocityComponent(VelocityComponent vc) {
        velocity = new Vector3(vc.velocity.x, vc.velocity.y, vc.velocity.z);
    }

    public VelocityComponent(){
        this(0,0);
    }
}
