package com.bryjamin.dancedungeon.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created by BB on 15/02/2018.
 */

public class Skins {

    static {
    }


    public static Skin DEFAULT_SKIN(AssetManager assetManager){
        Skin uiSkin = new Skin();

        uiSkin.add("myFont12", assetManager.get(Fonts.MEDIUM));
        uiSkin.add(Fonts.SMALL_FONT_STYLE_NAME, assetManager.get(Fonts.SMALL));
        uiSkin.add(Fonts.LARGE_FONT_STYLE_NAME, assetManager.get(Fonts.LARGE));
        uiSkin.addRegions(assetManager.get(FileStrings.SPRITE_ATLAS_FILE, TextureAtlas.class));

        //Add Button Skin
        uiSkin.add("border", NinePatches.getDefaultBorderPatch(assetManager.get(FileStrings.SPRITE_ATLAS_FILE, TextureAtlas.class)));

        //Add UI skin regions
        uiSkin.addRegions(new TextureAtlas(Gdx.files.internal("uiskin.atlas")));
        uiSkin.load(Gdx.files.internal("uiskin.json"));

        return uiSkin;
    }


}
