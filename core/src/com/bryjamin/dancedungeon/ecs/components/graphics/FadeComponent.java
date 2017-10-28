package com.bryjamin.dancedungeon.ecs.components.graphics;

import com.artemis.Component;

/**
 * Created by BB on 28/10/2017.
 */

public class FadeComponent extends Component {

    public float minAlpha = 0;
    public float maxAlpha = 1;

    public float alphaTimer = 0;
    public float alphaTimeLimit;

    public float alpha;

    public boolean fadeIn;
    public boolean flicker;

    public int count;

    public boolean isEndless = true;

    public FadeComponent() {
        isEndless = true;
        alphaTimeLimit = 1;
    }


    public FadeComponent(boolean fadeIn, float alphaTimeLimit, boolean isEndless) {
        this.fadeIn = fadeIn;
        this.alphaTimeLimit = alphaTimeLimit;
        this.isEndless = isEndless;
    }


}
