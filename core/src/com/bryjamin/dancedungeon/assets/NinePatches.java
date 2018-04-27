package com.bryjamin.dancedungeon.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class NinePatches {

    public static NinePatch getDefaultBorderPatch(TextureAtlas atlas){
        return getBorderPatch(atlas, new Color(Colors.TABLE_BORDER_COLOR));
    }

    public static NinePatch getBorderPatch(TextureAtlas atlas, Color c){

        NinePatch ninePatch = new NinePatch(atlas.findRegion(TextureStrings.BORDER), 16, 16, 16, 16);
        ninePatch.setColor(c);

        return ninePatch;
    }

    public static NinePatch getBorderPatch(TextureAtlas atlas, float alpha){

        Color c = new Color(Colors.TABLE_BORDER_COLOR);
        c.a = alpha;

        return getBorderPatch(atlas, c);
    }

    public static NinePatchDrawable getDefaultNinePatch(TextureAtlas atlas){
        return new NinePatchDrawable(getDefaultBorderPatch(atlas));
    }


}
