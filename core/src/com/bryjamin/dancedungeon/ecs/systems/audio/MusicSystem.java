package com.bryjamin.dancedungeon.ecs.systems.audio;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.bryjamin.dancedungeon.utils.options.Settings;
import com.bryjamin.dancedungeon.utils.sound.Mix;
/**
 * Music System plays music and manages Changing Music Tracks
 *
 * Due to static resources not being advised when using Android, This system is passed throughout the game
 *
 * Similar to the Assetmanager
 *
 *
 */
public class MusicSystem extends BaseSystem {


    private static final float MASTER_VOLUME = 1.0f;

    private Music currentMusic;

    private static final float rateOfVolumeDecrease = 0.02f;

    private static Mix currentMix;// = new Mix("");
    private static Mix upComingMix; //= new Mix("");


    public static boolean MUSIC_ON = false;



    private enum MusicState {
        PLAYING, PAUSED, WAITING
    }

    private MusicState musicState = MusicState.WAITING; //Current
    private MusicState prePuaseState = MusicState.WAITING; //State prior to being paused

    private enum VolumeState {
        FADING_OUT, FADING_IN, NORMAL
    }

    private VolumeState volumeState = VolumeState.FADING_IN;


    @Override
    protected void initialize() {
        MUSIC_ON = Settings.isMusicOn();
    }

    @Override
    protected void processSystem() {

        switch(musicState){

            case WAITING: //Waiting for the next track to be loaded.
                if(upComingMix != null){
                    changeTrack(upComingMix);
                    musicState = MusicState.PLAYING;
                    volumeState = VolumeState.FADING_IN;
                    upComingMix = null;
                }
                break;

            case PLAYING: //Changes the volumes of the music based on which BV

                float volume;

                switch (volumeState) {

                    case FADING_IN:

                        volume = currentMusic.getVolume() + currentMix.getVolume() * rateOfVolumeDecrease
                                >= currentMix.getVolume() ? currentMix.getVolume() : currentMusic.getVolume() + currentMix.getVolume() * rateOfVolumeDecrease;

                        currentMusic.setVolume(volume * MASTER_VOLUME);

                        if(currentMusic.getVolume() == currentMix.getVolume() * MASTER_VOLUME) volumeState = VolumeState.NORMAL;

                        break;

                    case FADING_OUT:

                        volume = currentMusic.getVolume() - currentMix.getVolume() * rateOfVolumeDecrease
                                <= 0 ? 0 : currentMusic.getVolume() - currentMix.getVolume() * rateOfVolumeDecrease;
                        currentMusic.setVolume(volume * MASTER_VOLUME);

                        if(currentMusic.getVolume() <= 0) musicState = MusicState.WAITING;

                        break;

                    case NORMAL:

                        if(currentMusic.getVolume() != currentMix.getVolume() * MASTER_VOLUME && MUSIC_ON){
                            currentMusic.setVolume(currentMix.getVolume() * MASTER_VOLUME);
                        }

                }

                if((volumeState == VolumeState.NORMAL || volumeState == VolumeState.FADING_IN) && upComingMix != null){
                    volumeState = VolumeState.FADING_OUT;
                }

        }

        if(currentMusic == null) return;

        if(!MUSIC_ON){
            if(currentMusic.getVolume() != 0) currentMusic.setVolume(0);
        } else {
            if(!currentMusic.isPlaying()) {
                currentMusic.play();
            }
        }

    }


    private void changeTrack(Mix mix){

        if(mix.equals(currentMix)) return;

        if(currentMusic != null){
            currentMusic.stop();
            currentMusic.dispose();
        }

        currentMix = mix;

        if(Gdx.files.internal(mix.getFileName()).exists()) {
            currentMusic = Gdx.audio.newMusic(Gdx.files.internal(mix.getFileName()));
            currentMusic.setLooping(true);
            currentMusic.setVolume(0);
            //currentMusic.play();
        }
    }

    /**
     * Queues Up The Next Mix To Be Played. The Other Mix Is Gradually Faded Out
     * @param mix
     */
    public void changeMix(Mix mix){
        if(mix.equals(currentMix)) return;

        if(Gdx.files.internal(mix.getFileName()).exists()) {
            upComingMix = mix;
        }
    }

    public void fadeOutMusic(){
        volumeState = VolumeState.FADING_OUT;
    }



    public void muteMusic(){
        if(currentMusic != null) {
            currentMusic.setVolume(0);
            MUSIC_ON = false;
        }
    }


    /**
     * Pauses The Music When Called
     */
    public void pauseMusic(){
        if(currentMusic != null){
            currentMusic.pause();
        }

        prePuaseState = musicState == MusicState.PLAYING ? MusicState.PLAYING : MusicState.WAITING;
        musicState = MusicState.PAUSED;

    }

    public void resumeMusic(){

        musicState = prePuaseState;
        if(currentMusic != null){

            if(MUSIC_ON) {
                currentMusic.play();
            }
        }
    }



    @Override
    protected void dispose() {
        super.dispose();
        //muteMusic();
    }
}

