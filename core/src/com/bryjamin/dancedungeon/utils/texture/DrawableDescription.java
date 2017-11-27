package com.bryjamin.dancedungeon.utils.texture;

import com.badlogic.gdx.graphics.Color;

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
    private float rotation;
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
    }


    public abstract static class DrawableDescriptionBuilder<T extends DrawableDescriptionBuilder<T>> {

        //Optional
        private int identifier;
        private float width = 0;
        private float height = 0;
        private float offsetX = 0;
        private float offsetY = 0;
        private float scaleX = 1;
        private float scaleY = 1;
        private float rotation = 0;

        private Color color = new Color(Color.WHITE);
        private Color resetColor = new Color(Color.WHITE);

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

        public T rotation(float val)
        { rotation = val; return getThis(); }

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

    public float getRotation() {
        return rotation;
    }

    public Color getColor() {
        return color;
    }

    public Color getResetColor() {
        return resetColor;
    }
}
