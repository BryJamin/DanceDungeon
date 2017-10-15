package com.bryjamin.dancedungeon.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Created by BB on 15/10/2017.
 */

public class BoundsDrawer {

    private static ShapeRenderer shapeRenderer = new ShapeRenderer();;

    /**
     * Draws the boundaries of an Array of Rectangles
     * @param batch - The SpriteBatch
     * @param bounds - Rectangle Array
     */
    public static void drawBounds(SpriteBatch batch, Array<? extends Rectangle> bounds){

        if(batch.isDrawing()) {
            batch.end();
        }

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        for(Rectangle r : bounds) {
            shapeRenderer.rect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
        }
        shapeRenderer.end();

        if(!batch.isDrawing()) {
            batch.begin();
        }
    }

    /**
     * Draws the boundaries of an Array of Rectangles
     * @param batch - The SpriteBatch
     * @param bounds - Rectangle Array
     */
    public static void drawBounds(SpriteBatch batch, Rectangle... bounds){

        if(batch.isDrawing()) {
            batch.end();
        }

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        for(Rectangle r : bounds) {
            shapeRenderer.rect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
        }
        shapeRenderer.end();

        if(!batch.isDrawing()) {
            batch.begin();
        }
    }

    /**
     * Draws the boundaries of an Array of Rectangles
     * @param batch - The SpriteBatch
     * @param bounds - Rectangle Array
     */
    public static void drawBounds(SpriteBatch batch, Color c, Rectangle... bounds){

        if(batch.isDrawing()) {
            batch.end();
        }

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(c);
        for(Rectangle r : bounds) {
            shapeRenderer.rect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
        }
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.end();

        if(!batch.isDrawing()) {
            batch.begin();
        }
    }

    public static void drawBounds(SpriteBatch batch, Color c, Array<Rectangle> bounds) {

        if(bounds.size <= 0) return;

        if(batch.isDrawing()) {
            batch.end();
        }

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(c);
        for(Rectangle r : bounds) {
            shapeRenderer.rect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
        }
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.end();

        if(!batch.isDrawing()) {
            batch.begin();
        }
    }

}
