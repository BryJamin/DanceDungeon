package com.bryjamin.dancedungeon.assets.music;

import com.bryjamin.dancedungeon.utils.sound.Mix;

public class MusicFiles {

        public static final Mix BG_MAIN_MENU = new Mix.MixMaker("audio/music/Field-02.ogg").build();

        public static final Mix BG_LEVEL_ONE = new Mix.MixMaker("audio/music/Field-02.ogg").build();

        public static final Mix MAP_MUSIC = new Mix.MixMaker("audio/music/SecondStep-01.ogg").volume(0.7f).build();

        public static final Mix BG_LEVEL_THREE = new Mix.MixMaker("audio/music/SittingAloneInMyRoomAndThisIsAllICouldComeUpWith-03.ogg").build();

        public static final Mix BG_LEVEL_FOUR = new Mix.MixMaker("audio/music/Mire.ogg").build();

        public static final Mix BG_LEVEL_FIVE = new Mix.MixMaker("audio/music/Forward-01.ogg").build();

}
