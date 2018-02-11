package com.bryjamin.dancedungeon.utils.math;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by BB on 11/02/2018.
 */

public class CenteringFrame {

    private Rectangle container = new Rectangle();

    private int rows = 1;
    private int columns = 1;
    private float xGap = 0;
    private float yGap = 0;

    private float widthPer;
    private float heightPer;

    public CenteringFrame(float x, float y, float width, float height){
        this.container = new Rectangle(x, y, width, height);
    }

    public CenteringFrame(Rectangle rectangle){
        this.container = container;
    }


    public Vector2 calculatePosition(int position){

        Vector2 containerCenter = container.getCenter(new Vector2());
        float startX = CenterMath.centerOnPositionX((widthPer * columns) + (xGap * columns), containerCenter.x);
        float startY = CenterMath.centerOnPositionY((heightPer * rows) + (yGap * rows), containerCenter.y);

        int mod = position % columns;
        int div = position / columns;

        return new Vector2(startX + (widthPer * mod) + (xGap * mod),
                startY - (div * heightPer) - (yGap * div));

    };

    /**
     * Instead of startY/Position 1 denoting the bottom left cell, it switchs it to the top left cell.
     * @param position
     * @return
     */
    public Vector2 calculatePositionReverseY(int position){

        float height = (heightPer * rows) + (yGap * rows);

        Vector2 containerCenter = container.getCenter(new Vector2());
        float startX = CenterMath.centerOnPositionX((widthPer * columns) + (xGap * columns), containerCenter.x);
        float startY = CenterMath.centerOnPositionY(height, containerCenter.y) + height - heightPer;

        System.out.println(containerCenter.y);
        System.out.println(startY);

        int mod = position % columns;
        int div = position / columns;

        return new Vector2(startX + (widthPer * mod) + (xGap * mod),
                startY - (div * heightPer) - (yGap * div));

    };


    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public void setxGap(float xGap) {
        this.xGap = xGap;
    }

    public void setyGap(float yGap) {
        this.yGap = yGap;
    }

    public void setWidthPer(float widthPer) {
        this.widthPer = widthPer;
    }

    public void setHeightPer(float heightPer) {
        this.heightPer = heightPer;
    }
}
