package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.BaseSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.Colors;
import com.bryjamin.dancedungeon.assets.Skins;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.systems.FixedToCameraPanAndFlingSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.ecs.systems.input.MapInputSystem;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CameraMath;

import java.util.Locale;

/**
 * Created by BB on 02/03/2018.
 */

public class MapStageUISystem extends BaseSystem {

    private StageUIRenderingSystem stageUIRenderingSystem;
    private RenderingSystem renderingSystem;
    private FixedToCameraPanAndFlingSystem camSys;
    private MainGame game;
    private PartyDetails partyDetails;
    private Viewport gameport;
    private Skin uiSkin;

    private Table container;
    private Table characterWindowContainer;
    private Table tooltipTable;

    private Array<Button> buttonArray = new Array<Button>();


    public MapStageUISystem(MainGame game, PartyDetails partyDetails, Viewport gameport) {
        this.game = game;
        this.partyDetails = partyDetails;
        this.gameport = gameport;
        this.uiSkin = Skins.DEFAULT_SKIN(game.assetManager);
    }


    @Override
    protected void initialize() {

        Stage stage = stageUIRenderingSystem.stage;


        container = new Table(uiSkin);
        container.setDebug(true);
        container.setWidth(stage.getWidth());
        container.setHeight(stage.getHeight());
        container.align(Align.top);

        characterWindowContainer = new Table();
        characterWindowContainer.setVisible(false);

        tooltipTable = new Table();
        tooltipTable.setVisible(false);

        Label label = new Label("Select Your Next Destination", uiSkin);
        container.add(label);
        container.row();

        float width = container.getWidth() / 3;

        Table infoTable = new Table(uiSkin);
        infoTable.align(Align.center);
        container.add(infoTable).height(Measure.units(5f));

        label = new Label("Money: $" + partyDetails.money, uiSkin);
        label.setAlignment(Align.center);
        infoTable.add(label).width(width).align(Align.center);

        label = new Label("Reputation: $" + partyDetails.money, uiSkin);
        label.setAlignment(Align.center);
        infoTable.add(label).width(width).align(Align.center);

        label = new Label("Something Else: $" + partyDetails.money, uiSkin);
        label.setAlignment(Align.center);
        infoTable.add(label).width(width).align(Align.center);


        stage.addActor(container);


        container.row();

        Table testbottom = new Table(uiSkin);
        testbottom.align(Align.center);
        testbottom.setWidth(stage.getWidth());
        //testbottom.setDebug(true);
        container.add(testbottom).height(Measure.units(5f)).padTop(stage.getHeight() - Measure.units(15f));
        buttonArray.clear();


        for (final UnitData unitData : partyDetails.getParty()) {

            if (unitData == null) continue;


            Stack stack = new Stack();

            TextureRegionDrawable drawable = new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(unitData.icon));

            //stack.add(new Button(uiSkin));
            Button btn = new Button(drawable, drawable.tint(new Color(0.1f, 0.1f, 0.1f, 1)));
            stack.add(btn);
            testbottom.add(stack).size(Measure.units(10f), Measure.units(10f)).padRight(Measure.units(2.5f));
            buttonArray.add(btn);


            btn.addListener(new ChangeListener() {

                @Override
                public void changed(ChangeEvent event, Actor actor) {

                    if (characterWindowContainer.isVisible()) {
                        openCharacterWindow(unitData);
                        world.getSystem(MapInputSystem.class).openMenu();
                    } else {
                        openCharacterWindow(unitData);
                        world.getSystem(MapInputSystem.class).openMenu();
                    }

                }
            });
        }
    }

    @Override
    protected void processSystem() {
        container.setPosition(CameraMath.getBtmLftX(gameport), CameraMath.getBtmY(gameport.getCamera()));

        if (characterWindowContainer.isVisible()) {
            characterWindowContainer.setPosition(CameraMath.getBtmLftX(gameport), CameraMath.getBtmY(gameport.getCamera()));
        }
    }

    //TODO messy?
    public void updateInformation() {
        container.remove();
        container.clear();
        initialize();
    }


    private void openCharacterWindow(UnitData unitData) {

        final Stage stage = stageUIRenderingSystem.stage;

        characterWindowContainer.remove();
        characterWindowContainer.clear();
        characterWindowContainer = new Table();
        characterWindowContainer.setWidth(stage.getWidth());
        characterWindowContainer.setHeight(stage.getHeight());
        characterWindowContainer.setVisible(true);
        stage.addActor(characterWindowContainer);

        final Table characterWindow = new Table(uiSkin);
        characterWindow.setDebug(true);
        characterWindow.setBackground(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(TextureStrings.BLOCK)).tint(Colors.RGBtoColor(34, 49, 63, 1)));
        characterWindow.align(Align.top);
        characterWindowContainer.add(characterWindow).size(Measure.units(60f), Measure.units(40f));

        Label label = new Label(unitData.name, uiSkin);
        characterWindow.add(label).expandX().colspan(5);

        characterWindow.row();

        Image portrait = new Image(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(unitData.icon)));
        characterWindow.add(portrait).size(Measure.units(10f), Measure.units(10f)).expandX();

        int max = unitData.getStatComponent().maxHealth;

        characterWindow.add(new Label(String.format(Locale.ENGLISH, "HP %s/%s", max, max), uiSkin)).expandX();


        container.setTouchable(Touchable.enabled);
        container.addListener(new InputListener() { //Tracks taps outside of the character Window in order to close window


            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                //Checks if the touch is outside the Character Window
                if (x < characterWindow.getX() || x > characterWindow.getX() + characterWindow.getWidth()
                        || y < characterWindow.getY() || y > characterWindow.getY() + characterWindow.getHeight()) {

                    boolean close = true;
                    for (Button btn : buttonArray) {
                        //Checks if touch is within any buttons on the screen
                        Vector2 pos = btn.localToStageCoordinates(new Vector2());
                        if (x > pos.x && x < pos.x + btn.getWidth() && y > pos.y && y < pos.y + btn.getHeight()) {
                            close = false;
                        }
                    }

                    if (close) {
                        container.setTouchable(Touchable.childrenOnly);
                        closeCharacterWindow();
                        stage.removeListener(this);
                    }
                    return true;
                }

                return false;
            }
        });


        //Skills Table Row / Upgrades
        characterWindow.row();
        Table skillsTable = new Table(uiSkin);
        characterWindow.add(skillsTable).height(Measure.units(25f)).colspan(5).expandX();
        skillsTable.setDebug(true);
        skillsTable.align(Align.left);

        Label skillsLabel = new Label("Skills", uiSkin);
        skillsTable.add(skillsLabel).padRight(Measure.units(10f));


        for (final Skill s : unitData.getSkillsComponent().skills) {


            Table upgradeTable = new Table(uiSkin);

            skillsTable.add(upgradeTable).height(Measure.units(20f)).padRight(Measure.units(2.5f));

            final Button skillButton = new Button(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(s.getIcon())));

            upgradeTable.add(skillButton)
                    .width(Measure.units(7.5f))
                    .height(Measure.units(7.5f))
                    .expandY();


            upgradeTable.row();
            upgradeTable.add(new TextButton("Upgrade", uiSkin));


            skillButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {


          /*          if(tooltipTable.isVisible()){
                        tooltipTable.clear();
                    }*/

                    tooltipTable.remove();
                    tooltipTable.clear();

                    Drawable background = new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(TextureStrings.BLOCK)).tint(new Color(Color.BLACK));

                    Vector2 pos = actor.localToStageCoordinates(new Vector2());
                    tooltipTable = new Table(uiSkin);
                    tooltipTable.setBackground(background);
                    tooltipTable.setDebug(true);
                    tooltipTable.setWidth(Measure.units(25f));
                    tooltipTable.setHeight(Measure.units(30f));
                    tooltipTable.add(new Label(s.getName(), uiSkin));
                    System.out.println(pos.x);

                    System.out.println("ax " + actor.getX());
                    System.out.println("ay " + actor.getY());

                    tooltipTable.setPosition(pos.x - tooltipTable.getWidth(), pos.y - tooltipTable.getHeight() / 2);
                    tooltipTable.row();

                    Label description = new Label(s.getDescription(world, world.createEntity()), uiSkin);
                    description.setWrap(true);
                    description.setAlignment(Align.center);
                    tooltipTable.add(description).pad(Measure.units(2.5f)).width(Measure.units(20f)).expand();

                    stage.addActor(tooltipTable);



                    stage.addListener(new InputListener() { //Tracks taps outside of the character Window in order to close window

                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                            //Checks if the touch is outside the Character Window
                            tooltipTable.remove();
                            stage.removeListener(this);

                            //Checks if touch is within any buttons on the screen
                            Vector2 pos = tooltipTable.localToStageCoordinates(new Vector2());
                            if (x < pos.x || x > pos.x + tooltipTable.getWidth() || y < pos.y || y > pos.y + tooltipTable.getHeight()) {
                                tooltipTable.remove();
                                stage.removeListener(this);
                            }

                            return false;
                        }
                    });


                }
            });

        }


    }

    private void closeCharacterWindow() {

        if (characterWindowContainer == null) return;

        characterWindowContainer.remove();
        characterWindowContainer.clear();
        characterWindowContainer.setVisible(false);

        world.getSystem(MapInputSystem.class).closedMenu();

    }


}
