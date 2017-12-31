package com.bryjamin.dancedungeon.ecs.components.graphics;

import com.artemis.Component;

/**
 * Created by BB on 28/10/2017.
 */

public class FadeComponent extends Component {

    public float minAlpha = 0;
    public float maxAlpha = 1;

    public float time = 0;
    public float maximumTime;

    public float alpha = 1;

    public boolean fadeIn;
    public boolean flicker;

    public int count;

    public boolean isEndless = true;

    public FadeComponent() {
        isEndless = true;
        maximumTime = 1;
    }


    public FadeComponent(boolean fadeIn, float maximumTime, boolean isEndless) {
        this.fadeIn = fadeIn;
        this.maximumTime = maximumTime;
        this.isEndless = isEndless;

        this.alpha = fadeIn ? 0 : 1;
        this.time = fadeIn ? 0 : maximumTime;
    }

    public FadeComponent(FadeBuilder fb){
        this.minAlpha = fb.minAlpha;
        this.maxAlpha = fb.maxAlpha;
        this.time = fb.time;
        this.maximumTime = fb.maximumTime;
        this.alpha = fb.alpha;
        this.fadeIn = fb.fadeIn;
        this.flicker = fb.flicker;
        this.count = fb.count;

        this.isEndless = fb.isEndless;

        //this.alpha = fadeIn ? fb.minAlpha : fb.maxAlpha;
        //this.time = fadeIn ? 0 : maximumTime;
    }


    public static class FadeBuilder {

        public float minAlpha = 0;
        public float maxAlpha = 1;

        public float time = 0;
        public float maximumTime;

        public float alpha = 1;

        public int count;

        public boolean fadeIn;
        public boolean flicker;

        public boolean isEndless = true;

        public FadeBuilder minAlpha(float val)
        { this.minAlpha = val; return this; }

        public FadeBuilder maxAlpha(float val)
        { this.maxAlpha = val; return this; }

        public FadeBuilder time(float val)
        { this.time = val; return this; }

        public FadeBuilder maximumTime(float val)
        { this.maximumTime = val; return this; }

        public FadeBuilder alpha(float val)
        { this.alpha = val; return this; }

        public FadeBuilder count(int val)
        { this.count = val; return this; }

        public FadeBuilder fadeIn(boolean val)
        { this.fadeIn = val; return this; }

        public FadeBuilder flicker(boolean val)
        { this.flicker = val; return this; }


        public FadeComponent build()
        { return new FadeComponent(this); }



    }




}
