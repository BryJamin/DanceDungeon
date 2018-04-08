package com.bryjamin.dancedungeon.utils.texture;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by BB on 10/10/2017.
 */

public abstract class DrawableDescription {

    //Required
    private int identifier;
    private float width;
    private float height;
    private float offsetX;
    private float offsetY;
    private float scaleX;
    private float scaleY;
    private double rotation;
    private Vector2 origin;
    private Color color;
    private Color resetColor;



    public DrawableDescription(DrawableDescriptionBuilder<?> ddb){
        this.identifier = ddb.identifier;
        this.width = ddb.width;
        this.height = ddb.height;
        this.offsetX = ddb.offsetX;
        this.offsetY = ddb.offsetY;
        this.scaleX = ddb.scaleX;
        this.scaleY = ddb.scaleY;
        this.color = ddb.color;
        this.resetColor = ddb.resetColor;
        this.origin = ddb.origin;
        this.rotation = ddb.rotation;
        this.color.a = ddb.alpha; //TODO Find a better way to store previous colors and alpha?
        this.resetColor.a = ddb.alpha;
    }


    public abstract static class DrawableDescriptionBuilder<T extends DrawableDescriptionBuilder<T>> {

        //Optional
        private int identifier = -1;
        private float width = 0;
        private float height = 0;
        private float offsetX = 0;
        private float offsetY = 0;
        private float scaleX = 1;
        private float scaleY = 1;
        private double rotation = 0;
        private Vector2 origin = null;

        private Color color = new Color(Color.WHITE);
        private Color resetColor = new Color(Color.WHITE);
        private float alpha = 1.0f;


        public DrawableDescriptionBuilder(){}

        public DrawableDescriptionBuilder(DrawableDescription d){
            identifier = d.identifier;
            width = d.width;
            height = d.height;
            offsetX = d.offsetX;
            offsetY = d.offsetY;
            scaleX = d.scaleX;
            scaleY = d.scaleY;
            rotation = d.rotation;
            color = new Color(d.color);
            resetColor = new Color(d.resetColor);
            alpha = resetColor.a;
        }


        public T identifier(int val)
        { identifier = val; return getThis(); }

        public T width(float val)
        { width = val; return getThis(); }

        public T height(float val)
        { height = val; return getThis(); }

        public T size(float val) {
            width = val;
            height = val;
            return getThis();
        }

        public T offsetX(float val)
        { offsetX = val; return getThis(); }

        public T offsetY(float val)
        { offsetY = val; return getThis(); }

        public T scaleX(float val)
        { scaleX = val; return getThis(); }

        public T scaleY(float val)
        { scaleY = val; return getThis(); }

        public T rotation(double val)
        { rotation = val; return getThis(); }

        public T origin(Vector2 val)
        { origin = val; return getThis(); }

        public T alpha(float val) {
            alpha = val;
            alpha = val;
            return getThis();
        }

        public T color(Color val) {
            color = val;
            resetColor = val;
            return getThis();
        }

        /** Solution for the unchecked cast warning. */
        public abstract T getThis();

        public abstract DrawableDescription build();

    }

    public int getIdentifier() {
        return identifier;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public double getRotation() {
        return rotation;
    }

    public Color getColor() {
        return color;
    }

    public Color getResetColor() {
        return resetColor;
    }

    public Vector2 getOrigin() {
        return origin;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }
}
