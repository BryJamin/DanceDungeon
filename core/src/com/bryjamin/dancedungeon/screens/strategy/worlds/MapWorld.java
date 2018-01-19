package com.bryjamin.dancedungeon.screens.strategy.worlds;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.systems.CameraSystem;
import com.bryjamin.dancedungeon.ecs.systems.ExpireSystem;
import com.bryjamin.dancedungeon.ecs.systems.MoveToTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.MovementSystem;
import com.bryjamin.dancedungeon.ecs.systems.ParentChildSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.ActionOnTapSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.ConditionalActionSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.DeathSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.BoundsDrawingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.FadeSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.UpdatePositionSystem;
import com.bryjamin.dancedungeon.ecs.systems.strategy.StrategyMapSystem;
import com.bryjamin.dancedungeon.factories.enemy.EnemyFactory;
import com.bryjamin.dancedungeon.factories.map.GameMap;
import com.bryjamin.dancedungeon.factories.map.MapGenerator;
import com.bryjamin.dancedungeon.factories.player.Unit;
import com.bryjamin.dancedungeon.factories.player.UnitMap;
import com.bryjamin.dancedungeon.factories.spells.FireballSkill;
import com.bryjamin.dancedungeon.factories.spells.basic.MageAttack;
import com.bryjamin.dancedungeon.factories.spells.restorative.Heal;
import com.bryjamin.dancedungeon.screens.WorldContainer;
import com.bryjamin.dancedungeon.screens.battle.BattleScreen;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.screens.strategy.MapScreen;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.math.CameraMath;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextDescription;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 17/12/2017.
 */

public class MapWorld extends WorldContainer {


    private Array<Unit> playerParty = new Array<Unit>();

    private Array<Array<String>> enemyParty = new Array<Array<String>>();

    //BattleScreen battleScreen;
    private ActionOnTapAdapter adapter;
    private GestureDetector mapGestureDetector;

    private GameMap gameMap;


    public MapWorld(MainGame game, Viewport gameport) {
        super(game, gameport);
        this.adapter = new ActionOnTapAdapter();
        this.mapGestureDetector = new GestureDetector(20f, 0.4f, 1.1f, 1.5f, new MapGestures());
        //halfTapSquareSize=20, tapCountInterval=0.4f, longPressDuration=1.1f, maxFlingDelay=0.15f.
        Unit warrior = new Unit(UnitMap.UNIT_WARRIOR);
        warrior.setStatComponent(new StatComponent.StatBuilder()
                .movementRange(5)
                .power(5)
                .maxHealth(15).build());


        Unit warrior2 = new Unit(UnitMap.UNIT_WARRIOR);
        warrior2.setStatComponent(new StatComponent.StatBuilder()
                .power(5)
                .movementRange(5)
                .maxHealth(15).build());

        Unit mage = new Unit(UnitMap.UNIT_MAGE);
        mage.setStatComponent(new StatComponent.StatBuilder()
                .movementRange(4)
                .maxHealth(20)
                .attackRange(3)
                .magic(6)
                .power(5).build());

        SkillsComponent skillsComponent = new SkillsComponent();
        skillsComponent.basicAttack = new MageAttack();
        skillsComponent.skillDescriptions.add(new FireballSkill());
        skillsComponent.skillDescriptions.add(new Heal());
        //skillsComponent.skillDescriptions.add(new FireballSkill());
        mage.setSkillsComponent(skillsComponent);

        playerParty.add(mage);
        playerParty.add(warrior);
        playerParty.add(warrior2);

        Array<String> fight1 = new Array<String>();
        fight1.add(EnemyFactory.BLOB);
        fight1.add(EnemyFactory.MAGE_BLOB);


        Array<String> fight2 = new Array<String>();
        fight2.add(EnemyFactory.BLOB);
        fight2.add(EnemyFactory.FAST_BLOB);
        fight2.add(EnemyFactory.BLOB);
        fight2.add(EnemyFactory.MAGE_BLOB);

        Array<String> fight3 = new Array<String>();
        fight3.add(EnemyFactory.FAST_BLOB);
        fight3.add(EnemyFactory.MAGE_BLOB);
        fight3.add(EnemyFactory.BLOB);
        fight3.add(EnemyFactory.MAGE_BLOB);

        enemyParty.add(fight1);
        enemyParty.add(fight2);
        enemyParty.add(fight3);

        createWorld();


    }


    private void createWorld() {

        gameMap = new MapGenerator().generateGameMap();

        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(WorldConfigurationBuilder.Priority.HIGHEST,
                        new MovementSystem(),
                        new UpdatePositionSystem(),
                        new MoveToTargetSystem()
                )
                .with(WorldConfigurationBuilder.Priority.HIGH,
                        new ConditionalActionSystem(),
                        new ParentChildSystem(),
                        new ExpireSystem(),
                        new DeathSystem()
                )
                .with(WorldConfigurationBuilder.Priority.LOWEST,
                        new ActionOnTapSystem(gameport),
                        new FadeSystem(),

                        new StrategyMapSystem(game, gameMap, playerParty),

                        new CameraSystem(gameport.getCamera(), 0,0, gameMap.getWidth() + Measure.units(20f), 0),

                        new RenderingSystem(game, gameport),
                        new BoundsDrawingSystem(batch))
                .build();

        world = new World(config);

        float width = Measure.units(15f);
        float height = Measure.units(7.5f);


        Entity generate = world.createEntity();
        generate.edit().add(new PositionComponent(Measure.units(75f), Measure.units(50f)));
        generate.edit().add(new HitBoxComponent(new HitBox(width, height)));
        generate.edit().add(new CenteringBoundaryComponent(new Rectangle(0, 0, width, height)));
        generate.edit().add(new DrawableComponent(Layer.ENEMY_LAYER_MIDDLE,
                new TextureDescription.Builder(TextureStrings.BLOCK)
                        .width(width)
                        .height(height).build(),
                new TextDescription.Builder(Fonts.MEDIUM)
                        .text("GENERATE")
                        .color(new Color(Color.BLACK))
                        .build()));
        generate.edit().add(new ActionOnTapComponent(new WorldAction() {
            @Override
            public void performAction(World world, Entity entity) {
                game.getScreen().dispose();
                game.setScreen(new MapScreen(game));
            }
        }));


        createParty();


    }


    public void startBattle(GameMap gameMap){
        PartyDetails partyDetails = new PartyDetails();
        partyDetails.setPlayerParty(playerParty);
        game.setScreen(new BattleScreen(game, game.getScreen(), gameMap, partyDetails));
    }



    public void createParty() {

        UnitMap unitMap = new UnitMap();

        float startX = Measure.units(10f);
        float gap = Measure.units(7.5f);
        float y = Measure.units(5f);

        for (int i = 0; i < playerParty.size; i++) {

            Entity e = BagToEntity.bagToEntity(world.createEntity(), unitMap.getUnit(playerParty.get(i)));
            // e.getComponent(PositionComponent.class).setX(CenterMath.multipleCenteringGetPosX(0, gameport.getWorldWidth(), Measure.units(5f), gap));
            e.getComponent(PositionComponent.class).setY(y);


        }
    }


    @Override
    public void handleInput(InputMultiplexer inputMultiplexer) {
        inputMultiplexer.addProcessor(adapter);
        inputMultiplexer.addProcessor(mapGestureDetector);
    }

    public void victory() {
        world.getSystem(StrategyMapSystem.class).onVictory();
    }


    private class MapGestures extends GestureDetector.GestureAdapter  {


        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {
            world.getSystem(CameraSystem.class).stopFling();
            return false;
        }

        @Override
        public boolean tap(float x, float y, int count, int button) {
            Vector3 input = gameport.unproject(new Vector3(x, y, 0));
            if (world.getSystem(ActionOnTapSystem.class).touch(input.x, input.y)) {
                return true;
            }
            ;
            return false;
        }



        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY) {
            //float x = Gdx.input.getDeltaX();
            //float y = Gdx.input.getDeltaY();

            gameport.getCamera().translate(-deltaX * Measure.units(0.15f), 0, 0);

            float tempValueToSeeFullMap = Measure.units(20f);

            if(CameraMath.getBtmLftX(gameport) < 0){
                gameport.getCamera().position.x = 0 + gameport.getCamera().viewportWidth / 2;
            } else if(CameraMath.getBtmRightX(gameport) > gameMap.getWidth() + tempValueToSeeFullMap){
                CameraMath.setBtmRightX(gameport, gameMap.getWidth() + tempValueToSeeFullMap);
            }


            return true;
        }


        @Override
        public boolean fling(float velocityX, float velocityY, int button) {

            System.out.println("Fling vel X is: " + velocityX);

         //   if(velocityX > 1250) {
                world.getSystem(CameraSystem.class).flingCamera(-velocityX, velocityY);
           // }
            return false;
            //return super.fling(velocityX, velocityY, button);
        }
    }


    private class ActionOnTapAdapter extends InputAdapter {

/*
        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
                float x = Gdx.input.getDeltaX();
                //float y = Gdx.input.getDeltaY();
                gameport.getCamera().translate(-x * Measure.units(0.15f), 0, 0);

                float tempValueToSeeFullMap = Measure.units(20f);

                if(CameraMath.getBtmLftX(gameport) < 0){
                    gameport.getCamera().position.x = 0 + gameport.getCamera().viewportWidth / 2;
                } else if(CameraMath.getBtmRightX(gameport) > gameMap.getWidth() + tempValueToSeeFullMap){
                    CameraMath.setBtmRightX(gameport, gameMap.getWidth() + tempValueToSeeFullMap);
                }


                return true;
        }*/




    }


}
