package com.bryjamin.dancedungeon.screens.strategy;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.music.MusicFiles;
import com.bryjamin.dancedungeon.ecs.systems.ExpireSystem;
import com.bryjamin.dancedungeon.ecs.systems.MapCameraSystemFlingAndPan;
import com.bryjamin.dancedungeon.ecs.systems.MoveToTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.MovementSystem;
import com.bryjamin.dancedungeon.ecs.systems.ParentChildSystem;
import com.bryjamin.dancedungeon.ecs.systems.PlayerPartyManagementSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.ActionOnTapSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.ConditionalActionSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.DeathSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.BoundsDrawingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.FadeSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.FollowPositionSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.ScaleTransformationSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.UpdateBoundPositionsSystem;
import com.bryjamin.dancedungeon.ecs.systems.input.MapInputSystem;
import com.bryjamin.dancedungeon.ecs.systems.music.MusicSystem;
import com.bryjamin.dancedungeon.ecs.systems.strategy.MapNodeSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.InformationBannerSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.MapScreenUISystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.StageUIRenderingSystem;
import com.bryjamin.dancedungeon.factories.map.GameMap;
import com.bryjamin.dancedungeon.screens.AbstractScreen;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.utils.GameDelta;
import com.bryjamin.dancedungeon.utils.Measure;

/**
 * Created by BB on 17/12/2017.
 *
 * Screen used during the game. For navigating between battles
 *
 */

public class MapScreen extends AbstractScreen {

    private World world;
    private GameMap gameMap;
    private PartyDetails partyDetails;

    public MapScreen(MainGame game, GameMap gameMap, PartyDetails partyDetails) {
        super(game);
        this.partyDetails = partyDetails;
        this.gameMap = gameMap;
        createWorld();
    }


    private void createWorld() {

        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(WorldConfigurationBuilder.Priority.HIGHEST,

                        //Initialization Systems
                        new MapNodeSystem(game, gameMap, partyDetails),

                        new MusicSystem(game, MusicFiles.MAP_MUSIC),
                        new PlayerPartyManagementSystem(partyDetails),

                        new MapInputSystem(game, gameport, 0, gameMap.getWidth() + Measure.units(20f)),
                        new MapCameraSystemFlingAndPan(gameport.getCamera(), 0, 0, gameMap.getWidth() + Measure.units(20f), 0),
                        new InformationBannerSystem(game, gameport),
                        new MapScreenUISystem(game, gameMap, partyDetails, gameport), //Updates and is fixed to camera, so need to be below fling system


                        //Positional Systems
                        new MovementSystem(),
                        new FollowPositionSystem(),
                        new UpdateBoundPositionsSystem(),
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

        //TODO I want to place this within the world instead of in the screen

        if(world.getSystem(MapNodeSystem.class).getCurrentMapNode() != null) {
            gameport.getCamera().position.set(world.getSystem(MapNodeSystem.class).getCurrentMapNode().getPosition().x,
                    gameport.getCamera().position.y, 0);
        }

    }




    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        GameDelta.delta(world, delta);
    }

    public void battleVictory(){
        world.getSystem(MapNodeSystem.class).onVictory();
        world.getSystem(MapScreenUISystem.class).updateInformation();
        world.getSystem(InformationBannerSystem.class).updateInformation();
        gameport.getCamera().position.set(world.getSystem(MapNodeSystem.class).getCurrentMapNode().getPosition().x,
                gameport.getCamera().position.y, 0); //Center camera on the current node.
    }

}
