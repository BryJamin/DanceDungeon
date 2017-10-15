package com.bryjamin.dancedungeon.ecs.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by BB on 09/10/2017.
 *
 * Position of an Entity
 */

public class PositionComponent extends Component {

    public Vector3 position;

    public PositionComponent(float x, float y){
        position = new Vector3(x,y,0);
    }


    public PositionComponent(Vector2 v){
        position = new Vector3(v.x,v.y,0);
    }

    public PositionComponent(){
        this(0,0);
    }

    public float getX(){
        return position.x;
    }

    public float getY(){
        return position.y;
    }

    public void setX(float x){
        this.position.x = x;
    }

    public void setY(float y){
        this.position.y = y;
    }


}

