package com.bryjamin.dancedungeon.utils.math;

/**
 * Created by BB on 18/10/2017.
 *
 * Class used for making co-ordinates used in tiles and mapping
 *
 */

public class Coordinates {

    private int x;
    private int y;

    public Coordinates(){
        this.x = 0;
        this.y = 0;
    }

    public Coordinates(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Coordinates(Coordinates e){
        this.x = e.x;
        this.y = e.y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void addX(int x){
        this.x += x;
    }

    public void addY(int y){
        this.y += y;
    }

    public void set(Coordinates coordinates){
        this.x = coordinates.getX();
        this.y = coordinates.getY();
    }

    public void add(int x, int y){
        this.x += x;
        this.y += y;
    }

    @Override
    public String toString() {
        return "Co-ordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinates mapCoords = (Coordinates) o;

        if (x != mapCoords.x) return false;
        return y == mapCoords.y;

    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

}
