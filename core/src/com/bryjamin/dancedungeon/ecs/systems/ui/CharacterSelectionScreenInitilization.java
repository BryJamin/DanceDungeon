package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.BaseSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.assets.Padding;
import com.bryjamin.dancedungeon.assets.Skins;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.factories.CharacterGenerator;
import com.bryjamin.dancedungeon.factories.map.MapGenerator;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
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

    private StageUIRenderingSystem stageUIRenderingSystem;
    private RenderingSystem renderingSystem;

    private Skin uiSkin;
    private Table container;
    private Table characterPane;
    private Table partyTable;
    private TextButton startExpedition;


    private Viewport gameport;
    private MainGame game;

    private Array<UnitData> availableMembers;

    private Array<Array<UnitData>> heroSquads = new Array<>();

    private Array<UnitData> partyMembers = new Array<UnitData>(PARTY_SIZE);

    public CharacterSelectionScreenInitilization(MainGame game, Viewport gameport, Array<UnitData> availableMembers) {
        this.gameport = gameport;
        this.game = game;
        this.availableMembers = availableMembers;

        for(int i = 0; i < PARTY_SIZE; i++){
            this.partyMembers.add(null);
        }
        this.uiSkin = Skins.DEFAULT_SKIN(game.assetManager);

        CharacterGenerator cg = new CharacterGenerator();

        heroSquads.add(new Array<>(new UnitData[]{cg.createWarrior(), cg.createArcher(), cg.createMage()}));
        heroSquads.add(new Array<>(new UnitData[]{cg.createWarrior(), cg.createArcher(), cg.createMage()}));
        heroSquads.add(new Array<>(new UnitData[]{cg.createWarrior(), cg.createArcher(), cg.createMage()}));

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
        createAvailablePartyFrame();
        createCurrentPartyFrame();

        //This is because initalize is recalled and kind of screws up a bit.
        //Better option is to have an intialize and 'redraw' method. When refreshinging tables
        if(characterPane == null) {
            characterPane = new Table(uiSkin);
        }

    }


    /**
     * Creates the Current Patty frame used for expeditions
     */
    private void createCurrentPartyFrame(){


        if(partyTable == null){
            partyTable = new Table(uiSkin);
        } else {
            partyTable.remove();
            partyTable.clear();
        }

        stageUIRenderingSystem.stage.addActor(partyTable);
        partyTable.setWidth(stageUIRenderingSystem.stage.getWidth());
        partyTable.setHeight(Measure.units(12.5f));
        partyTable.setDebug(StageUIRenderingSystem.DEBUG);


        float size = Measure.units(7.5f);

        startExpedition = new TextButton("Start Expedition", uiSkin);


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

        partyTable.add(startExpedition).width(BOTTOM_BUTTON_WIDTH).height(Measure.units(7.5f)).expandX().padLeft(Measure.units(10f))
                .padRight(Measure.units(2.5f));


        TextButton whatever = new TextButton("Change Heroes", uiSkin);


        partyTable.add(whatever).width(BOTTOM_BUTTON_WIDTH).height(Measure.units(7.5f)).expandX().padRight(Measure.units(10f));

    }


    private void createAvailablePartyFrame(){

        Stage stage = stageUIRenderingSystem.stage;


        container = new Table();
        stage.addActor(container);
        container.setDebug(StageUIRenderingSystem.DEBUG);
        //container.setBackground(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(TextureStrings.BLOCK)).tint(new Color(0,0,0,0.6f)));
        container.setWidth(stage.getWidth());
        container.setHeight(stage.getHeight());
        container.align(Align.top);

        float padTop = Measure.units(3.5f);

        CharacterGenerator cg = new CharacterGenerator();

        //TODO Create rows of Defender, Attacker, Support

        Label selectYourParty = new Label("Your Heroes", uiSkin);
        container.add(selectYourParty).expandX().padTop(Padding.SMALL);
        container.row();

        for(int i = 0; i < partyMembers.size; i++){

            Table partyMemberContainer = new Table(uiSkin);
            partyMemberContainer.setBackground(new NinePatchDrawable(new NinePatch(renderingSystem.getAtlas().findRegion(TextureStrings.BORDER), 4, 4, 4, 4)).tint(new Color(1,1,1, 0.5f)));


            partyMemberContainer.align(Align.center);
            container.add(partyMemberContainer).width(container.getWidth() - Measure.units(2.5f)).padBottom(Padding.SMALL);

            UnitData unitData = partyMembers.get(i);

            //Table characterPortraitContainer = new Table(uiSkin);
            //partyMemberContainer.add(characterPortraitContainer).expandX().padRight(Padding.MEDIUM);

            partyMemberContainer.add(new Label(unitData.name, uiSkin)).width(Measure.units(15f)).align(Align.left).colspan(4).padLeft(Padding.SMALL);
            partyMemberContainer.row();


            Image border = new Image(new NinePatch(renderingSystem.getAtlas().findRegion(TextureStrings.BORDER), 4, 4, 4, 4));
            Table imgContainer = new Table();
            imgContainer.add(new Image(renderingSystem.getAtlas().findRegion(unitData.icon))).size(Measure.units(7.5f));
            //characterPortraitContainer.row();
            partyMemberContainer.add(imgContainer).size(Measure.units(7.5f)).padRight(Padding.MEDIUM).padBottom(Padding.SMALL).padLeft(Padding.SMALL);


            //Table skillsTableContainer = new Table(uiSkin);
            //partyMemberContainer.add(skillsTableContainer).width(Measure.units(80f));

            //skillsTableContainer.row();

            imgContainer = new Table();
            imgContainer.setBackground(new NinePatchDrawable(new NinePatch(renderingSystem.getAtlas().findRegion(TextureStrings.BORDER), 4, 4, 4, 4)));
            imgContainer.add(new Image(renderingSystem.getAtlas().findRegion(unitData.getSkillsComponent().skills.first().getIcon()))).size(Measure.units(4f));

            partyMemberContainer.add(imgContainer).width(Measure.units(5f));
            partyMemberContainer.add(new Label(unitData.getSkillsComponent().skills.first().getName(), uiSkin)).expandX();

            Label description = new Label(unitData.getSkillsComponent().skills.first().getDescription(), uiSkin, Fonts.LABEL_STYLE_SMALL_FONT);
            description.setWrap(true);
            description.setAlignment(Align.center);
            partyMemberContainer.add(description).width(Measure.units(50f));



            container.row();

        }

    }



}
