package com.bryjamin.dancedungeon.ecs.systems.graphical;

import com.artemis.BaseSystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.FileStrings;
import com.bryjamin.dancedungeon.ecs.components.battle.AbilityPointComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.systems.FindPlayerSystem;
import com.bryjamin.dancedungeon.utils.Measure;

/**
 * Created by BB on 13/11/2017.
 */

public class UIRenderingSystem extends BaseSystem{

    public SpriteBatch batch;
    public Viewport gameport;
    private Camera gamecam;
    public AssetManager assetManager;
    public TextureAtlas atlas;

    private BitmapFont currencyFont;

    public UIRenderingSystem(MainGame game, Viewport gameport) {
        this.batch = game.batch;
        this.gameport = gameport;
        this.gamecam = gameport.getCamera();
        this.assetManager = game.assetManager;
        this.atlas = assetManager.get(FileStrings.SPRITE_ATLAS_FILE, TextureAtlas.class);
        this.currencyFont = assetManager.get(FileStrings.DEFAULT_FONT_NAME, BitmapFont.class);
    }

    @Override
    protected void begin() {
        if(!batch.isDrawing()) {
            batch.begin();
        }
    }

    @Override
    protected void processSystem() {

        float camX = gamecam.position.x - gamecam.viewportWidth / 2;
        float camY = gamecam.position.y - gamecam.viewportHeight / 2;

        float health = world.getSystem(FindPlayerSystem.class).getPlayerComponent(HealthComponent.class).health;

        float ap = world.getSystem(FindPlayerSystem.class).getPlayerComponent(AbilityPointComponent.class).abilityPoints;

        currencyFont.draw(batch, "health: " + health,
                camX + Measure.units(3f),
                gamecam.position.y + (gamecam.viewportHeight / 2) - Measure.units(1.25f),
                0, Align.left, true);


        currencyFont.draw(batch, "ap: " + ap,
                camX + Measure.units(40f),
                gamecam.position.y + (gamecam.viewportHeight / 2) - Measure.units(1.25f),
                0, Align.left, true);




    }

    @Override
    protected void end() {
        batch.end();
    }
}
