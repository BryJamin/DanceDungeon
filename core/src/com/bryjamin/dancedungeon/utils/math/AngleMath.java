package com.bryjamin.dancedungeon.utils.math;

/**
 * Created by BB on 17/10/2017.
 */

public class AngleMath {

    public static double angleOfTravel(float startX, float startY, float endX, float endY) {
        return (Math.atan2(endY - startY, endX - startX));
    }

    public static double angleOfTravelInDegrees(float startX, float startY, float endX, float endY) {
        return Math.toDegrees(Math.atan2(endY - startY, endX - startX));
    }


    public static double normalizeAngle(double angle)
    {
        double newAngle = angle;
        while (newAngle <= -180) newAngle += 360;
        while (newAngle > 180) newAngle -= 360;
        return newAngle;
    }


    public static float velocityX(float speed, double angleInRadians){
        return (float) (speed * Math.cos(angleInRadians));
    }

    public static float velocityY(float speed, double angleInRadians){
        return (float) (speed * Math.sin(angleInRadians));
    }


}
