package com.bryjamin.dancedungeon;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.bryjamin.dancedungeon.assets.FileStrings;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.assets.music.SoundFiles;
import com.bryjamin.dancedungeon.ecs.systems.audio.MusicSystem;
import com.bryjamin.dancedungeon.factories.unit.UnitLibrary;
import com.bryjamin.dancedungeon.factories.spells.SkillLibrary;
import com.bryjamin.dancedungeon.screens.LoadingScreen;
import com.bryjamin.dancedungeon.utils.Measure;


/**
 * Created by BB on 08/10/2017.
 */


public class MainGame extends Game {

    public static final float GAME_HEIGHT = 1800;
    public static final float GAME_WIDTH = 3000;
    public static final float GAME_BORDER = 150;



    public static final int GAME_UNITS = 30;

    //This means there are 96 tiles wide,
    //60 tiles high

    public SpriteBatch batch;
    public AssetManager assetManager = new AssetManager();
    public MusicSystem musicSystem = new MusicSystem();

    @Override
    public void create () {
        batch = new SpriteBatch(700);
        //Gdx.input.setCursorCatched(true);
        //Gdx.input.setCursorPosition(0, 0);

        assetManager.load(FileStrings.SPRITE_ATLAS_FILE, TextureAtlas.class);

       // SoundFileStrings.loadSoundsToManager(assetManager);

        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));


        FreetypeFontLoader.FreeTypeFontLoaderParameter size1Params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        size1Params.fontFileName = FileStrings.DEFAULT_FONT_FILE;
        size1Params.fontParameters.size = (int) Measure.units(3f);
        size1Params.fontParameters.borderColor = new Color(Color.BLACK);
        size1Params.fontParameters.borderWidth = Measure.units(0.25f);
        size1Params.fontParameters.minFilter = Texture.TextureFilter.Linear;
        size1Params.fontParameters.magFilter = Texture.TextureFilter.Linear;
        assetManager.load(Fonts.MEDIUM, BitmapFont.class, size1Params);



        FreetypeFontLoader.FreeTypeFontLoaderParameter small = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        small.fontFileName = FileStrings.SMALL_FONT_FILE;
        small.fontParameters.size = (int) Measure.units(2.25f);
        small.fontParameters.borderColor = new Color(Color.BLACK);
        small.fontParameters.borderWidth = 0;
        small.fontParameters.minFilter = Texture.TextureFilter.Linear;
        small.fontParameters.magFilter = Texture.TextureFilter.Linear;
        assetManager.load(Fonts.SMALL, BitmapFont.class, small);


        FreetypeFontLoader.FreeTypeFontLoaderParameter title = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        title.fontFileName = FileStrings.DEFAULT_FONT_FILE;
        title.fontParameters.size = (int) Measure.units(7.5f);
        title.fontParameters.minFilter = Texture.TextureFilter.Linear;
        title.fontParameters.magFilter = Texture.TextureFilter.Linear;
        assetManager.load(Fonts.LARGE, BitmapFont.class, title);


        SoundFiles.loadSoundsToManager(assetManager);


        //LOAD IN DATA FROM JSON
        SkillLibrary.loadFromJSON();
        UnitLibrary.loadFromJSON();

        setScreen(new LoadingScreen(this));
    }
}
