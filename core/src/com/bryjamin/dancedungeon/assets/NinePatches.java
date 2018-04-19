package com.bryjamin.dancedungeon.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class NinePatches {

    public static NinePatch getBorderPatch(TextureAtlas atlas){
        return new NinePatch(atlas.findRegion(TextureStrings.BORDER), 8, 8, 8, 8);
    }


}
