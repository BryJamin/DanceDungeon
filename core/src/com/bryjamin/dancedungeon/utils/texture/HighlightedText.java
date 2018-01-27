package com.bryjamin.dancedungeon.utils.texture;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

/**
 * Created by BB on 24/01/2018.
 */

public class HighlightedText {

    private String text = "";
    private Array<Highlight> highlightArray = new Array<Highlight>();

    public HighlightedText(){}

    public HighlightedText add(String text){
        this.text += text;
        return this;
    }

    public HighlightedText add(String text, Color color){
        highlightArray.add(new Highlight(color, this.text.length(), this.text.length() + text.length()));
        this.text += text;
        return this;
    }

    public String getText() {
        return text;
    }

    public Array<Highlight> getHighlightArray() {
        return highlightArray;
    }
}
