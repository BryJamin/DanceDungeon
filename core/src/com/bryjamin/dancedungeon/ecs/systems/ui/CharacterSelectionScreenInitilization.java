package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.BaseSystem;
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
import com.bryjamin.dancedungeon.assets.TextResource;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.factories.enemy.UnitLibrary;
import com.bryjamin.dancedungeon.factories.map.MapGenerator;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.screens.menu.MenuScreen;
import com.bryjamin.dancedungeon.screens.strategy.MapScreen;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 10/02/2018.
 */

public class CharacterSelectionScreenInitilization extends BaseSystem {

    private static int PARTY_SIZE = 3;

    private static final float BOTTOM_BUTTON_WIDTH = Measure.units(30f);
    private static final float BOTTOM_BUTTON_HEIGHT = Measure.units(7.5f);

    private StageUIRenderingSystem stageUIRenderingSystem;
    private RenderingSystem renderingSystem;

    private Skin uiSkin;
    private Table container;
    private Table characterTable;
    private Table bottomContainer;
    private TextButton startExpedition;


    private Viewport gameport;
    private MainGame game;

    private Array<UnitData> availableMembers;

    private Array<Array<UnitData>> heroSquads = new Array<>();

    private Array<UnitData> partyMembers = new Array<UnitData>(PARTY_SIZE);

    public CharacterSelectionScreenInitilization(MainGame game, Viewport gameport) {
        this.gameport = gameport;
        this.game = game;
        this.availableMembers = availableMembers;

        for(int i = 0; i < PARTY_SIZE; i++){
            this.partyMembers.add(null);
        }
        this.uiSkin = Skins.DEFAULT_SKIN(game.assetManager);


        heroSquads.add(new Array<>(new UnitData[]{
                UnitLibrary.getUnitData(UnitLibrary.CHARACTERS_SGT_SWORD),
                UnitLibrary.getUnitData(UnitLibrary.CHARACTERS_BOLAS),
                UnitLibrary.getUnitData(UnitLibrary.CHARACTERS_FIRAS)}));


        heroSquads.add(new Array<>(new UnitData[]{
                UnitLibrary.getUnitData(UnitLibrary.MELEE_BLOB),
                UnitLibrary.getUnitData(UnitLibrary.MELEE_BLOB),
                UnitLibrary.getUnitData(UnitLibrary.MELEE_BLOB)}));


        heroSquads.add(new Array<>(new UnitData[]{
                UnitLibrary.getUnitData(UnitLibrary.MELEE_BLOB),
                UnitLibrary.getUnitData(UnitLibrary.MELEE_BLOB),
                UnitLibrary.getUnitData(UnitLibrary.MELEE_BLOB)}));

        partyMembers = heroSquads.get(0);

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

    }


    /**
     * Populates Bottom Container With Buttons
     */
    private void populateBottomContainer(){

        bottomContainer.clear();
        bottomContainer.setDebug(StageUIRenderingSystem.DEBUG);

        startExpedition = new TextButton(TextResource.SCREEN_CHARACTER_START, uiSkin);

        startExpedition.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                game.getScreen().dispose();

                PartyDetails partyDetails = new PartyDetails();

                for (int i = 0; i < PARTY_SIZE; i++) {
                    try {
                        partyDetails.addPartyMember(partyMembers.get(i), i);
                    } catch (IndexOutOfBoundsException e) {
                        partyDetails.addPartyMember(null, i);
                    }
                }

                game.setScreen(new MapScreen(game, new MapGenerator().generateGameMap(), partyDetails));
            }
        });

        bottomContainer.add(startExpedition).width(BOTTOM_BUTTON_WIDTH).height(BOTTOM_BUTTON_HEIGHT).padRight(Padding.SMALL).expandX();


        TextButton whatever = new TextButton(TextResource.SCREEN_CHARACTER_HEROES, uiSkin);
        bottomContainer.add(whatever).width(BOTTOM_BUTTON_WIDTH).height(BOTTOM_BUTTON_HEIGHT).padRight(Padding.SMALL).expandX();



        TextButton backToMainMenu = new TextButton(TextResource.SCREEN_CHARACTER_BACK, uiSkin);

        backToMainMenu.addListener(new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getScreen().dispose();
                game.setScreen(new MenuScreen(game));
            }
        });

        bottomContainer.add(backToMainMenu).width(BOTTOM_BUTTON_WIDTH).height(BOTTOM_BUTTON_HEIGHT).padRight(Padding.SMALL).expandX();


    }


    private void populateCharacterTable(){


        characterTable.clear();

        for(int i = 0; i < partyMembers.size; i++){

            Table partyMemberContainer = new Table(uiSkin);

            NinePatchDrawable ninePatchDrawable = NinePatches.getDefaultNinePatch(renderingSystem.getAtlas());
            ninePatchDrawable.getPatch().getColor().a = 0.5f;

            partyMemberContainer.setBackground(ninePatchDrawable);


            partyMemberContainer.align(Align.center);
            characterTable.add(partyMemberContainer).width(container.getWidth() - Measure.units(2.5f)).padBottom(Padding.SMALL).expandY();

            UnitData unitData = partyMembers.get(i);

            partyMemberContainer.add(new Label(unitData.name, uiSkin)).width(Measure.units(15f)).align(Align.left).colspan(4).padLeft(Padding.SMALL);
            partyMemberContainer.row();


            Image border = new Image(NinePatches.getDefaultNinePatch(renderingSystem.getAtlas()));
            Table imgContainer = new Table();
            imgContainer.add(new Image(renderingSystem.getAtlas().findRegion(unitData.icon))).size(Measure.units(7.5f));
            //characterPortraitContainer.row();
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

    }



}
