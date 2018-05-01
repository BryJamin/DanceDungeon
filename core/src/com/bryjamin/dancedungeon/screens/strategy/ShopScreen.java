package com.bryjamin.dancedungeon.screens.strategy;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.ecs.systems.ExpireSystem;
import com.bryjamin.dancedungeon.ecs.systems.MoveToTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.MovementSystem;
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
import com.bryjamin.dancedungeon.ecs.systems.input.BasicInputSystemWithStage;
import com.bryjamin.dancedungeon.ecs.systems.ui.InformationBannerSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.ShopScreenUISystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.StageUIRenderingSystem;
import com.bryjamin.dancedungeon.screens.AbstractScreen;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.utils.GameDelta;

public class ShopScreen extends AbstractScreen {

    private World world;
    private Screen previousScreen;
    private PartyDetails partyDetails;

    public ShopScreen(MainGame game, Screen previousScreen, PartyDetails partyDetails) {
        super(game);
        this.previousScreen = previousScreen;
        this.partyDetails = partyDetails;
        createWorld();
    }


    private void createWorld() {

        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(WorldConfigurationBuilder.Priority.HIGHEST,

                        //Initialization Systems
                        new BasicInputSystemWithStage(gameport),
                        new ShopScreenUISystem(game, gameport, this),
                        new PlayerPartyManagementSystem(partyDetails),
                        new InformationBannerSystem(game, gameport),

                        //Positional Systems
                        new MovementSystem(),
                        new FollowPositionSystem(),
                        new UpdateBoundPositionsSystem(),
                        new MoveToTargetSystem()
                )
                .with(WorldConfigurationBuilder.Priority.HIGH,
                        new ConditionalActionSystem(),
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

    public Screen getPreviousScreen() {
        return previousScreen;
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        GameDelta.delta(world, delta);
    }
}
