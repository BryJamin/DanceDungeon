package com.bryjamin.dancedungeon.ecs.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.FileStrings;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by BB on 10/10/2017.
 */

public class RenderingSystem extends EntitySystem {

    private ComponentMapper<PositionComponent> positionm;
    private ComponentMapper<DrawableComponent> drawablem;


    private SpriteBatch batch;
    private Viewport gameport;
    private AssetManager assetManager;
    private TextureAtlas atlas;

    private ArrayList<Entity> orderedEntities = new ArrayList<Entity>();


    /**
     * Used to draw Entities behind the UI
     *
     */
    public RenderingSystem(MainGame game, Viewport gameport) {
        super(Aspect.all(PositionComponent.class).one(DrawableComponent.class));
        this.batch = game.batch;
        this.gameport = gameport;
        this.assetManager = game.assetManager;
        this.atlas = assetManager.get(FileStrings.SPRITE_ATLAS_FILE, TextureAtlas.class);
    }

    @Override
    protected void processSystem() {
        for (int i = 0; orderedEntities.size() > i; i++) {
            if(process(orderedEntities.get(i))){
            };
        }
    }

    protected boolean process(Entity e) {

        DrawableComponent drawableComponent = drawablem.get(e);
        PositionComponent positionComponent = positionm.get(e);

        for(DrawableDescription drawableDescription : drawableComponent.drawables) {

            float originX = drawableDescription.getWidth() * 0.5f;
            float originY = drawableDescription.getHeight() * 0.5f;

            TextureRegion tr = atlas.findRegion(drawableDescription.getRegion(), drawableDescription.getIndex());
            if(tr == null) tr =  atlas.findRegion(TextureStrings.BLOCK);

            batch.draw(tr,
                    positionComponent.getX() + drawableDescription.getOffsetX(),
                    positionComponent.getY() + drawableDescription.getOffsetY(),
                    originX, originY,
                    drawableDescription.getWidth(), drawableDescription.getHeight(),
                    drawableDescription.getScaleX(), drawableDescription.getScaleY(),
                    drawableDescription.getRotation());

        }


        return true;
    }

    @Override
    public void inserted(Entity e) {
        orderedEntities.add(e);
        Collections.sort(orderedEntities, new Comparator<Entity>() {
            @Override
            public int compare(Entity e1, Entity e2) {

                Integer layer1 = 0;
                Integer layer2 = 0;

                if(drawablem.has(e1)) {
                    layer1 = drawablem.get(e1).layer;
                }

                if(drawablem.has(e2)) {
                    layer2 = drawablem.get(e2).layer;
                }

                return layer1.compareTo(layer2);
            }
        });
    }

    @Override
    public void removed(Entity e) {
        orderedEntities.remove(e);
    }

}
