package com.bryjamin.dancedungeon.ecs.systems.graphical;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.FileStrings;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.BoundComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CenterMath;

/**
 * Created by BB on 15/11/2017.
 */

public class HealthBarSystem extends EntityProcessingSystem {

    private float initialHealthBarWidth = Measure.units(6.5f);
    private float initialHealthBarHeight = Measure.units(1f);
    private float initialHealthBarOffsetY = Measure.units(-1.5f);

    private final float whiteHealthBarSpeed = Measure.units(25f);
    private final float redHealthBarSpeed = Measure.units(80f);

    private Color bottomBarColor = new Color(Color.BLACK);
    private Color middleBarColor = new Color(Color.WHITE);
    private Color topBarColor = new Color(Color.RED);


    private SpriteBatch batch;

    private TextureAtlas atlas;

    private ObjectMap<Entity, HealthBar> entityHealthBarObjectMap = new ObjectMap<Entity, HealthBar>();

    public HealthBarSystem(MainGame game, Viewport gameport) {
        super(Aspect.all(HealthComponent.class, PositionComponent.class, BoundComponent.class));
        this.batch = game.batch;
        this.atlas = game.assetManager.get(FileStrings.SPRITE_ATLAS_FILE, TextureAtlas.class);
    }


    @Override
    protected void process(Entity e) {

        PositionComponent positionComponent = e.getComponent(PositionComponent.class);
        HealthComponent healthComponent = e.getComponent(HealthComponent.class);
        HealthBar healthBar = entityHealthBarObjectMap.get(e);

        float maxHealth = healthComponent.maxHealth;
        float health = healthComponent.health;


        //Black bar


        float currentHealthBarWidth = (health / maxHealth) * initialHealthBarWidth;


        if (healthBar.redHealthBarLength >= currentHealthBarWidth) {
            healthBar.redHealthBarLength = currentHealthBarWidth;
        } else if (healthBar.redHealthBarLength < currentHealthBarWidth) {
            healthBar.redHealthBarLength += redHealthBarSpeed * world.delta;
            if (healthBar.redHealthBarLength > currentHealthBarWidth)
                healthBar.redHealthBarLength = currentHealthBarWidth;
        }


        if (healthBar.whiteHealthBarLength < healthBar.redHealthBarLength) {
            healthBar.whiteHealthBarLength = healthBar.redHealthBarLength;
            healthBar.whiteHealthBarTimer = healthBar.whiteHealthBarResetTimer;
        } else if (healthBar.whiteHealthBarLength >= healthBar.redHealthBarLength) {

            healthBar.whiteHealthBarTimer -= world.delta;

            if (healthBar.whiteHealthBarTimer <= 0) {
                healthBar.whiteHealthBarLength -= whiteHealthBarSpeed * world.delta;
            }

        }

        BoundComponent boundComponent = e.getComponent(BoundComponent.class);

        float x = positionComponent.getX() + CenterMath.offsetX(boundComponent.bound.getWidth(), initialHealthBarWidth);
        float y = positionComponent.getY() + initialHealthBarOffsetY;

        batch.setColor(bottomBarColor);
        batch.draw(atlas.findRegion(TextureStrings.BLOCK),
                x, y,
                initialHealthBarWidth,
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

        public float redHealthBarLength = initialHealthBarWidth;
        public float whiteHealthBarLength;

        public float whiteHealthBarTimer = 0.5f;
        public final float whiteHealthBarResetTimer = 0.5f;

        public float fadeTimer;
        public final float fadeTimerResetTime = 1.5f;

        public float alpha = 0;


    }


}
