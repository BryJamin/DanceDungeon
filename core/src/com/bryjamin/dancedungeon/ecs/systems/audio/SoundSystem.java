package com.bryjamin.dancedungeon.ecs.systems.audio;

import com.artemis.BaseSystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.assets.music.SoundFiles;
import com.bryjamin.dancedungeon.utils.options.Settings;
import com.bryjamin.dancedungeon.utils.sound.Mix;

public class SoundSystem extends BaseSystem {


    private static final float MASTER_VOLUME = 1.0f;

    private AssetManager assetManager;

    private Array<Mix> upcomingMixes = new Array<Mix>();

    private Array<Mix[]> upcomingMegaMixes = new Array<Mix[]>();


    private Array<Music> musicToBeDisposed = new Array<Music>();

    public static boolean SOUNDON = false;

    public float silenceSoundId;


    private Sound sound;

    public SoundSystem(AssetManager assetManager) {
        this.assetManager = assetManager;
        sound = assetManager.get(SoundFiles.soundOfSilence, Sound.class);
        silenceSoundId = sound.loop(0f);
        SOUNDON = Settings.isSoundOn();
    }


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (!enabled) {
            sound.stop();
        }
    }

    @Override
    protected void processSystem() {

        for (Mix m : upcomingMixes) {
            Sound s = assetManager.get(m.getFileName(), Sound.class);
            s.play(SOUNDON ? m.getVolume() * MASTER_VOLUME : 0, m.getPitch(), 0);
        }

        upcomingMixes.clear();
        upcomingMegaMixes.clear();

    }


    /**
     * Queues up a Mix to be played in the process of the SoundSystem
     * To avoid amplification of sound, if the same mix is played (As all mixes come from the same static methods)
     * that mix is not queued up.
     *
     * @param mix - The mix to be played
     */
    public void playSound(Mix mix) {

        if (assetManager.isLoaded(mix.getFileName(), Sound.class) && SOUNDON) {

            if (!upcomingMixes.contains(mix, true)) {
                upcomingMixes.add(mix);
            }
        }
    }

    /**
     * This runs the playSound method for a random mix from an Array of Mixes
     *
     * @param mixes - Array of mixes
     */
    public void playRandomSound(Mix... mixes) {

        if (mixes.length <= 0) return;

        //Prevents The Same Sound Overlapping and playing at the same time
        if (!upcomingMegaMixes.contains(mixes, true)) {
            playSound(mixes[MathUtils.random.nextInt(mixes.length)]);
            upcomingMegaMixes.add(mixes);
        }
    }

    @Override
    protected void dispose() {
        super.dispose();
        sound.stop();
    }


}