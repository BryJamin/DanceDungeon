package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.TextResource;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.factories.ButtonFactory;
import com.bryjamin.dancedungeon.screens.menu.MenuScreen;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 02/02/2018.
 */

public class DefeatScreenCreationSystem extends BaseSystem {

    private Screen nextScreen;
    private Viewport gameport;
    private MainGame game;

    public DefeatScreenCreationSystem(MainGame game, Viewport gameport){
        this.nextScreen = nextScreen;
        this.gameport = gameport;
        this.game = game;
    }


    @Override
    protected void processSystem() {

    }


    @Override
    protected void initialize() {
        float width = Measure.units(15f);
        float height = Measure.units(7.5f);

        new ButtonFactory.ButtonBuilder()
                .text(TextResource.BATTLE_OVER_CONTINUE)
                .pos(CenterMath.offsetX(gameport.getWorldWidth(), width), Measure.units(20f))
                .width(width)
                .height(height)
                .buttonAction(new WorldAction() {
                    @Override
                    public void performAction(World world, Entity entity) {
                        game.getScreen().dispose();
                        game.setScreen(new MenuScreen(game));
                    }
                })
                .build(world);


        Entity blackScreen = world.createEntity();
        blackScreen.edit().add(new PositionComponent(CenterMath.centerOnPositionX(gameport.getCamera().viewportWidth, gameport.getCamera().position.x),
                CenterMath.centerOnPositionY(gameport.getCamera().viewportHeight, gameport.getCamera().position.y)));
        blackScreen.edit().add(new HitBoxComponent(new HitBox(gameport.getCamera().viewportWidth, gameport.getCamera().viewportHeight)));
        blackScreen.edit().add(new DrawableComponent(Layer.BACKGROUND_LAYER_FAR,
                new TextureDescription.Builder(TextureStrings.BLOCK)
                        .color(new Color(1, 0, 0, 0.6f))
                        .width(gameport.getWorldWidth())
                        .height(gameport.getWorldHeight()).build()));


        new ButtonFactory.ButtonBuilder()
                .text(TextResource.BATTLE_OVER_DEFEAT)
                .pos(CenterMath.centerOnPositionX(gameport.getCamera().viewportWidth, gameport.getCamera().position.x),
                        CenterMath.centerOnPositionY(Measure.units(10f), gameport.getCamera().position.y) + Measure.units(10f))
                .width(gameport.getWorldWidth())
                .height(Measure.units(10f))
                .build(world);

    }
}
