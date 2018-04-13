package com.bryjamin.dancedungeon.desktop;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.bryjamin.dancedungeon.assets.FileStrings;

/**
 * Created by BB on 15/09/2017.
 *
 * Used to pack textures into a sprite sheet programmatically
 *
 */

public class PackTextures {



    public static void main (String args[]){

        packSpriteAtlas();
        packMapTileAtlas();


    }

    public static void packSpriteAtlas(){

        String projectPath = System.getProperty("user.dir");
        String inputDir = projectPath + "/images/tobepacked";
        String outputDir = projectPath;
        String packFileName = FileStrings.ATLAS_NAME;

        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth = 2048;
        settings.maxHeight = 2048;
        settings.filterMin = Texture.TextureFilter.Nearest;
        settings.filterMag = Texture.TextureFilter.Nearest;
        settings.paddingX = 2;
        settings.paddingY = 2;
        settings.duplicatePadding = true;
        settings.combineSubdirectories = true;

        TexturePacker.process(settings, inputDir,outputDir,packFileName);

    }

    public static void packMapTileAtlas(){

        String projectPath = System.getProperty("user.dir");
        String inputDir = projectPath + "/images/maptiles";
        String outputDir = projectPath;
        String packFileName = "map";

        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.maxWidth = 2048;
        settings.maxHeight = 2048;
        settings.filterMin = Texture.TextureFilter.Nearest;
        settings.filterMag = Texture.TextureFilter.Nearest;
        settings.paddingX = 0;
        settings.paddingY = 0;
        settings.combineSubdirectories = true;

        TexturePacker.process(settings, inputDir,outputDir,packFileName);


    }


}
