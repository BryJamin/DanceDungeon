package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.FileStrings;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.assets.Padding;
import com.bryjamin.dancedungeon.assets.Skins;
import com.bryjamin.dancedungeon.assets.TextResource;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.systems.audio.MusicSystem;
import com.bryjamin.dancedungeon.ecs.systems.audio.SoundSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.factories.map.GameMap;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.screens.menu.CharacterSelectionScreen;
import com.bryjamin.dancedungeon.screens.strategy.MapScreen;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.options.DevOptions;
import com.bryjamin.dancedungeon.utils.options.PlayerSave;
import com.bryjamin.dancedungeon.utils.options.QuickSave;
import com.bryjamin.dancedungeon.utils.options.Settings;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

import javax.xml.soap.Text;


public class MenuScreenUISystem extends BaseSystem {

    private static int PARTY_SIZE = 3;

    private static final float BOTTOM_BUTTON_WIDTH = Measure.units(30f);


    private static final float BUTTON_WIDTH = Measure.units(30f);
    private static final float BUTTON_HEIGHT = Measure.units(7.5f);
    private static final float SETTINGS_BUTTON_SIZE = Measure.units(7.5f);

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

    private MenuState menuState = MenuState.MAIN;

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
        Label title = new Label(TextResource.GAME_TITLE, uiSkin, Fonts.LARGE_FONT_STYLE_NAME, new Color(Color.WHITE));
        title.setAlignment(Align.left);

        titleContainer.add(title).width(container.getWidth()).height(Measure.units(10f)).padLeft(Padding.MEDIUM).expandX();

        container.add(titleContainer);

        container.row();
        startButtonContainer = new Table(uiSkin);
        container.add(startButtonContainer).expandY();
        populateMiddleContainer();

        container.row();
        bottomContainer = new Table(uiSkin);
        bottomContainer.setWidth(stage.getWidth());
        container.add(bottomContainer).height(Measure.units(10f)).width(stage.getWidth());

        populateBottomContainer();





    }

    private void populateBottomContainer(){

        bottomContainer.clear();
        bottomContainer.align(Align.right);

        //Music Button


        if(menuState == MenuState.OPTIONS){

            TextButton back = new TextButton(TextResource.SCREEN_MENU_BACK, uiSkin);

            back.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    menuState = MenuState.MAIN;
                    populateMiddleContainer();
                    populateBottomContainer();
                }
            });

            bottomContainer.add(back).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(Padding.MEDIUM).expandX().align(Align.left).padLeft(Padding.SMALL);
        }


        final Button music = new Button(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(TextureStrings.SETTINGS_MUSIC_ON)),
                null,
                new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(TextureStrings.SETTINGS_MUSIC_OFF)
                ));

        music.setChecked(!Settings.isMusicOn());

        music.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Settings.toggleMusic();
                music.setChecked(!Settings.isMusicOn());
            }
        });

        bottomContainer.add(music).size(SETTINGS_BUTTON_SIZE).padRight(Padding.MEDIUM);

        final Button sounds = new Button(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(TextureStrings.SETTINGS_SOUND_ON)),
                null,
                new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(TextureStrings.SETTINGS_SOUND_OFF)
                ));

        sounds.setChecked(!Settings.isSoundOn());

        sounds.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Settings.toggleSound();
                SoundSystem.SOUNDON = Settings.isSoundOn();
                sounds.setChecked(!Settings.isSoundOn());
            }
        });


        bottomContainer.add(sounds).size(SETTINGS_BUTTON_SIZE).padRight(Padding.MEDIUM);;


    }



    public void populateMiddleContainer(){

        startButtonContainer.clear();

        switch (menuState){

            case MAIN:

                if(QuickSave.isThereAValidQuickSave()){
                    TextButton textBtn1 = new TextButton(TextResource.SCREEN_MENU_CONTINUE, uiSkin);
                    textBtn1.addListener(new ClickListener() {

                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            game.getScreen().dispose();
                            QuickSave.SavedData savedData = QuickSave.savedData;
                            QuickSave.clear();
                            GameMap gameMap = savedData.getGameMap();
                            gameMap.setUpLoadedMap();
                            PartyDetails partyDetails = savedData.getPartyDetails();
                            game.setScreen(new MapScreen(game, gameMap, partyDetails));
                        }
                    });

                    startButtonContainer.add(textBtn1).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(Padding.MEDIUM);
                    startButtonContainer.row();
                }


                TextButton textBtn1 = new TextButton(TextResource.SCREEN_MENU_NEW_GAME, uiSkin);
                textBtn1.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.getScreen().dispose();
                        game.setScreen(new CharacterSelectionScreen(game));
                    }
                });

                TextButton textBtn2 = new TextButton(TextResource.SCREEN_MENU_OPTIONS, uiSkin);
                textBtn2.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        menuState = MenuState.OPTIONS;
                        populateMiddleContainer();
                        populateBottomContainer();
                    }
                });

                startButtonContainer.add(textBtn1).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(Padding.MEDIUM);
                startButtonContainer.row();
                startButtonContainer.add(textBtn2).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);

                break;


            case OPTIONS:


                Table optionsTable = new Table();
                ScrollPane optionsPane = new ScrollPane(optionsTable);
                startButtonContainer.add(optionsPane).fill();

                String text = DevOptions.getUtilityScoreSetting() ?
                        TextResource.SCREEN_MENU_SHOW_MOVEMENT_SCORE_ON : TextResource.SCREEN_MENU_SHOW_MOVEMENT_SCORE_OFF;

                TextButton toggleScore = new TextButton(text, uiSkin);
                toggleScore.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        DevOptions.toggleUtilityInfo();
                        populateMiddleContainer();
                    }
                });

                TextButton toggleTutorial = new TextButton(TextResource.SCREEN_MENU_TUTORIAL_ON, uiSkin);
                toggleTutorial.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        PlayerSave.turnOnFirstTimePlayer();
                        populateMiddleContainer();
                    }
                });



                addButtonToTable(optionsTable, toggleScore);
                addButtonToTable(optionsTable, toggleTutorial);

                break;


        }

    }

    private void addButtonToTable(Table table, Button button){

        switch (menuState){
            case OPTIONS:
                table.add(button).width(BUTTON_WIDTH + Measure.units(27.5f)).height(BUTTON_HEIGHT).padBottom(Padding.MEDIUM);
                break;
            case MAIN:
                table.add(button).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).padBottom(Padding.MEDIUM);
        }

        table.row();
    }

}


