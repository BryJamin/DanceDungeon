package com.bryjamin.dancedungeon.utils.math;

/**
 * Created by BB on 19/10/2017.
 */

public class CenterMath {

    //TODO for ease of readability I use offSetX and Y, but it's the same calculation

    public static float offsetX(float width, float widthToCenter){
        return (width / 2) - (widthToCenter / 2);
    }

    public static float offsetY(float height, float heightToCenter){
        return (height / 2) - (heightToCenter / 2);
    }

}
