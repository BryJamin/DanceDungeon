package com.bryjamin.dancedungeon.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by BB on 15/02/2018.
 */

public class Skins {


    public static Skin DEFAULT_SKIN(AssetManager assetManager){
        Skin uiSkin = new Skin();
        uiSkin.add("myFont12", assetManager.get(Fonts.MEDIUM));
        uiSkin.add(Fonts.SMALL_FONT, assetManager.get(Fonts.SMALL));
        uiSkin.addRegions(new TextureAtlas(Gdx.files.internal("uiskin.atlas")));
        uiSkin.load(Gdx.files.internal("uiskin.json"));

        return uiSkin;
    }


}
