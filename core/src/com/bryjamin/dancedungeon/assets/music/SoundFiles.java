package com.bryjamin.dancedungeon.assets.music;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.bryjamin.dancedungeon.utils.sound.Mix;

public class SoundFiles {

    public static final String playerFire = "audio/sounds/projectile/laser.ogg";
    public static final Mix playerFireMix = new Mix(playerFire, 0.05f);


    public static final Mix playerFireMix1 = new Mix.MixMaker(playerFire).volume(0.05f).pitch(0.925f).build();
    public static final Mix playerFireMix2 = new Mix.MixMaker(playerFire).volume(0.05f).pitch(0.95f).build();
    public static final Mix playerFireMix3 = new Mix.MixMaker(playerFire).volume(0.05f).pitch(0.975f).build();
    public static final Mix playerFireMix4 = new Mix.MixMaker(playerFire).volume(0.05f).pitch(1.0f).build();
    public static final Mix playerFireMix5 = new Mix.MixMaker(playerFire).volume(0.05f).pitch(1.025f).build();
    public static final Mix playerFireMix6 = new Mix.MixMaker(playerFire).volume(0.05f).pitch(1.05f).build();
    public static final Mix playerFireMix7 = new Mix.MixMaker(playerFire).volume(0.05f).pitch(1.075f).build();
    public static final Mix playerFireMix8 = new Mix.MixMaker(playerFire).volume(0.05f).pitch(1.1f).build();

    public static final Mix[] playerFireMegaMix = new Mix[]{playerFireMix1, playerFireMix2, playerFireMix3, playerFireMix4,
            playerFireMix5,
            playerFireMix6,
            playerFireMix7,
            playerFireMix8
    };


    public static final String enemyFire = "audio/sounds/projectile/laser2.ogg";

    public static final Mix enemyFireMix1 = new Mix.MixMaker(enemyFire).volume(0.05f).pitch(0.825f).build();
    public static final Mix enemyFireMix2 = new Mix.MixMaker(enemyFire).volume(0.05f).pitch(0.85f).build();
    public static final Mix enemyFireMix3 = new Mix.MixMaker(enemyFire).volume(0.05f).pitch(0.875f).build();
    public static final Mix enemyFireMix4 = new Mix.MixMaker(enemyFire).volume(0.05f).pitch(0.9f).build();
    public static final Mix enemyFireMix5 = new Mix.MixMaker(enemyFire).volume(0.05f).pitch(0.925f).build();
    public static final Mix enemyFireMix6 = new Mix.MixMaker(enemyFire).volume(0.05f).pitch(0.95f).build();
    public static final Mix enemyFireMix7 = new Mix.MixMaker(enemyFire).volume(0.05f).pitch(0.975f).build();
    public static final Mix enemyFireMix8 = new Mix.MixMaker(enemyFire).volume(0.05f).pitch(1.0f).build();

    public static final Mix[] enemyFireMegaMix = new Mix[]{enemyFireMix1, enemyFireMix2, enemyFireMix3, enemyFireMix4,
            enemyFireMix5,
            enemyFireMix6,
            enemyFireMix7,
            enemyFireMix8
    };


    public static final String explosion1 = "audio/sounds/explosions/1_Explosion.ogg";
    public static final String explosion2 = "audio/sounds/explosions/2_Explosion.ogg";
    public static final String explosion3 = "audio/sounds/explosions/3_Explosion.ogg";
    public static final String explosion4 = "audio/sounds/explosions/4_Explosion.ogg";

    public static final String[] explosionStrings = new String[]{explosion1, explosion2, explosion3, explosion4};

    public static final Mix explosionMix1 = new Mix.MixMaker(explosion1).volume(0.1f).build();
    public static final Mix explosionMix2 = new Mix.MixMaker(explosion2).volume(0.1f).build();
    public static final Mix explosionMix3 = new Mix.MixMaker(explosion3).volume(0.1f).build();
    public static final Mix explosionMix4 = new Mix.MixMaker(explosion4).volume(0.1f).build();

    public static final Mix[] explosionMegaMix = new Mix[]{explosionMix1, explosionMix2, explosionMix3, explosionMix4};

    public static final Mix bigExplosionMix1 = new Mix.MixMaker(explosion1).volume(0.1f).pitch(0.5f).build();
    public static final Mix bigExplosionMix2 = new Mix.MixMaker(explosion2).volume(0.1f).pitch(0.5f).build();
    public static final Mix bigExplosionMix3 = new Mix.MixMaker(explosion3).volume(0.1f).pitch(0.5f).build();
    public static final Mix bigExplosionMix4 = new Mix.MixMaker(explosion4).volume(0.1f).pitch(0.5f).build();

    public static final Mix[] bigExplosionMegaMix = new Mix[]{bigExplosionMix1, bigExplosionMix2, bigExplosionMix3, bigExplosionMix4};


    public static final Mix quietExplosionMix1 = new Mix.MixMaker(explosion1).volume(0.005f).build();
    public static final Mix quietExplosionMix2 = new Mix.MixMaker(explosion2).volume(0.005f).build();
    public static final Mix quietExplosionMix3 = new Mix.MixMaker(explosion3).volume(0.005f).build();
    public static final Mix quietExplosionMix4 = new Mix.MixMaker(explosion4).volume(0.005f).build();

    public static final Mix[] queitExplosionMegaMix = new Mix[]{quietExplosionMix1, quietExplosionMix2, quietExplosionMix3, quietExplosionMix4};

    public static final String soundOfSilence = "audio/sounds/twosecondsilence.ogg";


    public static void loadSoundsToManager(AssetManager assetManager){

        assetManager.load(playerFire, Sound.class);
        assetManager.load(enemyFire, Sound.class);
        assetManager.load(soundOfSilence, Sound.class);

        for(String s : explosionStrings) assetManager.load(s, Sound.class);
    }

}
