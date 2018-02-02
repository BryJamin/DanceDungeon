package com.bryjamin.dancedungeon.screens.menu.worlds;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.TextResource;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
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
import com.bryjamin.dancedungeon.factories.ButtonFactory;
import com.bryjamin.dancedungeon.screens.WorldContainer;
import com.bryjamin.dancedungeon.screens.strategy.MapScreen;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CenterMath;

/**
 * Created by BB on 26/11/2017.
 */

public class MenuWorld extends WorldContainer {

    private MenuAdapter adapter;


    public MenuWorld(MainGame game, Viewport gameport) {
        super(game, gameport);
        createWorld();
        adapter = new MenuAdapter();
    }


    private void createWorld() {

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
                        new ActionOnTapSystem(),
                        new FadeSystem(),
                        new RenderingSystem(game, gameport),
                        new BoundsDrawingSystem(batch))
                .build();

        world = new World(config);

        float width = Measure.units(15f);
        float height = Measure.units(7.5f);


        new ButtonFactory.ButtonBuilder()
                .text(TextResource.GAME_TITLE_START)
                .pos(CenterMath.offsetX(gameport.getWorldWidth(), width), CenterMath.offsetY(gameport.getWorldHeight(), height) - Measure.units(5f))
                .width(width)
                .height(height)
                .buttonAction(new WorldAction() {
                    @Override
                    public void performAction(World world, Entity entity) {
                        game.getScreen().dispose();
                        game.setScreen(new MapScreen(game));
                    }
                })
                .build(world);


        new ButtonFactory.ButtonBuilder()
                .text(TextResource.GAME_TITLE)
                .pos(CenterMath.centerPositionX(gameport.getCamera().viewportWidth, gameport.getCamera().position.x),
                        CenterMath.centerPositionY(Measure.units(10f), gameport.getCamera().position.y) + Measure.units(10f))
                .width(gameport.getWorldWidth())
                .height(Measure.units(10f))
                .build(world);


    }


    @Override
    public void handleInput(InputMultiplexer inputMultiplexer) {
        inputMultiplexer.addProcessor(adapter);
    }


    private class MenuAdapter extends InputAdapter {

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            Vector3 input = gameport.unproject(new Vector3(screenX, screenY, 0));
            if (world.getSystem(ActionOnTapSystem.class).touch(input.x, input.y)) {
                return true;
            }
            ;
            return false;
        }
    }

}
