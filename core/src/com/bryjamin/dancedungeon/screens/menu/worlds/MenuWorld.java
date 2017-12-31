package com.bryjamin.dancedungeon.screens.menu.worlds;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.assets.TextResource;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
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
import com.bryjamin.dancedungeon.screens.WorldContainer;
import com.bryjamin.dancedungeon.screens.strategy.StrategyScreen;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextDescription;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

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


    private void createWorld(){

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
                        new RenderingSystem(game, gameport),
                        new BoundsDrawingSystem(batch))
                .build();

        world = new World(config);

        float width = Measure.units(15f);
        float height = Measure.units(7.5f);

        Entity startButton = world.createEntity();
        startButton.edit().add(new PositionComponent(CenterMath.offsetX(gameport.getWorldWidth(), width), CenterMath.offsetY(gameport.getWorldHeight(), height) - Measure.units(5f)));
        startButton.edit().add(new HitBoxComponent(new HitBox(width, height)));
        startButton.edit().add(new CenteringBoundaryComponent(new Rectangle(0,0, width, height)));
        startButton.edit().add(new DrawableComponent(Layer.ENEMY_LAYER_MIDDLE,
                new TextureDescription.Builder(TextureStrings.BLOCK)
                        .width(width)
                        .height(height).build(),
                new TextDescription.Builder(Fonts.MEDIUM)
                        .text(TextResource.GAME_TITLE_START)
                        .color(new Color(Color.BLACK))
                        .build()));
        startButton.edit().add(new ActionOnTapComponent(new WorldAction() {
            @Override
            public void performAction(World world, Entity entity) {
                game.getScreen().dispose();
                game.setScreen(new StrategyScreen(game));
            }
        }));



        Entity victoryText = world.createEntity();
        victoryText.edit().add(new PositionComponent(CenterMath.centerPositionX(gameport.getCamera().viewportWidth, gameport.getCamera().position.x),
                CenterMath.centerPositionY(Measure.units(10f), gameport.getCamera().position.y) + Measure.units(10f)));
        victoryText.edit().add(new DrawableComponent(Layer.BACKGROUND_LAYER_MIDDLE,
                new TextureDescription.Builder(TextureStrings.BLOCK)
                        .color(new Color(Color.WHITE))
                        .width(gameport.getWorldWidth())
                        .height(Measure.units(10f)).build(),

                new TextDescription.Builder(Fonts.MEDIUM)
                        .text(TextResource.GAME_TITLE)
                        .color(new Color(Color.BLACK))
                        .width(gameport.getWorldWidth())
                        .height(Measure.units(10f)).build()


        ));


    }


    @Override
    public void handleInput(InputMultiplexer inputMultiplexer) {
        inputMultiplexer.addProcessor(adapter);
    }


    private class MenuAdapter extends InputAdapter {

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            Vector3 input = gameport.unproject(new Vector3(screenX, screenY, 0));
            if(world.getSystem(ActionOnTapSystem.class).touch(input.x, input.y)){
                return  true;
            };
            return false;
        }
    }

}
