package com.bryjamin.dancedungeon.utils;

/**
 * Created by BB on 16/02/2018.
 */

public class StopWatch {

    private long start;
    private String name;

    public void start(String name){
        this.start = System.nanoTime();
        this.name = name;
    };


    public void stop(){

        System.out.println();
        long elapsed = System.nanoTime() - start;

        System.out.println(name + ": Time elapsed is " + (elapsed));
        System.out.println("in seconds is: " + (double)elapsed / 1000000000.0);
    }




}
