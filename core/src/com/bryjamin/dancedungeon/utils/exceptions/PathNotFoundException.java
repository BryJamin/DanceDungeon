package com.bryjamin.dancedungeon.utils.exceptions;

/**
 * Created by BB on 15/11/2017.
 * <p>
 * Used for pathing algorithms if they are unable to find a path
 */

public class PathNotFoundException extends Exception {

    public PathNotFoundException() {
    }

    public PathNotFoundException(String message) {
        super(message);
    }

}
