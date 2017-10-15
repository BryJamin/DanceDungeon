package com.bryjamin.dancedungeon.ecs.components;

import com.artemis.Component;

/**
 * Created by BB on 11/10/2017.
 *
 * Used to track health of an entity as well as accumulated damage
 *
 */

public class HealthComponent extends Component {

    public float health;
    public float maxHealth;

    private float accumulatedDamage;

    public HealthComponent(float health) {
        this.health = health;
        this.maxHealth = health;
    }

    public HealthComponent(){
        this(1);
    }


    public void applyDamage(float accumulatedDamage){
        this.accumulatedDamage += accumulatedDamage;
    }

    public float getAccumulatedDamage() {
        return accumulatedDamage;
    }

    public void clearDamage(){
        accumulatedDamage = 0;
    }

}