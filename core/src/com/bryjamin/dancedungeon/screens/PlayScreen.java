package com.bryjamin.dancedungeon.screens;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.VelocityComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.systems.MovementSystem;
import com.bryjamin.dancedungeon.ecs.systems.RenderingSystem;
import com.bryjamin.dancedungeon.utils.GameDelta;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;
import com.bryjamin.dancedungeon.utils.texture.Layer;

/**
 * Created by BB on 11/10/2017.
 */

public class PlayScreen extends AbstractScreen {

    private OrthographicCamera gamecam;
    private Viewport gameport;
    private World world;

    public PlayScreen(MainGame game) {
        super(game);

        gamecam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gameport = new FitViewport(MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT, gamecam);

        gamecam.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);
        //gamecam.position.set(gameport.getWorldWidth() / 2, gameport.getWorldHeight() / 2, 0);
        gamecam.update();
        gameport.apply();


        createWorld();

    }



    public void createWorld(){

        WorldConfiguration config = new WorldConfigurationBuilder()
            .with(WorldConfigurationBuilder.Priority.HIGHEST,
                    new MovementSystem(),
                    new RenderingSystem(game, gameport)
            ).build();

        world = new World(config);

        Entity e = world.createEntity();
        e.edit().add(new PositionComponent(0,0));
        e.edit().add(new VelocityComponent(Measure.units(5f), 0));
        e.edit().add(new DrawableComponent(Layer.ENEMY_LAYER_MIDDLE,
                new DrawableDescription.DrawableDescriptionBuilder(TextureStrings.BLOCK).size(Measure.units(10)).build()));

    }

    @Override
    public void resize(int width, int height) {
        gameport.update(width, height);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(gamecam.combined);

        gamecam.update();

        GameDelta.delta(world, delta);
        world.process();


    }
}
