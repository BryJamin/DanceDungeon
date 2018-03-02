package com.bryjamin.dancedungeon.screens.strategy;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.ecs.systems.ExpireSystem;
import com.bryjamin.dancedungeon.ecs.systems.FixedToCameraPanAndFlingSystem;
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
import com.bryjamin.dancedungeon.ecs.systems.input.MapInputSystem;
import com.bryjamin.dancedungeon.ecs.systems.strategy.EventGenerationSystem;
import com.bryjamin.dancedungeon.ecs.systems.strategy.StrategyMapSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.MapStageInitialisationSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.StageUIRenderingSystem;
import com.bryjamin.dancedungeon.factories.map.GameMap;
import com.bryjamin.dancedungeon.factories.map.MapGenerator;
import com.bryjamin.dancedungeon.screens.AbstractScreen;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.utils.GameDelta;
import com.bryjamin.dancedungeon.utils.Measure;

/**
 * Created by BB on 17/12/2017.
 */

public class MapScreen extends AbstractScreen {

    private World world;
    private GameMap gameMap;
    private PartyDetails partyDetails;

    public MapScreen(MainGame game, PartyDetails partyDetails) {
        super(game);
        this.partyDetails = partyDetails;
        createWorld();
    }


    private void createWorld() {

        gameMap = new MapGenerator().generateGameMap();

        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(WorldConfigurationBuilder.Priority.HIGHEST,

                        //Initialization Systems

                        new EventGenerationSystem(),
                        new StrategyMapSystem(game, gameMap, partyDetails),

                        new MapInputSystem(game, gameport, 0, gameMap.getWidth() + Measure.units(20f)),
                        new FixedToCameraPanAndFlingSystem(gameport.getCamera(), 0, 0, gameMap.getWidth() + Measure.units(20f), 0),
                        new MapStageInitialisationSystem(game, partyDetails, gameport), //Updates and is fixed to camera, so need to be below fling system


                        //Positional Systems
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
                        new ActionOnTapSystem(),
                        new FadeSystem(),
                        new ScaleTransformationSystem(),
                        new RenderingSystem(game, gameport),
                        new StageUIRenderingSystem(new Stage(gameport, game.batch)),
                        new BoundsDrawingSystem(batch))
                .build();

        world = new World(config);

    }




    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        GameDelta.delta(world, delta);
    }

    public void battleVictory(){
        world.getSystem(StrategyMapSystem.class).onVictory();
        world.getSystem(MapStageInitialisationSystem.class).updateInformation();
    }

}
