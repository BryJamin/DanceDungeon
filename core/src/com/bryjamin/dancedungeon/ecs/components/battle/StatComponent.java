package com.bryjamin.dancedungeon.ecs.components.battle;

import com.artemis.Component;

/**
 * Created by BB on 19/12/2017.
 */

public class StatComponent extends Component {

    public int health;
    public int maxHealth;

    public int movementRange;
    public int attackRange;

    public int power;
    public int magic;

    public int stun;

    public StatComponent(){}

    public StatComponent (StatBuilder sb){
        health = sb.health;
        maxHealth = sb.maxHealth;
        movementRange = sb.movementRange;
        attackRange = sb.attackRange;
        power = sb.power;
        magic = sb.magic;
    }

    public static class StatBuilder {

        private int health = 10;
        private int maxHealth = 10;

        private int attackRange = 1;
        private int movementRange = 3;

        private int power = 5;
        private int magic = 5;

        public StatBuilder health(int val)
        { this.health = val; return this; }

        public StatBuilder maxHealth(int val)
        { this.maxHealth = val; return this; }

        public StatBuilder healthAndMax(int val)
        { this.maxHealth = val; this.health = val; return this; }

        public StatBuilder attackRange(int val)
        { this.attackRange = val; return this; }

        public StatBuilder movementRange(int val)
        { this.movementRange = val; return this; }

        public StatBuilder power(int val)
        { this.power = val; return this; }

        public StatBuilder magic(int val)
        { this.magic = val; return this; }

        public StatComponent build()
        { return new StatComponent(this); }

    }




}
