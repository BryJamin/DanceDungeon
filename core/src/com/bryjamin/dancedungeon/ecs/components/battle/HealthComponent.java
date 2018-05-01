package com.bryjamin.dancedungeon.ecs.components.battle;

import com.artemis.Component;

/**
 *
 * Created by BB on 11/10/2017.
 *
 * Used to track health of an entity as well as accumulated damage
 *
 */
public class HealthComponent extends Component {

    public int health;
    public int maxHealth;

    private int accumulatedDamage;
    private int accumulatedHealing;


    public HealthComponent(int health) {
        this.health = health;
        this.maxHealth = health;
    }

    public HealthComponent(int health, int maxHealth) {
        this.health = health;
        this.maxHealth = maxHealth  ;
    }

    public HealthComponent(){
        this(1);
    }


    public void applyDamage(int accumulatedDamage){
        this.accumulatedDamage += accumulatedDamage;
    }

    public void applyHealing(int accumulatedHealing){
        this.accumulatedHealing += accumulatedHealing;
    }

    public int getAccumulatedDamage() {
        return accumulatedDamage;
    }

    public int getAccumulatedHealing() {
        return accumulatedHealing;
    }

    public void clearDamage(){
        accumulatedDamage = 0;
    }

    public void clearHealing(){
        accumulatedHealing = 0;
    }

}