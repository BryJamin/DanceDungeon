package com.bryjamin.dancedungeon.screens.strategy.worlds;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
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
import com.bryjamin.dancedungeon.ecs.systems.graphical.FollowPositionSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.ScaleTransformationSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.UpdatePositionSystem;
import com.bryjamin.dancedungeon.ecs.systems.strategy.EventGenerationSystem;
import com.bryjamin.dancedungeon.ecs.systems.strategy.StrategyMapSystem;
import com.bryjamin.dancedungeon.factories.map.GameMap;
import com.bryjamin.dancedungeon.factories.map.MapGenerator;
import com.bryjamin.dancedungeon.factories.player.Unit;
import com.bryjamin.dancedungeon.factories.player.UnitMap;
import com.bryjamin.dancedungeon.factories.spells.FireballSkill;
import com.bryjamin.dancedungeon.factories.spells.basic.MageAttack;
import com.bryjamin.dancedungeon.factories.spells.restorative.Heal;
import com.bryjamin.dancedungeon.screens.WorldContainer;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CameraMath;

/**
 * Created by BB on 17/12/2017.
 */

public class MapWorld extends WorldContainer {


    private Array<Unit> playerParty = new Array<Unit>();

    //BattleScreen battleScreen;
    private GestureDetector mapGestureDetector;

    private GameMap gameMap;


    public MapWorld(MainGame game, Viewport gameport) {
        super(game, gameport);
        this.mapGestureDetector = new GestureDetector(20f, 0.4f, 1.1f, 1.5f, new MapGestures());
        //halfTapSquareSize=20, tapCountInterval=0.4f, longPressDuration=1.1f, maxFlingDelay=0.15f.
        Unit warrior = new Unit(UnitMap.UNIT_WARRIOR);
        warrior.setStatComponent(new StatComponent.StatBuilder()
                .movementRange(5)
                .power(5)
                .healthAndMax(15).build());


        Unit warrior2 = new Unit(UnitMap.UNIT_WARRIOR);
        warrior2.setStatComponent(new StatComponent.StatBuilder()
                .power(10)
                .movementRange(6)
                .healthAndMax(15).build());

        Unit mage = new Unit(UnitMap.UNIT_MAGE);
        mage.setStatComponent(new StatComponent.StatBuilder()
                .movementRange(4)
                .healthAndMax(20)
                .attackRange(36)
                .magic(10)
                .power(5).build());

        SkillsComponent skillsComponent = new SkillsComponent();
        skillsComponent.basicAttack = new MageAttack();
        skillsComponent.skillDescriptions.add(new FireballSkill());
        skillsComponent.skillDescriptions.add(new Heal());
        mage.setSkillsComponent(skillsComponent);

        playerParty.add(mage);
        playerParty.add(warrior);
        playerParty.add(warrior2);

        createWorld();

    }


    private void createWorld() {

        gameMap = new MapGenerator().generateGameMap();

        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(WorldConfigurationBuilder.Priority.HIGHEST,

                        //Initialization Systems
                        new EventGenerationSystem(),
                        new StrategyMapSystem(game, gameMap, playerParty),



                        //PositionalS Systems
                        new MovementSystem(),
                        new FollowPositionSystem(),
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
                        new ScaleTransformationSystem(),
                        new RenderingSystem(game, gameport),
                        new BoundsDrawingSystem(batch),
                        new CameraSystem(gameport.getCamera(), 0,0, gameMap.getWidth() + Measure.units(20f), 0))
                .build();

        world = new World(config);

    }

    @Override
    public void handleInput(InputMultiplexer inputMultiplexer) {
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

         //   if(velocityX > 1250) {
                world.getSystem(CameraSystem.class).flingCamera(-velocityX, velocityY);
           // }
            return false;
            //return super.fling(velocityX, velocityY, button);
        }
    }


}
