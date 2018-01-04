package com.bryjamin.dancedungeon.ecs.components.battle;

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
    private float accumulatedHealing;


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

    public void applyHealing(float accumulatedHealing){
        this.accumulatedHealing += accumulatedHealing;
    }

    public float getAccumulatedDamage() {
        return accumulatedDamage;
    }

    public float getAccumulatedHealing() {
        return accumulatedHealing;
    }

    public void clearDamage(){
        accumulatedDamage = 0;
    }

    public void clearHealing(){
        accumulatedHealing = 0;
    }

}