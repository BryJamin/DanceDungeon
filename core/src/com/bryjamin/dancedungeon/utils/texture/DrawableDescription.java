package com.bryjamin.dancedungeon.utils.texture;

/**
 * Created by BB on 10/10/2017.
 */

public class DrawableDescription {

    //Required
    private String region;
    private int index;
    private int identifier;
    private float width;
    private float height;
    private float offsetX;
    private float offsetY;
    private float scaleX = 1;
    private float scaleY = 1;
    private float rotation = 0;


    public DrawableDescription(DrawableDescriptionBuilder ddb){
        this.region = ddb.region;
        this.index = ddb.index;
        this.identifier = ddb.identifier;
        this.width = ddb.width;
        this.height = ddb.height;
        this.offsetX = ddb.offsetX;
        this.offsetY = ddb.offsetY;
    }


    public static class DrawableDescriptionBuilder {

        //Required
        private String region;

        //Optional
        private int index = 0;
        private int identifier;
        private float width = 0;
        private float height = 0;
        private float offsetX = 0;
        private float offsetY = 0;
        private float scaleX = 1;
        private float scaleY = 1;
        private float rotation = 0;


        public DrawableDescriptionBuilder(String region){
            this.region = region;
        }

        public DrawableDescriptionBuilder index(int val)
        { index = val; return this; }

        public DrawableDescriptionBuilder identifier(int val)
        { identifier = val; return this; }

        public DrawableDescriptionBuilder width(float val)
        { width = val; return this; }

        public DrawableDescriptionBuilder height(float val)
        { height = val; return this; }

        public DrawableDescriptionBuilder size(float val) {
            width = val;
            height = val;
            return this;
        }

        public DrawableDescriptionBuilder offsetX(float val)
        { offsetX = val; return this; }

        public DrawableDescriptionBuilder offsetY(float val)
        { offsetY = val; return this; }

        public DrawableDescriptionBuilder scaleX(float val)
        { scaleX = val; return this; }

        public DrawableDescriptionBuilder scaleY(float val)
        { scaleY = val; return this; }

        public DrawableDescriptionBuilder rotation(float val)
        { rotation = val; return this; }

        public DrawableDescription build()
        { return new DrawableDescription(this); }


    }


    public String getRegion() {
        return region;
    }

    public int getIndex() {
        return index;
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
}
