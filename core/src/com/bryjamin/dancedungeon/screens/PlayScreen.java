package com.bryjamin.dancedungeon.screens;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.ecs.DirectionalInputAdapter;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.DispellableComponent;
import com.bryjamin.dancedungeon.ecs.systems.ExpireSystem;
import com.bryjamin.dancedungeon.ecs.systems.FindPlayerSystem;
import com.bryjamin.dancedungeon.ecs.systems.MoveToTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.MovementSystem;
import com.bryjamin.dancedungeon.ecs.systems.ParentChildSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.ActionOnTapSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.ConditionalActionSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BlinkOnHitSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BulletSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.DeathSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.DispelSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.ExplosionSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.HealthSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.SelectedTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.BoundsDrawingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.FadeSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.HealthBarSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.PlayerGraphicalTargetingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.UIRenderingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.UpdatePositionSystem;
import com.bryjamin.dancedungeon.factories.decor.FloorFactory;
import com.bryjamin.dancedungeon.factories.enemy.DummyFactory;
import com.bryjamin.dancedungeon.factories.enemy.RangedDummyFactory;
import com.bryjamin.dancedungeon.factories.player.PlayerFactory;
import com.bryjamin.dancedungeon.factories.player.spells.SpellFactory;
import com.bryjamin.dancedungeon.utils.GameDelta;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.bag.ComponentBag;
import com.bryjamin.dancedungeon.utils.math.AngleMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;


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

                //    if(world.getSystem(PlayerGraphicalTargetingSystem.class).createTarget(input.x, input.y)) return true;

                    if(world.getSystem(SelectedTargetSystem.class).selectCharacter(input.x, input.y)) return true;

                    if(world.getSystem(ActionOnTapSystem.class).touch(input.x, input.y)){
                        return  true;
                    };
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


        float originX = Measure.units(10f);
        float originY = Measure.units(10f);
        float width = Measure.units(80f);
        float height = Measure.units(45f);

        int rows = 5;
        int columns = 10;


        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(WorldConfigurationBuilder.Priority.HIGHEST,
                        new MovementSystem(),
                        new UpdatePositionSystem(),
                        new TileSystem(originX, originY, width, height, rows, columns),
                        new MoveToTargetSystem()
                )
                .with(WorldConfigurationBuilder.Priority.HIGH,
                        new ConditionalActionSystem(),
                        new ExplosionSystem(),
                        new BulletSystem(),
                        new DispelSystem(),
                        new TurnSystem(),
                        new HealthSystem(),
                        new FindPlayerSystem(),
                        new ParentChildSystem(),
                        new BlinkOnHitSystem(),
                        new DeathSystem(),
                        new ExpireSystem()
                )
                .with(WorldConfigurationBuilder.Priority.LOWEST,
                        new ActionOnTapSystem(gameport),
                        new FadeSystem(),
                        new PlayerGraphicalTargetingSystem(),
                        new RenderingSystem(game, gameport),
                        new HealthBarSystem(game, gameport),
                        new UIRenderingSystem(game, gameport),
                        new BoundsDrawingSystem(batch),
                        
                        new SelectedTargetSystem()
                )

                .build();

        world = new World(config);


        ComponentBag player = new PlayerFactory(assetManager).player(Measure.units(10f), Measure.units(10f), new Coordinates(5,3));
        BagToEntity.bagToEntity(world.createEntity(), player);

        world.getSystem(FindPlayerSystem.class).setPlayerBag(player);


        ComponentBag playerzzzzzz = new PlayerFactory(assetManager).player2(Measure.units(10f), Measure.units(10f), new Coordinates(1,3));
        BagToEntity.bagToEntity(world.createEntity(), playerzzzzzz);


        ComponentBag bag = new DummyFactory(assetManager).targetDummySprinter(Measure.units(10f), Measure.units(50f));
        Entity e = BagToEntity.bagToEntity(world.createEntity(), bag);
        e.getComponent(CoordinateComponent.class).coordinates.setX(MathUtils.random(0, 7));
        e.getComponent(CoordinateComponent.class).coordinates.setY(MathUtils.random(0, 5));


        ComponentBag bag2 = new DummyFactory(assetManager).targetDummyWalker(Measure.units(10f), Measure.units(50f));
        Entity e2 = BagToEntity.bagToEntity(world.createEntity(), bag2);
        e2.getComponent(CoordinateComponent.class).coordinates.setX(MathUtils.random(0, 7));
        e2.getComponent(CoordinateComponent.class).coordinates.setY(MathUtils.random(0, 5));

        ComponentBag bag3 = new DummyFactory(assetManager).targetDummyWalker(Measure.units(10f), Measure.units(50f));
        Entity e3 = BagToEntity.bagToEntity(world.createEntity(), bag3);
        e3.getComponent(CoordinateComponent.class).coordinates.setX(MathUtils.random(0, 7));
        e3.getComponent(CoordinateComponent.class).coordinates.setY(MathUtils.random(0, 5));


        ComponentBag bag4 = new RangedDummyFactory(assetManager).rangedDummy(Measure.units(10f), Measure.units(50f));
        Entity e4 = BagToEntity.bagToEntity(world.createEntity(), bag4);
        e4.getComponent(CoordinateComponent.class).coordinates.setX(MathUtils.random(0, 7));
        e4.getComponent(CoordinateComponent.class).coordinates.setY(MathUtils.random(0, 5));

        BagToEntity.bagToEntity(world.createEntity(), new FloorFactory(assetManager).createFloor(originX, originY, width, height,
                rows, columns));


        BagToEntity.bagToEntity(world.createEntity(), new SpellFactory().endTurnButton(0, 0));

        BagToEntity.bagToEntity(world.createEntity(), new SpellFactory().defaultButton(Measure.units(0), Measure.units(50f), new WorldAction() {
            @Override
            public void performAction(World world, Entity entity) {
                game.setScreen(new PlayScreen(game));
            }
        }));


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
