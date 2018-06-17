package com.bryjamin.dancedungeon.ecs.components.graphics;

import com.artemis.Component;

/**
 * Created by BB on 28/10/2017.
 *
 * Used to create a 'Fading' Effect for an Entity's drawable
 *
 * Can Fade In or Out or Endlessly.
 */

public class FadeComponent extends Component {

    public float minAlpha = 0;
    public float maxAlpha = 1;

    public float currentDuration = 0;
    public float maximumDuration;

    public float alpha = 1;

    public boolean fadeIn;
    public boolean flicker;

    public int count; //How many times should an Entity fade in or out.

    public boolean isEndless = true; //Overrides count

    public FadeComponent() {
        isEndless = true;
        maximumDuration = 1;
    }


    public FadeComponent(boolean fadeIn, float maximumDuration, boolean isEndless) {
        this.fadeIn = fadeIn;
        this.maximumDuration = maximumDuration;
        this.isEndless = isEndless;

        this.alpha = fadeIn ? 0 : 1;
        this.currentDuration = fadeIn ? 0 : maximumDuration;
    }

    public FadeComponent(FadeBuilder fb){
        this.minAlpha = fb.minAlpha;
        this.maxAlpha = fb.maxAlpha;
        this.currentDuration = fb.currentDuration;
        this.maximumDuration = fb.maximumDuration;
        this.alpha = fb.alpha;
        this.fadeIn = fb.fadeIn;
        this.flicker = fb.flicker;
        this.count = fb.count;

        this.isEndless = fb.isEndless;

        this.alpha = fadeIn ? fb.minAlpha : fb.maxAlpha;
        this.currentDuration = fadeIn ? 0 : maximumDuration;
    }


    public static class FadeBuilder {

        private float minAlpha = 0;
        private float maxAlpha = 1;

        private float currentDuration = 0;
        private float maximumDuration = 1;

        private float alpha = 1;

        private int count;

        private boolean fadeIn;
        private boolean flicker;

        private boolean isEndless = true;

        public FadeBuilder minAlpha(float val)
        { this.minAlpha = val; return this; }

        public FadeBuilder maxAlpha(float val)
        { this.maxAlpha = val; return this; }

        public FadeBuilder currentDuration(float val)
        { this.currentDuration = val; return this; }

        public FadeBuilder maximumDuration(float val)
        { this.maximumDuration = val; return this; }

        public FadeBuilder alpha(float val)
        { this.alpha = val; return this; }

        public FadeBuilder count(int val)
        { this.count = val; return this; }

        public FadeBuilder fadeIn(boolean val)
        { this.fadeIn = val; return this; }

        public FadeBuilder endless(boolean val)
        { this.isEndless = val; return this; }


        public FadeBuilder flicker(boolean val)
        { this.flicker = val; return this; }


        public FadeComponent build()
        { return new FadeComponent(this); }



    }




}
