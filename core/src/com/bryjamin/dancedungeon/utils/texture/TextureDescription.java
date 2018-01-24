package com.bryjamin.dancedungeon.utils.texture;

/**
 * Created by BB on 26/11/2017.
 */

public class TextureDescription extends DrawableDescription {


    private String region;
    private HighlightedText highlightedText;
    private int index;

    public TextureDescription(Builder tdb) {
        super(tdb);
        this.region = tdb.region;
        this.index = tdb.index;
        this.highlightedText = tdb.highlightedText;
    }

    public static class Builder extends DrawableDescriptionBuilder<Builder> {

        //Required
        private String region;

        //Optional
        private int index = 0;
        private HighlightedText highlightedText;

        public Builder(String region) {
            this.region = region;
        }


        public Builder(TextureDescription tf)
        {
            super(tf);
            this.region = tf.region;
            this.index = tf.index;
        }

        public Builder region(String val)
        { region = val; return getThis(); }

        public Builder index(int val)
        { index = val; return getThis(); }

        public Builder highlightedText(HighlightedText val)
        { highlightedText = val; return getThis(); }

        @Override
        public Builder getThis() {
            return this;
        }

        public TextureDescription build() {
            return new TextureDescription(this);
        }
    }

    public void setRegion(String region) {
        this.region = region;
    }


    public String getRegion() {
        return region;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public HighlightedText getHighlightedText() {
        return highlightedText;
    }
}
