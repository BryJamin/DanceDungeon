package com.bryjamin.dancedungeon.ecs.components.battle;

import com.artemis.Component;

/**
 * Created by BB on 19/10/2017.
 */

public class BulletComponent extends Component{

    public float damage;

    public BulletComponent(){};

    public BulletComponent(float damage){
        this.damage = damage;
    }

}
