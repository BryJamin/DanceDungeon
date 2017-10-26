package com.bryjamin.dancedungeon.screens;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.DirectionalInputAdapter;
import com.bryjamin.dancedungeon.ecs.components.BoundComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.VelocityComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.BulletComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.DispellableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.FriendlyComponent;
import com.bryjamin.dancedungeon.ecs.systems.ExpireSystem;
import com.bryjamin.dancedungeon.ecs.systems.FindPlayerSystem;
import com.bryjamin.dancedungeon.ecs.systems.MoveToTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.MovementSystem;
import com.bryjamin.dancedungeon.ecs.systems.RenderingSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BlinkOnHitSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BulletSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.DeathSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.DispelSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.ExplosionSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.HealthSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.BoundsDrawingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.UpdatePositionSystem;
import com.bryjamin.dancedungeon.factories.decor.FloorFactory;
import com.bryjamin.dancedungeon.factories.enemy.DummyFactory;
import com.bryjamin.dancedungeon.factories.player.PlayerFactory;
import com.bryjamin.dancedungeon.utils.GameDelta;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.math.AngleMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.DrawableDescription;
import com.bryjamin.dancedungeon.utils.texture.Layer;


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

                Vector3 input = gameport.unproject(new Vector3(x, y, 0));


                if(world.getSystem(TurnSystem.class).turn == TurnSystem.TURN.ALLY) {

                    world.getSystem(TileSystem.class).isMovementSquare(input.x, input.y,
                            world.getSystem(FindPlayerSystem.class).getPlayerComponent(PositionComponent.class),
                            world.getSystem(FindPlayerSystem.class).getPlayerComponent(BoundComponent.class));

                    PositionComponent pc = world.getSystem(FindPlayerSystem.class).getPlayerComponent(PositionComponent.class);


                    Entity bullet = world.createEntity();


                    double angle = AngleMath.angleOfTravel(pc.getX(), pc.getY(), input.x, input.y);

                    bullet.edit().add(new PositionComponent(pc.getX(), pc.getY()));
                    bullet.edit().add(new BulletComponent(2));
                    bullet.edit().add(new BoundComponent(new Rectangle(pc.getX(), pc.getY(), Measure.units(5f), Measure.units(5f))));
                    bullet.edit().add(new VelocityComponent(AngleMath.velocityX(Measure.units(50f), angle), AngleMath.velocityY(Measure.units(50f), angle)));
                    bullet.edit().add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE, new DrawableDescription.DrawableDescriptionBuilder(TextureStrings.BLOCK)
                            .size(Measure.units(5))
                            .color(new Color(Color.YELLOW))
                            .build()));
                    bullet.edit().add(new FriendlyComponent());

                    world.getSystem(TurnSystem.class).setUp(TurnSystem.TURN.ENEMY);

                }


                return true;
            }

            @Override
            public boolean swipe(float startX, float startY, float endX, float endY) {


                double angle = AngleMath.angleOfTravelInDegrees(startX, startY, endX, endY);

                float wiggleRoom = 22.5f;

                if(angle < wiggleRoom && angle > -wiggleRoom || angle > 180 - wiggleRoom && angle < -180 + wiggleRoom)  {
                    world.getSystem(DispelSystem.class).dispel(DispellableComponent.Type.HORIZONTAL);
                } else if(angle < 90 + wiggleRoom && angle > 90 - wiggleRoom || angle > -90 - wiggleRoom && angle < -90 + wiggleRoom) {
                    world.getSystem(DispelSystem.class).dispel(DispellableComponent.Type.VERTICAL);
                } else if(angle < 135 + wiggleRoom && angle > 135 - wiggleRoom || angle > -45 - wiggleRoom && angle < -45 + wiggleRoom){
                    world.getSystem(DispelSystem.class).dispel(DispellableComponent.Type.FRONT_SLASH);
                } else if(angle < 45 + wiggleRoom && angle > 45 - wiggleRoom || angle > -135 - wiggleRoom && angle < -135 + wiggleRoom){
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
                        new UpdatePositionSystem(),
                        new TileSystem(Measure.units(10f), Measure.units(5f), Measure.units(80f), Measure.units(50f), 5, 10),
                        new MoveToTargetSystem()
                )
                .with(WorldConfigurationBuilder.Priority.HIGH,
                        new ExplosionSystem(),
                        new BulletSystem(),
                        new DispelSystem(),
                        new TurnSystem(),
                        new HealthSystem(),
                        new FindPlayerSystem(),
                        new BlinkOnHitSystem(),
                        new DeathSystem(),
                        new ExpireSystem()
                )
                .with(WorldConfigurationBuilder.Priority.LOWEST,
                        new RenderingSystem(game, gameport),
                        new BoundsDrawingSystem(batch)
                )

                .build();

        world = new World(config);


        ComponentBag player = new PlayerFactory(assetManager).player(Measure.units(10f), Measure.units(10f));
        BagToEntity.bagToEntity(world.createEntity(), player);

        world.getSystem(FindPlayerSystem.class).setPlayerBag(player);

        ComponentBag bag = new DummyFactory(assetManager).targetDummyLeft(Measure.units(10f), Measure.units(50f));
        Entity e = BagToEntity.bagToEntity(world.createEntity(), bag);
       // world.getSystem(TileSystem.class).placeUsingCoordinates(new Coordinates(-2, 1), e.getComponent(PositionComponent.class), e.getComponent(BoundComponent.class));


        bag = new DummyFactory(assetManager).targetDummyVert(Measure.units(40f), Measure.units(50f));
        e = BagToEntity.bagToEntity(world.createEntity(), bag);
       // world.getSystem(TileSystem.class).placeUsingCoordinates(new Coordinates(-2, 2), e.getComponent(PositionComponent.class), e.getComponent(BoundComponent.class));


        bag = new DummyFactory(assetManager).targetDummyBackSlash(Measure.units(55f), Measure.units(50f));
        BagToEntity.bagToEntity(world.createEntity(), bag);
/*

        BagToEntity.bagToEntity(world.createEntity(), new DummyFactory(assetManager).targetDummyFrontSlash(Measure.units(25f), Measure.units(50f)));
        BagToEntity.bagToEntity(world.createEntity(), new DummyFactory(assetManager).targetDummyFrontSlash(Measure.units(25f), Measure.units(50f)));
        BagToEntity.bagToEntity(world.createEntity(), new DummyFactory(assetManager).targetDummyFrontSlash(Measure.units(25f), Measure.units(50f)));
*/

        BagToEntity.bagToEntity(world.createEntity(), new FloorFactory(assetManager).createFloor(Measure.units(10f), Measure.units(5f), Measure.units(80f), Measure.units(50f),
                5, 10));

        world.getSystem(TileSystem.class).findShortestPath(new Coordinates(1,3), new Coordinates(6,3));

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
