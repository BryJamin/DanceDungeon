package com.bryjamin.dancedungeon.ecs.components.battle;

import com.artemis.Component;

/**
 * Created by BB on 15/10/2017.
 */

public class ExplosionComponent extends Component {

    public float damage;

    public ExplosionComponent(float damage){
        this.damage = damage;
    }

    public ExplosionComponent(){
    }


}
