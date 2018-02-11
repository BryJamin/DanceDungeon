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


    public static float centerOnPositionX(float width, float posX){
        return posX - width / 2;
    }

    public static float centerOnPositionY(float height, float posY){
        return posY - height / 2;
    }




    /*public static float multipleCenteringGetPosX(float startX, float fullWidth, float widthToCenter, float gap){





        return (width / 2) - (widthToCenter / 2);
    }

*/


}
