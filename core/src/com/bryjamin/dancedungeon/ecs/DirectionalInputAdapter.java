package com.bryjamin.dancedungeon.ecs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by BB on 16/10/2017.
 */

public class DirectionalInputAdapter extends InputAdapter {


    private static final float tapSquareSize = 40;

    private float lastTapX, lastTapY;
    private float tapSquareCenterX, tapSquareCenterY;


    private int tapCount = 0;
    private int lastTapPointer = -1;

    private static final long tapInterval = (long)(0.4f * 1000000000L);
    private long tapStartTime;
    private long lastTapTime;

    private boolean inTapSquare;

    private boolean test;

    DirectionalGestureListener listener;

    public DirectionalInputAdapter(DirectionalGestureListener listener){
        this.listener = listener;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if(pointer > 0) return false;


        tapStartTime = Gdx.input.getCurrentEventTime();
        tapSquareCenterX = screenX;
        tapSquareCenterY = screenY;
        inTapSquare = true;
        test= true;
        System.out.println(inTapSquare);


        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        if(pointer > 0) return false;
        System.out.println("Test is : " + test);

        System.out.println(inTapSquare);

        if (inTapSquare && !isWithinTapSquare(screenX, screenY, tapSquareCenterX, tapSquareCenterY)) inTapSquare = false;

        System.out.println(inTapSquare);

        if(inTapSquare && isWithinTapInterval()) {

            boolean test = TimeUtils.nanoTime() - lastTapTime > tapInterval;
            if (lastTapPointer != pointer ||
                    !isWithinTapSquare(screenX, screenY, lastTapX, lastTapY) ||
                    test) {
                tapCount = 0;
            }


            //TODO may need to put it below this possible maybe I dunno

            tapCount++;
            lastTapTime = TimeUtils.nanoTime();
            lastTapX = screenX;
            lastTapY = screenY;
            lastTapPointer = pointer;

            System.out.println("??");

            return listener.tap(screenX, screenY, tapCount, button);

        } else {
            if(TimeUtils.nanoTime() - lastTapTime > tapInterval * 2){

                System.out.println("?dawdawdw?");


                return listener.swipe(tapSquareCenterX, tapSquareCenterY, screenX, screenY);
            }
        }


        return false;
    }


    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        System.out.println("drag");
        System.out.println(pointer);
        return false;
    }


    private boolean isWithinTapSquare (float x, float y, float centerX, float centerY) {
        return Math.abs(x - centerX) < tapSquareSize && Math.abs(y - centerY) < tapSquareSize;
    }

    private boolean isWithinTapInterval(){
        return Gdx.input.getCurrentEventTime() - tapStartTime < tapInterval;
    }



    public static interface DirectionalGestureListener {

        public boolean tap (float x, float y, int count, int button);


        public boolean swipe (float startX, float startY, float endX, float endY);


       // public boolean tap (float x, float y, int count, int button);


    }



}
