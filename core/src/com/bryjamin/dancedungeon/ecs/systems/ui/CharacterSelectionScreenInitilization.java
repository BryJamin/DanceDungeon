package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.BaseSystem;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
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
import com.bryjamin.dancedungeon.assets.TextResource;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.factories.map.event.EventLibrary;
import com.bryjamin.dancedungeon.factories.unit.UnitLibrary;
import com.bryjamin.dancedungeon.factories.map.MapGenerator;
import com.bryjamin.dancedungeon.factories.map.event.TutorialEvent;
import com.bryjamin.dancedungeon.factories.unit.UnitData;
import com.bryjamin.dancedungeon.screens.battle.BattleScreen;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.screens.menu.MenuScreen;
import com.bryjamin.dancedungeon.screens.strategy.MapScreen;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.options.PlayerSave;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 10/02/2018.
 */

public class CharacterSelectionScreenInitilization extends BaseSystem {

    private static int PARTY_SIZE = 3;

    private static int SQUAD_CHOICE = 0;

    private static final float BOTTOM_BUTTON_WIDTH = Measure.units(30f);
    private static final float BOTTOM_BUTTON_HEIGHT = Measure.units(7.5f);

    private StageUIRenderingSystem stageUIRenderingSystem;
    private RenderingSystem renderingSystem;

    private Skin uiSkin;
    private Table container;
    private Table characterTable;
    private Table bottomContainer;
    private TextButton startExpedition;

    private Table tutorialTable;

    private Viewport gameport;
    private MainGame game;

    private Array<UnitData> availableMembers;

    private Array<HeroSquad> heroSquads = new Array<>();

    private Array<String> partyMembers = new Array<>();


    private enum State {
        START_EXPEDITION, CHANGE_HEROES
    }

    private State state = State.START_EXPEDITION;


    public CharacterSelectionScreenInitilization(MainGame game, Viewport gameport) {
        this.gameport = gameport;
        this.game = game;
        this.availableMembers = availableMembers;

        for(int i = 0; i < PARTY_SIZE; i++){
            this.partyMembers.add(null);
        }
        this.uiSkin = Skins.DEFAULT_SKIN(game.assetManager);


        heroSquads.add(new HeroSquad(TextResource.SCREEN_CHARACTER_SQUAD_NAME_1,
                UnitLibrary.CHARACTERS_SGT_SWORD,
                UnitLibrary.CHARACTERS_BOLAS,
                UnitLibrary.CHARACTERS_FIRAS
        ));


        heroSquads.add(new HeroSquad(TextResource.SCREEN_CHARACTER_SQUAD_NAME_2,
                UnitLibrary.CHARACTERS_SWITCH,
                UnitLibrary.CHARACTERS_WANDA,
                UnitLibrary.CHARACTERS_HIRAN
        ));

        heroSquads.add(new HeroSquad(TextResource.SCREEN_CHARACTER_SQUAD_NAME_3,
                UnitLibrary.MELEE_BLOB,
                UnitLibrary.RANGED_LOBBA,
                UnitLibrary.RANGED_BLASTER
        ));

        partyMembers = heroSquads.get(SQUAD_CHOICE).unitIds;

    }

    private class HeroSquad {

        private String name;
        private Array<String> unitIds = new Array<>();

        public HeroSquad(String name, String unit1, String unit2, String unit3){
            this.name = name;
            unitIds.addAll(unit1, unit2, unit3);
        }


        public String getName() {
            return name;
        }

        public Array<String> getUnitIds() {
            return unitIds;
        }
    }


    private PartyDetails createPartyDetails(){

        PartyDetails partyDetails = new PartyDetails();

        for (int i = 0; i < PARTY_SIZE; i++) {
            try {
                partyDetails.addPartyMember(UnitLibrary.getUnitData(partyMembers.get(i)), i);
            } catch (IndexOutOfBoundsException e) {
                partyDetails.addPartyMember(null, i);
            }
        }

        return partyDetails;

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

        if(partyMembers.contains(null, true)) {
            startExpedition.setDisabled(true);
        } else {
            startExpedition.setDisabled(false);
        }
    }

    @Override
    protected void initialize() {

        createWorldMap();


        Stage stage = stageUIRenderingSystem.stage;

        container = new Table();
        stage.addActor(container);
        container.setDebug(StageUIRenderingSystem.DEBUG);
        //container.setBackground(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(TextureStrings.BLOCK)).tint(new Color(0,0,0,0.6f)));
        container.setWidth(stage.getWidth());
        container.setHeight(stage.getHeight());
        container.align(Align.top);


        characterTable = new Table(uiSkin);
        container.add(characterTable).expandY();

        container.row();
        bottomContainer = new Table(uiSkin);
        container.add(bottomContainer).height(Measure.units(12.5f));

        populateCharacterTable();
        populateBottomContainer();

        tutorialTable = new Table();
        tutorialTable.setWidth(stage.getWidth());
        tutorialTable.setHeight(stage.getHeight());
        tutorialTable.setVisible(false);
        stage.addActor(tutorialTable);

    }


    /**
     * Populates Bottom Container With Buttons
     */
    private void populateBottomContainer(){

        bottomContainer.clear();
        bottomContainer.setDebug(StageUIRenderingSystem.DEBUG);


        switch (state) {

            case START_EXPEDITION:

                startExpedition = new TextButton(TextResource.SCREEN_CHARACTER_START, uiSkin);

                startExpedition.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {

                        game.getScreen().dispose();

                        if(!PlayerSave.isFirstTimePlayer()) {
                            goToMapScreen();
                        } else {
                            openTutorialTable();
                        }
                    }
                });

                bottomContainer.add(startExpedition).width(BOTTOM_BUTTON_WIDTH).height(BOTTOM_BUTTON_HEIGHT).padRight(Padding.SMALL).expandX();


                TextButton changeHeroes = new TextButton(TextResource.SCREEN_CHARACTER_HEROES, uiSkin);
                bottomContainer.add(changeHeroes).width(BOTTOM_BUTTON_WIDTH).height(BOTTOM_BUTTON_HEIGHT).padRight(Padding.SMALL).expandX();
                changeHeroes.addListener(new ClickListener(){

                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        state = State.CHANGE_HEROES;
                        populateBottomContainer();
                        populateCharacterTable();
                    }

                });



                TextButton backToMainMenu = new TextButton(TextResource.SCREEN_CHARACTER_BACK, uiSkin);

                backToMainMenu.addListener(new ClickListener(){

                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.getScreen().dispose();
                        game.setScreen(new MenuScreen(game));
                    }
                });

                bottomContainer.add(backToMainMenu).width(BOTTOM_BUTTON_WIDTH).height(BOTTOM_BUTTON_HEIGHT).padRight(Padding.SMALL).expandX();

                break;

            case CHANGE_HEROES:

                TextButton back = new TextButton(TextResource.SCREEN_CHARACTER_BACK, uiSkin);

                back.addListener(new ClickListener(){

                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        state = State.START_EXPEDITION;
                        populateCharacterTable();
                        populateBottomContainer();
                    }
                });

                bottomContainer.add(back).width(BOTTOM_BUTTON_WIDTH).height(BOTTOM_BUTTON_HEIGHT).padRight(Padding.SMALL).expandX();

                break;


        }


    }


    private void populateCharacterTable(){


        characterTable.clear();


        switch (state){

            case START_EXPEDITION:

                for(int i = 0; i < partyMembers.size; i++){

                    Table partyMemberContainer = new Table(uiSkin);

                    NinePatchDrawable ninePatchDrawable = NinePatches.getDefaultNinePatch(renderingSystem.getAtlas());
                    ninePatchDrawable.getPatch().getColor().a = 0.5f;

                    partyMemberContainer.setBackground(ninePatchDrawable);
                    partyMemberContainer.align(Align.center);
                    characterTable.add(partyMemberContainer).width(container.getWidth() - Measure.units(2.5f)).padBottom(Padding.SMALL).expandY();

                    UnitData unitData = UnitLibrary.getUnitData(partyMembers.get(i));
                    partyMemberContainer.add(new Label(unitData.name, uiSkin)).width(Measure.units(15f)).align(Align.left).colspan(4).padLeft(Padding.SMALL);
                    partyMemberContainer.row();

                    //Character Image
                    Table imgContainer = new Table();
                    imgContainer.add(new Image(renderingSystem.getAtlas().findRegion(unitData.icon))).size(Measure.units(7.5f));
                    partyMemberContainer.add(imgContainer).size(Measure.units(7.5f)).padRight(Padding.MEDIUM).padBottom(Padding.SMALL).padLeft(Padding.SMALL);

                    imgContainer = new Table();
                    imgContainer.setBackground(NinePatches.getDefaultNinePatch(renderingSystem.getAtlas()));
                    imgContainer.add(new Image(renderingSystem.getAtlas().findRegion(unitData.getSkills().first().getIcon()))).size(Measure.units(4f));

                    partyMemberContainer.add(imgContainer).width(Measure.units(5f));
                    partyMemberContainer.add(new Label(unitData.getSkills().first().getName(), uiSkin)).expandX();

                    Label description = new Label(unitData.getSkills().first().getDescription(), uiSkin, Fonts.LABEL_STYLE_SMALL_FONT);
                    description.setWrap(true);
                    description.setAlignment(Align.center);
                    partyMemberContainer.add(description).width(Measure.units(50f));

                    characterTable.row();

                }


                break;


            case CHANGE_HEROES:


                for(int i = 0; i < heroSquads.size; i++) {

                    Table partyMemberContainer = new Table(uiSkin);

                    NinePatchDrawable ninePatchDrawable = NinePatches.getDefaultNinePatch(renderingSystem.getAtlas());
                    ninePatchDrawable.getPatch().getColor().a = 0.5f;

                    partyMemberContainer.setBackground(ninePatchDrawable);
                    partyMemberContainer.align(Align.center);
                    characterTable.add(partyMemberContainer).width(container.getWidth() - Measure.units(2.5f)).padBottom(Padding.SMALL).expandY();

                    partyMemberContainer.add(new Label(heroSquads.get(i).getName(), uiSkin)).expandX().align(Align.center).colspan(4);
                    partyMemberContainer.row();


                    for(String s : heroSquads.get(i).unitIds) {

                        UnitData unitData = UnitLibrary.getUnitData(s);

                        //Character Image
                        Table imgContainer = new Table();
                        imgContainer.add(new Image(renderingSystem.getAtlas().findRegion(unitData.icon))).size(Measure.units(5f));
                        imgContainer.add(new Label(unitData.name, uiSkin,Fonts.LABEL_STYLE_SMALL_FONT)).width(Measure.units(7.5f)).padLeft(Padding.SMALL);
                        partyMemberContainer.add(imgContainer).width(Measure.units(15f)).height(Measure.units(7.5f)).padRight(Padding.MEDIUM).padBottom(Padding.SMALL).padLeft(Padding.SMALL);
                        //partyMemberContainer.add()
                    }

                    final int j = i;

                    TextButton selectButton = new TextButton(TextResource.SCREEN_CHARACTER_SELECT, uiSkin);
                    selectButton.addListener(new ClickListener(){
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            partyMembers = heroSquads.get(j).unitIds;
                            SQUAD_CHOICE = j;
                            state = State.START_EXPEDITION;
                            populateBottomContainer();
                            populateCharacterTable();
                        }
                    });

                    partyMemberContainer.add(selectButton).width(Measure.units(15f)).height(Measure.units(7.5f)).padBottom(Padding.SMALL);

                    characterTable.row();


                }


                break;


        }


    }



    private void openTutorialTable(){

        tutorialTable.setVisible(true);
        tutorialTable.clear();
        tutorialTable.setTouchable(Touchable.enabled);
        tutorialTable.addListener(new ClickListener()); //Prevents buttons being pushed outside the window

        Table innerTable = new Table();
        innerTable.align(Align.center);
        innerTable.setBackground(NinePatches.getDefaultNinePatch(renderingSystem.getAtlas()));

        float width = Measure.units(85f);

        tutorialTable.add(innerTable).width(width).height(Measure.units(50f));

        Label label = new Label(TextResource.SCREEN_CHARACTER_TUTORIALS_TITLE, uiSkin);
        innerTable.add(label).colspan(2).padTop(Padding.LARGE);
        innerTable.row();


        Label text1 = new Label(TextResource.SCREEN_CHARACTER_TUTORIALS_QUESTION_1, uiSkin);
        text1.setWrap(true);
        text1.setAlignment(Align.center);
        innerTable.add(text1).colspan(2).width(width).expandY();
        innerTable.row();


        Label text2 = new Label(TextResource.SCREEN_CHARACTER_TUTORIALS_QUESTION_2, uiSkin);
        text2.setWrap(true);
        text2.setAlignment(Align.center);
        innerTable.add(text2).colspan(2).width(width - Measure.units(10f)).expandY().align(Align.center);
        innerTable.row();



        TextButton yes = new TextButton(TextResource.SCREEN_CHARACTER_TUTORIALS_YES, uiSkin);
        yes.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new BattleScreen(game, game.getScreen(), EventLibrary.getEvent(EventLibrary.TUTORIAL_EVENT), createPartyDetails(), true));
            }
        });



        TextButton no = new TextButton(TextResource.SCREEN_CHARACTER_TUTORIALS_NO, uiSkin);
        no.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                goToMapScreen();
                PlayerSave.turnOffFirstTimePlayer();
            }
        });

        float bWidth = Measure.units(25f);
        float bHeight = Measure.units(10f);

        innerTable.add(yes).size(bWidth, bHeight).padBottom(Padding.LARGE);
        innerTable.add(no).size(bWidth, bHeight).padBottom(Padding.LARGE);

    }



    private void goToMapScreen(){
        game.setScreen(new MapScreen(game, new MapGenerator().generateGameMap(), createPartyDetails()));
    }

}
