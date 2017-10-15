package com.bryjamin.dancedungeon.screens;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.ecs.components.ExplosionComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.systems.MovementSystem;
import com.bryjamin.dancedungeon.ecs.systems.RenderingSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.ExplosionSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.HealthSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.BoundsDrawingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.UpdatePositionSystem;
import com.bryjamin.dancedungeon.factories.enemy.DummyFactory;
import com.bryjamin.dancedungeon.factories.player.PlayerFactory;
import com.bryjamin.dancedungeon.utils.GameDelta;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;

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
                    new UpdatePositionSystem())
                .with(WorldConfigurationBuilder.Priority.HIGH,
                    new ExplosionSystem(),
                    new HealthSystem(),
                    new RenderingSystem(game, gameport),
                    new BoundsDrawingSystem(batch)
            ).build();

        world = new World(config);


        BagToEntity.bagToEntity(world.createEntity(), new PlayerFactory(assetManager).player(Measure.units(10f), Measure.units(10f)));
        BagToEntity.bagToEntity(world.createEntity(), new DummyFactory(assetManager).targetDummy(Measure.units(10f), Measure.units(50f)));
        BagToEntity.bagToEntity(world.createEntity(), new DummyFactory(assetManager).targetDummy(Measure.units(25f), Measure.units(50f)));


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

        handleInput(delta);
        GameDelta.delta(world, delta);
        world.process();


    }

    public void handleInput(float dt) {


        InputMultiplexer multiplexer = new InputMultiplexer();


        multiplexer.addProcessor(new InputAdapter() {

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {

                Vector2 input = gameport.unproject(new Vector2(screenX, screenY));


                float size = Measure.units(20f);
                //float height;

                Entity explosion = world.createEntity();
                explosion.edit().add(new PositionComponent(input));
                explosion.edit().add(new ExplosionComponent(5));
                explosion.edit().add(new HitBoxComponent(new HitBox(new Rectangle(input.x,input.y, size, size), -size / 2, -size / 2)));

                return false;
            }

        });


        Gdx.input.setInputProcessor(multiplexer);


    }




}
