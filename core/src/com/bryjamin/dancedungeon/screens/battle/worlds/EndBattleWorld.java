package com.bryjamin.dancedungeon.screens.battle.worlds;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.FileStrings;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.assets.TextResource;
import com.bryjamin.dancedungeon.assets.TextureStrings;
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
import com.bryjamin.dancedungeon.screens.menu.MenuScreen;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextDescription;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;


/**
 * Created by BB on 28/11/2017.
 */

public class EndBattleWorld extends WorldContainer {

    private VictoryAdapter victoryAdapter;


    public enum State {
        VICTORY, DEFEAT
    }

    private State state;


    public EndBattleWorld(MainGame game, Viewport gameport, State state) {
        super(game, gameport);
        this.state = state;
        createWorld();
        victoryAdapter = new VictoryAdapter();
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

        Entity exitButton = world.createEntity();
        exitButton.edit().add(new PositionComponent(CenterMath.offsetX(gameport.getWorldWidth(), width), Measure.units(20f)));
        exitButton.edit().add(new HitBoxComponent(new HitBox(width, height)));
        exitButton.edit().add(new DrawableComponent(Layer.ENEMY_LAYER_MIDDLE,
                new TextureDescription.Builder(TextureStrings.BLOCK)
                        .width(width)
                        .height(height).build(),
                new TextDescription.Builder(FileStrings.DEFAULT_FONT_NAME)
                        .width(width)
                        .height(height)
                        .text(TextResource.BATTLE_OVER_CONTINUE)
                        .color(new Color(Color.BLACK))
                        .build()));
        exitButton.edit().add(new ActionOnTapComponent(new WorldAction() {
            @Override
            public void performAction(World world, Entity entity) {
                game.getScreen().dispose();
                game.setScreen(new MenuScreen(game));
            }
        }));



        Entity blackScreen = world.createEntity();
        blackScreen.edit().add(new PositionComponent(CenterMath.centerPositionX(gameport.getCamera().viewportWidth, gameport.getCamera().position.x),
                CenterMath.centerPositionY(gameport.getCamera().viewportHeight, gameport.getCamera().position.y)));
        blackScreen.edit().add(new HitBoxComponent(new HitBox(gameport.getCamera().viewportWidth, gameport.getCamera().viewportHeight)));
        blackScreen.edit().add(new DrawableComponent(Layer.BACKGROUND_LAYER_FAR,
                new TextureDescription.Builder(TextureStrings.BLOCK)
                        .color(state == State.VICTORY ? new Color(0,1,0, 0.6f) : new Color(1,0,0,0.6f))
                        .width(gameport.getWorldWidth())
                        .height(gameport.getWorldHeight()).build()));

        Entity victoryText = world.createEntity();
        victoryText.edit().add(new PositionComponent(CenterMath.centerPositionX(gameport.getCamera().viewportWidth, gameport.getCamera().position.x),
                CenterMath.centerPositionY(Measure.units(10f), gameport.getCamera().position.y) + Measure.units(10f)));
        victoryText.edit().add(new DrawableComponent(Layer.BACKGROUND_LAYER_MIDDLE,
                new TextureDescription.Builder(TextureStrings.BLOCK)
                        .color(new Color(Color.WHITE))
                        .width(gameport.getWorldWidth())
                        .height(Measure.units(10f)).build(),

                new TextDescription.Builder(Fonts.MEDIUM)
                        .text(state == State.VICTORY ? TextResource.BATTLE_OVER_VICTORY : TextResource.BATTLE_OVER_DEFEAT)
                        .color(new Color(Color.BLACK))
                        .width(gameport.getWorldWidth())
                        .height(Measure.units(10f)).build()
                ));

    }


    @Override
    public void handleInput(InputMultiplexer inputMultiplexer) {
        inputMultiplexer.addProcessor(victoryAdapter);
    }



    private class VictoryAdapter extends InputAdapter {

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
