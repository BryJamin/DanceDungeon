package com.bryjamin.dancedungeon.utils.random;

/**
 * Created by BB on 20/01/2018.
 */

import com.badlogic.gdx.utils.Array;

import java.util.Random;

/**
 * Created by Home on 22/04/2017.
 */

public class WeightedRoll<T> {

    private Array<WeightedObject<T>> weightedObjects = new Array<WeightedObject<T>>();


    private Random random;


    public WeightedRoll(Random random){
        this.weightedObjects = new Array<WeightedObject<T>>();
        this.random = random;
    }

    public WeightedRoll(Array<WeightedObject<T>> weightedObjects, Random random){
        this.weightedObjects = weightedObjects;
        this.random = random;
    }

    public WeightedRoll(Random random, WeightedObject<T>... weightedObjects){
        this.weightedObjects.addAll(weightedObjects);
        this.random = random;
    }

    public WeightedRoll(WeightedRoll<T> weightedRoll){
        this.weightedObjects = new Array<WeightedObject<T>>();
        this.getWeightedObjects().addAll(weightedRoll.getWeightedObjects());
        this.random = weightedRoll.getRandom();
    }

    public void addWeightedObjects(WeightedObject<T>... weightedObjects){
        this.weightedObjects.addAll(weightedObjects);
    }

    public Array<WeightedObject<T>> getWeightedObjects() {
        return weightedObjects;
    }

    /**
     * Randomly selects a WeightedObject from the WeightObject array
     * using their weights.
     * @return - The WeightedObjects held object.
     */
    public T roll(){
        return rollForWeight().obj();
    }


    /**
     * Randomly selects a WeightedObject from the WeightObject array
     * using their weights.
     * @return The Weighted Object
     */
    public WeightedObject<T> rollForWeight(){

        Array<T> objects = new Array<T>();
        objects.setSize(weightedObjects.size);
        int[] percentages = new int[weightedObjects.size];

        for(int i = 0; i < weightedObjects.size; i++){
            objects.set(i, weightedObjects.get(i).obj());
            percentages[i] = weightedObjects.get(i).getWeight();
        }

        int totalWeight = 0;
        for(int i : percentages){
            totalWeight += i;
        }

        int roll = random.nextInt(totalWeight);

        WeightedObject<T> chosenWeight = null;

        for(int i = 0; i < percentages.length; i++){
            if(roll < percentages[i]){
                chosenWeight = weightedObjects.get(i);
                break;
            }
            roll -= percentages[i];
        }


        return chosenWeight;

    }


    public Random getRandom() {
        return random;
    }
}

