package com.bryjamin.dancedungeon.utils.texture;

import com.badlogic.gdx.utils.Align;

/**
 * Created by BB on 26/11/2017.
 */

public class TextDescription extends DrawableDescription {

    private String font;
    private String text;
    private int align;

    public TextDescription(Builder tdb) {
        super(tdb);
        this.font = tdb.font;
        this.text = tdb.text;
        this.align = tdb.align;
    }

    public static final class Builder extends DrawableDescriptionBuilder<Builder> {

        //Required
        private String font;

        //Optional
        private String text;
        private int align = Align.center;

        public Builder(String font) {
            this.font = font;
        }

        public Builder text(String val)
        { text = val; return getThis(); }

        public Builder align(int val)
        { align = val; return getThis(); }

        @Override
        public Builder getThis() {
            return this;
        }

        public TextDescription build() {
            return new TextDescription(this);
        }
    }


    public String getFont() {
        return font;
    }

    public String getText() {
        return text;
    }

    public int getAlign() {
        return align;
    }
}
