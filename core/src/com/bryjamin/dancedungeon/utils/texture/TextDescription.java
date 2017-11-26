package com.bryjamin.dancedungeon.utils.texture;

import com.badlogic.gdx.utils.Align;

/**
 * Created by BB on 26/11/2017.
 */

public class TextDescription extends DrawableDescription {

    private String font;
    private String text;
    private int align;

    public TextDescription(TextDescriptionBuilder tdb) {
        super(tdb);
        this.font = tdb.font;
        this.text = tdb.text;
        this.align = tdb.align;
    }

    public static class TextDescriptionBuilder extends DrawableDescriptionBuilder {

        //Required
        private String font;

        //Optional
        private String text;
        private int align = Align.center;

        public TextDescriptionBuilder(String font) {
            this.font = font;
        }

        public TextDescriptionBuilder text(String val)
        { text = val; return getThis(); }

        public TextDescriptionBuilder align(int val)
        { align = val; return getThis(); }

        @Override
        public TextDescriptionBuilder getThis() {
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
