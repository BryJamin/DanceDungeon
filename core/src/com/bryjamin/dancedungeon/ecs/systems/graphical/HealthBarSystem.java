package com.bryjamin.dancedungeon.ecs.systems.graphical;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.Colors;
import com.bryjamin.dancedungeon.assets.FileStrings;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CenterMath;

import java.util.Locale;


/**
 * Created by BB on 15/11/2017.
 *
 * Used to draw the Health bars shown on screen.
 *
 * Also to create an 'effect' where after being hit white health is shown and then decreased over time.
 *
 *
 */

public class HealthBarSystem extends EntityProcessingSystem {

    private ComponentMapper<UnitComponent> unitM;

    private TileSystem tileSystem;

    private float initialHealthBarHeight = Measure.units(0.75f);
    private float initialHealthBarOffsetY = Measure.units(-1.5f);

    private final float whiteHealthBarSpeed = Measure.units(25f);
    private final float redHealthBarSpeed = Measure.units(80f);

    private Color bottomBarColor = new Color(Color.BLACK);
    private Color middleBarColor = new Color(Color.RED);
    private Color topBarColor = new Color(Colors.HEATH_BAR_COLOR);

    private Color healthTextColor = new Color(Color.WHITE);

    private Vector2 v2 = new Vector2();


    private SpriteBatch batch;

    private TextureAtlas atlas;

    private BitmapFont healthFont;
    private GlyphLayout glyphLayout = new GlyphLayout();


    private ObjectMap<Entity, HealthBar> entityHealthBarObjectMap = new ObjectMap<Entity, HealthBar>();

    public HealthBarSystem(MainGame game) {
        super(Aspect.all(UnitComponent.class, PositionComponent.class, CenteringBoundComponent.class, CoordinateComponent.class));
        this.batch = game.batch;
        this.atlas = game.assetManager.get(FileStrings.SPRITE_ATLAS_FILE, TextureAtlas.class);
        healthFont = game.assetManager.get(Fonts.SMALL, BitmapFont.class);
    }

    @Override
    protected boolean checkProcessing() {
        return false;
    }


    @Override
    protected void process(Entity e) {


        Rectangle rect = tileSystem.getCellDimensions();

        PositionComponent positionComponent = e.getComponent(PositionComponent.class);
        UnitComponent unitComponent = e.getComponent(UnitComponent.class);
        HealthBar healthBar = entityHealthBarObjectMap.get(e);

        float maxHealth = unitComponent.getUnitData().getMaxHealth();
        float health = unitComponent.getUnitData().getHealth() < 0 ? 0 : unitComponent.getUnitData().getHealth();

        //Black bar
        float width = ((rect.getWidth() / 5) * 3.5f);

        float currentHealthBarWidth = (health / maxHealth) * width;
        float offsetX = rect.getWidth() / 5;


        //Sets up the red health bar. If the red health bar is less than the actual player's health
        //It increases in size until it reaches the correct length.

        if (healthBar.redHealthBarLength >= currentHealthBarWidth) { //The red health bar can not be greater than the player's health
            healthBar.redHealthBarLength = currentHealthBarWidth;
        } else if (healthBar.redHealthBarLength < currentHealthBarWidth) {
            healthBar.redHealthBarLength = currentHealthBarWidth; //redHealthBarSpeed * world.delta;
            if (healthBar.redHealthBarLength > currentHealthBarWidth)
                healthBar.redHealthBarLength = currentHealthBarWidth;
        }


        if (healthBar.whiteHealthBarLength < healthBar.redHealthBarLength) { //The white health bar can not be less than the red
            healthBar.whiteHealthBarLength = healthBar.redHealthBarLength;
            healthBar.whiteHealthBarTimer = healthBar.whiteHealthBarResetTimer;
        } else if (healthBar.whiteHealthBarLength >= healthBar.redHealthBarLength) {

            //The white health bar decreases only after period of time to return the red health bar's length.
            healthBar.whiteHealthBarTimer -= world.delta;

            if (healthBar.whiteHealthBarTimer <= 0) {
                healthBar.whiteHealthBarLength -= whiteHealthBarSpeed * world.delta;
            }

        }

        CenteringBoundComponent centeringBoundComponent = e.getComponent(CenteringBoundComponent.class);

        Vector2 center = centeringBoundComponent.bound.getCenter(v2);


        float x = CenterMath.centerOnPositionX(rect.getWidth(), center.x) + offsetX; //positionComponent.getX() + CenterMath.offsetX(centeringBoundComponent.bound.getWidth(), rect.getWidth());
        float y = CenterMath.centerOnPositionY(rect.getHeight(), center.y); //positionComponent.getY() + initialHealthBarOffsetY;

        batch.setColor(bottomBarColor);
        batch.draw(atlas.findRegion(TextureStrings.BLOCK),
                x, y,
                width,
                initialHealthBarHeight);


        batch.setColor(middleBarColor);
        batch.draw(atlas.findRegion(TextureStrings.BLOCK),
                x, y,
                healthBar.whiteHealthBarLength,
                initialHealthBarHeight);


        batch.setColor(topBarColor);
        batch.draw(atlas.findRegion(TextureStrings.BLOCK),
                x, y,
                healthBar.redHealthBarLength,
                initialHealthBarHeight);


        batch.setColor(topBarColor);
        batch.draw(atlas.findRegion(TextureStrings.BLOCK),
                x, y,
                healthBar.redHealthBarLength,
                initialHealthBarHeight);

        glyphLayout.setText(healthFont, String.format(Locale.ENGLISH, "%s", (int) health), healthTextColor, rect.getWidth() / 5, Align.center, false);

        BitmapFontCache bitmapFontCache = new BitmapFontCache(healthFont);

        bitmapFontCache.addText(glyphLayout, x - offsetX,
                y + glyphLayout.height + CenterMath.offsetY(rect.getWidth() / 5, glyphLayout.height));

        //applyHighlightToText(e, bitmapFontCache, textDescription.getText());

        bitmapFontCache.draw(batch);



    }


    @Override
    public void inserted(Entity e) {
        entityHealthBarObjectMap.put(e, new HealthBar());
    }

    @Override
    public void removed(Entity e) {
        entityHealthBarObjectMap.remove(e);
    }


    @Override
    protected void begin() {
        if (!batch.isDrawing()) {
            batch.begin();
        }
    }

    @Override
    protected void end() {
        batch.end();
    }


    private class HealthBar {

        public float redHealthBarLength;
        public float whiteHealthBarLength;

        public float whiteHealthBarTimer = 0.35f;
        public final float whiteHealthBarResetTimer = 0.35f;

        public float fadeTimer;
        public final float fadeTimerResetTime = 1.5f;

        public float alpha = 0;


    }


}
