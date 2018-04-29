package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.assets.NinePatches;
import com.bryjamin.dancedungeon.assets.Padding;
import com.bryjamin.dancedungeon.assets.Skins;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.factories.enemy.UnitLibrary;
import com.bryjamin.dancedungeon.factories.map.GameMap;
import com.bryjamin.dancedungeon.factories.map.MapGenerator;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.screens.menu.CharacterSelectionScreen;
import com.bryjamin.dancedungeon.screens.strategy.MapScreen;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.save.QuickSave;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;


public class MenuScreenUISystem extends BaseSystem {

    private static int PARTY_SIZE = 3;

    private static final float BOTTOM_BUTTON_WIDTH = Measure.units(30f);

    private StageUIRenderingSystem stageUIRenderingSystem;
    private RenderingSystem renderingSystem;

    private Skin uiSkin;

    private Table container;
    private Table titleContainer;
    private Table startButtonContainer;
    private Table bottomContainer;


    private enum MenuState {
        OPTIONS, MAIN
    }



    private Viewport gameport;

    private MainGame game;


    public MenuScreenUISystem(MainGame game, Viewport gameport) {
        this.gameport = gameport;
        this.game = game;
        this.uiSkin = Skins.DEFAULT_SKIN(game.assetManager);
    }

    private void createWorldMap() {

        float size = gameport.getWorldHeight() * 2f;


        world.createEntity().edit().add(new PositionComponent(CenterMath.centerOnPositionX(size, MainGame.GAME_WIDTH / 2),
                CenterMath.centerOnPositionY(size, MainGame.GAME_HEIGHT / 2)))
                .add(new DrawableComponent(Layer.BACKGROUND_LAYER_MIDDLE,
                        new TextureDescription.Builder(TextureStrings.WORLD_MAP)
                                .height(size)
                                .width(size)
                                .build()));
    }


    @Override
    protected void processSystem() {
    }

    @Override
    protected void initialize() {
        createWorldMap();

        Stage stage = stageUIRenderingSystem.stage;

        container = new Table();
        container.setDebug(StageUIRenderingSystem.DEBUG);
        container.setWidth(stage.getWidth());
        container.setHeight(stage.getHeight());

        stage.addActor(container);

        container.align(Align.top);

        titleContainer = new Table(uiSkin);
        titleContainer.setDebug(true);

        Label title = new Label("ISLE", uiSkin, Fonts.LARGE_FONT_STYLE_NAME, new Color(Color.WHITE));
        title.setAlignment(Align.left);

        titleContainer.add(title).width(container.getWidth()).height(Measure.units(10f)).padLeft(Padding.MEDIUM).expandX();

        container.add(titleContainer);

        container.row();
        startButtonContainer = new Table(uiSkin);
        container.add(startButtonContainer).expandY();
        populateMiddleContainer();

        container.row();
        bottomContainer = new Table(uiSkin);
        bottomContainer.setDebug(true);
        container.add(bottomContainer).height(Measure.units(10f));





    }



    public void populateMiddleContainer(){

        startButtonContainer.clear();

        if(QuickSave.isThereAValidQuickSave()){
            TextButton textBtn1 = new TextButton("Continue", uiSkin);
            textBtn1.addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.getScreen().dispose();
                    QuickSave.SavedData savedData = QuickSave.savedData;
                    GameMap gameMap = savedData.getGameMap();
                    gameMap.setUpLoadedMap();
                    PartyDetails partyDetails = savedData.getPartyDetails();
                    game.setScreen(new MapScreen(game, gameMap, partyDetails));
                }
            });

            startButtonContainer.add(textBtn1).width(Measure.units(20f)).padBottom(Padding.MEDIUM);
            startButtonContainer.row();
        }


        TextButton textBtn1 = new TextButton("New Game", uiSkin);
        textBtn1.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getScreen().dispose();
                game.setScreen(new CharacterSelectionScreen(game));
            }
        });

        TextButton textBtn2 = new TextButton("Quit Game", uiSkin);
        textBtn2.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
                System.exit(0);
            }
        });

        startButtonContainer.add(textBtn1).width(Measure.units(20f)).padBottom(Padding.MEDIUM);
        startButtonContainer.row();
        startButtonContainer.add(textBtn2).width(Measure.units(20f));



    }

}


