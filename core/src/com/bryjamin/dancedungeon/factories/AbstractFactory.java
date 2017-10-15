package com.bryjamin.dancedungeon.factories;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.bryjamin.dancedungeon.assets.FileStrings;

/**
 * Created by BB on 15/10/2017.
 *
 * Abstract class for factories which consist mainly of Component Bags
 */

public abstract class AbstractFactory {

    protected AssetManager assetManager;
    protected TextureAtlas atlas;

    public AbstractFactory(AssetManager assetManager){
        this.assetManager = assetManager;
        this.atlas = assetManager.get(FileStrings.SPRITE_ATLAS_FILE, TextureAtlas.class);
    }
/*
    public AbstractFactory(AssetManager assetManager, TextureAtlas atlas){
        this.assetManager = assetManager;
        this.atlas = atlas;
    }*/

}
