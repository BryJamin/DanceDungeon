package com.bryjamin.dancedungeon.screens.menu;

import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.music.MusicFiles;
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
import com.bryjamin.dancedungeon.ecs.systems.graphical.UpdateBoundPositionsSystem;
import com.bryjamin.dancedungeon.ecs.systems.input.BasicInputSystemWithStage;
import com.bryjamin.dancedungeon.ecs.systems.ui.MenuScreenUISystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.StageUIRenderingSystem;
import com.bryjamin.dancedungeon.screens.AbstractScreen;
import com.bryjamin.dancedungeon.utils.GameDelta;

/**
 * Created by BB on 08/10/2017.
 */

public class MenuScreen extends AbstractScreen {

    private World world;

    public MenuScreen(MainGame game) {
        super(game);
        createWorld();
        game.musicSystem.changeMix(MusicFiles.BG_MAIN_MENU);
    }


    private void createWorld() {


        WorldConfiguration config = new WorldConfigurationBuilder()
                .with(WorldConfigurationBuilder.Priority.HIGHEST,

                        new MenuScreenUISystem(game, gameport),
                        new BasicInputSystemWithStage(gameport),
                        game.musicSystem,
                        new MovementSystem(),
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
                        new RenderingSystem(game, gameport),
                        new StageUIRenderingSystem( new Stage(gameport, game.batch)),
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

}
