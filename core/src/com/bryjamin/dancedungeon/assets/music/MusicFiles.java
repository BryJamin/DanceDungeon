package com.bryjamin.dancedungeon.assets.music;

import com.bryjamin.dancedungeon.utils.sound.Mix;

public class MusicFiles {

        public static final Mix BG_MAIN_MENU = new Mix.MixMaker("audio/music/Field-02.ogg").build();
        public static final Mix MAP_MUSIC = new Mix.MixMaker("audio/music/SecondStep-01.ogg").volume(0.7f).build();
        public static final Mix BATTLE_MUSIC = new Mix.MixMaker("audio/music/Mire.ogg").build();


}
