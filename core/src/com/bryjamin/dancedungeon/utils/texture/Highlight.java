package com.bryjamin.dancedungeon.utils.texture;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by BB on 24/01/2018.
 *
 * Used to highlight the text of a sentence a different colour
 *
 */

public class Highlight {

    public Color color;
    public int start;
    public int end;

    public Highlight(Color color, int start, int end) {
        this.color = color;
        this.start = start;
        this.end = end;
    }


}
