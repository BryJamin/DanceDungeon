package com.bryjamin.dancedungeon.ecs.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.utils.HitBox;

/**
 * Created by BB on 15/10/2017.
 */

public class HitBoxComponent extends Component {

    public Array<HitBox> hitBoxes = new Array<HitBox>();

    public boolean enabled = true;

    public HitBoxComponent(){}

    public HitBoxComponent(HitBox... hitBoxes){
        this.hitBoxes.addAll(hitBoxes);
    }

    public boolean overlaps(Rectangle r){
        for(HitBox hitBox : hitBoxes){
            if(r.overlaps(hitBox.hitbox)){
                return true;
            }
        }
        return false;
    }

    public boolean contains(float x, float y){
        for(HitBox hitBox : hitBoxes){
            if(hitBox.hitbox.contains(x, y)){
                return true;
            }
        }
        return false;
    }


    public void update(PositionComponent pc){
        for(HitBox hb : hitBoxes){
            hb.hitbox.x = pc.getX() + hb.offsetX;
            hb.hitbox.y = pc.getY() + hb.offsetY;
        }
    }


}
