package com.bryjamin.dancedungeon.ecs.systems.graphical;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.FileStrings;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.BlinkOnHitComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.GreyScaleComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.HighLightTextComponent;
import com.bryjamin.dancedungeon.ecs.systems.MovementSystem;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;
import com.bryjamin.dancedungeon.utils.texture.Highlight;
import com.bryjamin.dancedungeon.utils.texture.TextDescription;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by BB on 10/10/2017.
 */

public class RenderingSystem extends EntitySystem {

    private ComponentMapper<PositionComponent> positionm;
    private ComponentMapper<CenteringBoundaryComponent> boundm;
    private ComponentMapper<DrawableComponent> drawablem;
    private ComponentMapper<BlinkOnHitComponent> blinkOnHitm;
    private ComponentMapper<HighLightTextComponent> highlightM;

    private ComponentMapper<GreyScaleComponent> greyScaleMapper;

    private MovementSystem movementSystem;


    private SpriteBatch batch;
    private Viewport gameport;
    private AssetManager assetManager;
    private TextureAtlas atlas;

    private ArrayList<Entity> orderedEntities = new ArrayList<Entity>();

    private Color white = new Color(Color.WHITE);

    public ShaderProgram whiteShaderProgram;
    public ShaderProgram greyScaleShaderProgram;

    private GlyphLayout glyphLayout = new GlyphLayout();


    public TextureAtlas getAtlas() {
        return atlas;
    }

    /**
     * Used to draw Entities behind the UI
     */
    public RenderingSystem(MainGame game, Viewport gameport) {
        super(Aspect.all(PositionComponent.class).one(DrawableComponent.class));
        this.batch = game.batch;
        this.gameport = gameport;
        this.assetManager = game.assetManager;
        this.atlas = assetManager.get(FileStrings.SPRITE_ATLAS_FILE, TextureAtlas.class);
        loadShader();
    }


    public void loadShader() {
        whiteShaderProgram = new ShaderProgram(Gdx.files.internal(FileStrings.DEFAULT_VERTEX_SHADER),
                Gdx.files.internal(FileStrings.ALL_WHITE_FRAGMENT_SHADER));
        if (!whiteShaderProgram.isCompiled())
            throw new GdxRuntimeException("Couldn't compile shader: " + whiteShaderProgram.getLog());

        greyScaleShaderProgram = new ShaderProgram(Gdx.files.internal(FileStrings.DEFAULT_VERTEX_SHADER),
                Gdx.files.internal(FileStrings.GREYSCALE_FRAGMENT_SHADER));
        if (!greyScaleShaderProgram.isCompiled())
            throw new GdxRuntimeException("Couldn't compile shader: " + whiteShaderProgram.getLog());

    }


    @Override
    protected void processSystem() {
        for (int i = 0; orderedEntities.size() > i; i++) {
            if (process(orderedEntities.get(i))) {
            }
            ;
        }
    }

    @Override
    protected void begin() {
        batch.setProjectionMatrix(gameport.getCamera().combined);
        batch.begin();
        // Archetype archetype = new ArchetypeBuilder().add(PositionComponent.class).build(world);
    }

    @Override
    protected void end() {
        batch.end();
    }


    protected boolean process(Entity e) {

        DrawableComponent drawableComponent = drawablem.get(e);
        PositionComponent positionComponent = positionm.get(e);

        boolean shaderOn = false;


        if (blinkOnHitm.has(e)) {
            shaderOn = blinkOnHitm.get(e).isHit;
        }

        if (shaderOn) {
            batch.end();
            batch.setShader(whiteShaderProgram);
            batch.begin();
        }

        if (greyScaleMapper.has(e)) {
            shaderOn = true;
            batch.end();
            batch.setShader(greyScaleShaderProgram);
            batch.begin();
        }

        DrawableDescription drawableDescription = drawableComponent.drawables;


        float originX = drawableDescription.getOrigin() == null ?
                drawableDescription.getWidth() * 0.5f : drawableDescription.getOrigin().x;
        float originY = drawableDescription.getOrigin() == null ?
                drawableDescription.getHeight() * 0.5f : drawableDescription.getOrigin().y;

        batch.setColor(drawableDescription.getColor());

        if (drawableDescription instanceof TextureDescription) {

            TextureDescription textureDescription = (TextureDescription) drawableDescription;


            TextureRegion tr;

            try {
                tr = atlas.findRegion(textureDescription.getRegion(), textureDescription.getIndex());

                if (tr == null)
                    throw new Exception("No Texture Data for: " + textureDescription.getRegion() +
                            " index: " + textureDescription.getIndex());
            } catch (Exception ex) {
                ex.printStackTrace();
                tr = atlas.findRegion(TextureStrings.BLOCK);
            }

            //TODO currently testing, using integers instead of floats for positions
            batch.draw(tr,
                    (int) (positionComponent.getX() + drawableDescription.getOffsetX()),
                    (int) (positionComponent.getY() + drawableDescription.getOffsetY()),
                    originX, originY,
                    (int) drawableDescription.getWidth(), (int) drawableDescription.getHeight(),
                    drawableDescription.getScaleX(), drawableDescription.getScaleY(),
                    (float) drawableDescription.getRotation());

        } else if (drawableDescription instanceof TextDescription) {


            TextDescription textDescription = (TextDescription) drawableDescription;

            BitmapFont bmf = assetManager.get(textDescription.getFont(), BitmapFont.class);


            if (boundm.has(e)) {
                CenteringBoundaryComponent bc = boundm.get(e);
                glyphLayout.setText(bmf, textDescription.getText(), drawableDescription.getColor(), bc.bound.width, textDescription.getAlign(), false);

                BitmapFontCache bitmapFontCache = new BitmapFontCache(bmf);

                bitmapFontCache.addText(glyphLayout, positionComponent.getX(),
                        positionComponent.getY() + glyphLayout.height + CenterMath.offsetY(bc.bound.height, glyphLayout.height) + textDescription.getOffsetY());

                applyHighlightToText(e, bitmapFontCache, textDescription.getText());

                bitmapFontCache.draw(batch);

            } else {

                glyphLayout.setText(bmf, textDescription.getText(), drawableDescription.getColor(), textDescription.getWidth(), textDescription.getAlign(), false);

                BitmapFontCache bitmapFontCache = new BitmapFontCache(bmf);

                bitmapFontCache.addText(glyphLayout, positionComponent.getX(),
                        positionComponent.getY() + glyphLayout.height + CenterMath.offsetY(textDescription.getHeight(), glyphLayout.height) + textDescription.getOffsetY());

                applyHighlightToText(e, bitmapFontCache, textDescription.getText());

                bitmapFontCache.draw(batch);

            }


        }

        batch.setColor(white);

        
        if (shaderOn) removeShader();


        return true;
    }


    private void applyHighlightToText(Entity e, BitmapFontCache cache, String text) {

        if (highlightM.has(e)) {

            HighLightTextComponent hltc = highlightM.get(e);
            for (Highlight h : hltc.highLights) {
                if (text.length() > h.start && text.length() > h.end) //TODO Bit Redundant, might be better to throw an error earlier
                    cache.setColors(h.color, h.start, h.end);
            }
        }


    }


    private void removeShader() {
        batch.end();
        batch.setShader(null);
        batch.begin();
    }

    @Override
    public void inserted(Entity e) {
        orderedEntities.add(e);
        Collections.sort(orderedEntities, new Comparator<Entity>() {
            @Override
            public int compare(Entity e1, Entity e2) {

                Integer layer1 = 0;
                Integer layer2 = 0;

                if (drawablem.has(e1)) {
                    layer1 = drawablem.get(e1).layer;
                }

                if (drawablem.has(e2)) {
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
