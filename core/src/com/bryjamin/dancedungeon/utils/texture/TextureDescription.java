package com.bryjamin.dancedungeon.utils.texture;

/**
 * Created by BB on 26/11/2017.
 */

public class TextureDescription extends DrawableDescription {


    private final String region;
    private final int index;

    public TextureDescription(Builder tdb) {
        super(tdb);
        this.region = tdb.region;
        this.index = tdb.index;
    }

    public static class Builder extends DrawableDescriptionBuilder {

        //Required
        private String region;

        //Optional
        private int index = 0;

        public Builder(String region) {
            this.region = region;
        }

        public Builder region(String val)
        { region = val; return getThis(); }

        public Builder index(int val)
        { index = val; return getThis(); }

        @Override
        public Builder getThis() {
            return this;
        }

        public TextureDescription build() {
            return new TextureDescription(this);
        }
    }


    public String getRegion() {
        return region;
    }

    public int getIndex() {
        return index;
    }
}
