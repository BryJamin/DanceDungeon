package com.bryjamin.dancedungeon.utils.random;

/**
 * Created by BB on 20/01/2018.
 *
 * Object used within the WeightedRoll class.
 *
 * Defines the 'weight' an object holds within the randomizer
 */

public class WeightedObject<T> {

    private T t;
    private int weight;

    public WeightedObject(T t, int weight) {
        this.t = t;
        this.weight = weight;
    }

    public T obj() {
        return t;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
