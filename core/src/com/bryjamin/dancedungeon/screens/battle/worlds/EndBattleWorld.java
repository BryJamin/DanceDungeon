package com.bryjamin.dancedungeon.screens.battle.worlds;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.TextResource;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
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
import com.bryjamin.dancedungeon.factories.ButtonFactory;
import com.bryjamin.dancedungeon.screens.WorldContainer;
import com.bryjamin.dancedungeon.screens.battle.BattleScreen;
import com.bryjamin.dancedungeon.screens.strategy.MapScreen;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.texture.Layer;
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
                        new ActionOnTapSystem(),
                        new FadeSystem(),
                        new RenderingSystem(game, gameport),
                        new BoundsDrawingSystem(batch))
                .build();

        world = new World(config);


        float width = Measure.units(15f);
        float height = Measure.units(7.5f);

        new ButtonFactory.ButtonBuilder()
                .text(state == State.VICTORY ? TextResource.BATTLE_OVER_VICTORY : TextResource.BATTLE_OVER_DEFEAT)
                .pos(CenterMath.offsetX(gameport.getWorldWidth(), width), Measure.units(20f))
                .width(width)
                .height(height)
                .buttonAction(new WorldAction() {
                    @Override
                    public void performAction(World world, Entity entity) {
                        Screen prev = ((BattleScreen) game.getScreen()).getPreviousScreen();
                        game.getScreen().dispose();
                        game.setScreen(prev);

                        ((MapScreen) prev).battleVictory();
                    }
                })
                .build(world);


        Entity blackScreen = world.createEntity();
        blackScreen.edit().add(new PositionComponent(CenterMath.centerPositionX(gameport.getCamera().viewportWidth, gameport.getCamera().position.x),
                CenterMath.centerPositionY(gameport.getCamera().viewportHeight, gameport.getCamera().position.y)));
        blackScreen.edit().add(new HitBoxComponent(new HitBox(gameport.getCamera().viewportWidth, gameport.getCamera().viewportHeight)));
        blackScreen.edit().add(new DrawableComponent(Layer.BACKGROUND_LAYER_FAR,
                new TextureDescription.Builder(TextureStrings.BLOCK)
                        .color(state == State.VICTORY ? new Color(0,1,0, 0.6f) : new Color(1,0,0,0.6f))
                        .width(gameport.getWorldWidth())
                        .height(gameport.getWorldHeight()).build()));




        new ButtonFactory.ButtonBuilder()
                .text(state == State.VICTORY ? TextResource.BATTLE_OVER_VICTORY : TextResource.BATTLE_OVER_DEFEAT)
                .pos(CenterMath.centerPositionX(gameport.getCamera().viewportWidth, gameport.getCamera().position.x),
                        CenterMath.centerPositionY(Measure.units(10f), gameport.getCamera().position.y) + Measure.units(10f))
                .width(gameport.getWorldWidth())
                .height(Measure.units(10f))
                .build(world);

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
