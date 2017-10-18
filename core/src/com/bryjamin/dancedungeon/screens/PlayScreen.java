package com.bryjamin.dancedungeon.screens;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.ecs.DirectionalInputAdapter;
import com.bryjamin.dancedungeon.ecs.components.battle.DispellableComponent;
import com.bryjamin.dancedungeon.ecs.systems.ExpireSystem;
import com.bryjamin.dancedungeon.ecs.systems.MovementSystem;
import com.bryjamin.dancedungeon.ecs.systems.RenderingSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BlinkOnHitSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.DeathSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.DispelSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.ExplosionSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.HealthSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.BoundsDrawingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.UpdatePositionSystem;
import com.bryjamin.dancedungeon.factories.enemy.DummyFactory;
import com.bryjamin.dancedungeon.factories.player.PlayerFactory;
import com.bryjamin.dancedungeon.utils.AngleMath;
import com.bryjamin.dancedungeon.utils.GameDelta;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;

/**
 * Created by BB on 11/10/2017.
 */

public class PlayScreen extends AbstractScreen {

    private OrthographicCamera gamecam;
    private Viewport gameport;
    private World world;

    private DirectionalInputAdapter directionalInputAdapter;

    public PlayScreen(MainGame game) {
        super(game);

        gamecam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gameport = new FitViewport(MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT, gamecam);

        gamecam.setToOrtho(false, MainGame.GAME_WIDTH, MainGame.GAME_HEIGHT);
        //gamecam.position.set(gameport.getWorldWidth() / 2, gameport.getWorldHeight() / 2, 0);
        gamecam.update();
        gameport.apply();

        directionalInputAdapter = new DirectionalInputAdapter(new DirectionalInputAdapter.DirectionalGestureListener() {
            @Override
            public boolean tap(float x, float y, int count, int button) {
                return true;
            }

            @Override
            public boolean swipe(float startX, float startY, float endX, float endY) {


                double angle = AngleMath.angleOfTravelInDegrees(startX, startY, endX, endY);


                System.out.println(angle);

                float wiggleRoom = 22.5f;

                if(angle < wiggleRoom && angle > -wiggleRoom || angle > 180 - wiggleRoom && angle < -180 + wiggleRoom)  {
                    System.out.println("HORI");
                    world.getSystem(DispelSystem.class).dispel(DispellableComponent.Type.HORIZONTAL);
                } else if(angle < 90 + wiggleRoom && angle > 90 - wiggleRoom || angle > -90 - wiggleRoom && angle < -90 + wiggleRoom) {
                    System.out.println("vert");
                    world.getSystem(DispelSystem.class).dispel(DispellableComponent.Type.VERTICAL);
                } else if(angle < 135 + wiggleRoom && angle > 135 - wiggleRoom || angle > -45 - wiggleRoom && angle < -45 + wiggleRoom){
                    System.out.println("Front Slash");
                    world.getSystem(DispelSystem.class).dispel(DispellableComponent.Type.FRONT_SLASH);
                } else if(angle < 45 + wiggleRoom && angle > 45 - wiggleRoom || angle > -135 - wiggleRoom && angle < -135 + wiggleRoom){
                    System.out.println("Back Slash");
                    world.getSystem(DispelSystem.class).dispel(DispellableComponent.Type.BACK_SLASH);
                }


                return true;
            }
        });


        createWorld();

    }



    public void createWorld(){

        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(WorldConfigurationBuilder.Priority.HIGHEST,
                    new MovementSystem(),
                    new UpdatePositionSystem())
                .with(WorldConfigurationBuilder.Priority.HIGH,
                        new ExplosionSystem(),
                        new DispelSystem(),
                        new HealthSystem(),
                        new BlinkOnHitSystem(),
                        new DeathSystem(),
                        new ExpireSystem(),
                        new RenderingSystem(game, gameport),
                        new BoundsDrawingSystem(batch)
            ).build();

        world = new World(config);


        BagToEntity.bagToEntity(world.createEntity(), new PlayerFactory(assetManager).player(Measure.units(10f), Measure.units(10f)));


        ComponentBag bag = new DummyFactory(assetManager).targetDummyLeft(Measure.units(10f), Measure.units(50f));
        BagToEntity.bagToEntity(world.createEntity(), bag);


        bag = new DummyFactory(assetManager).targetDummyVert(Measure.units(40f), Measure.units(50f));
        BagToEntity.bagToEntity(world.createEntity(), bag);


        bag = new DummyFactory(assetManager).targetDummyBackSlash(Measure.units(55f), Measure.units(50f));
        BagToEntity.bagToEntity(world.createEntity(), bag);

        BagToEntity.bagToEntity(world.createEntity(), new DummyFactory(assetManager).targetDummyFrontSlash(Measure.units(25f), Measure.units(50f)));


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


        multiplexer.addProcessor(directionalInputAdapter);

/*
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
                explosion.edit().add(new ExpireComponent(1f));

                return false;
            }

        });
*/


        Gdx.input.setInputProcessor(multiplexer);


    }




}
