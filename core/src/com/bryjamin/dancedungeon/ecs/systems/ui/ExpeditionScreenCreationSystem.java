package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.Skins;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ActionOnTapComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PartyUiComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.factories.ButtonFactory;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.screens.strategy.MapScreen;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.math.CenteringFrame;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 10/02/2018.
 */

public class ExpeditionScreenCreationSystem extends BaseSystem {

    private static int PARTY_SIZE = 4;

    private StageUIRenderingSystem stageUIRenderingSystem;
    private RenderingSystem renderingSystem;
    private Skin uiSkin;
    private Table container;
    private Table characterPane;


    private Viewport gameport;
    private MainGame game;

    private Array<UnitData> availableMembers;
    private Array<UnitData> partyMembers = new Array<UnitData>(4);

    public ExpeditionScreenCreationSystem(MainGame game, Viewport gameport, Array<UnitData> availableMembers, Array<UnitData> partyMembers) {
        this.gameport = gameport;
        this.game = game;
        this.availableMembers = availableMembers;

        for(int i = 0; i < PARTY_SIZE; i++){
            this.partyMembers.add(null);
        }
        this.uiSkin = Skins.DEFAULT_SKIN(game.assetManager);
    }

    private void createWorldMap() {

        float size = gameport.getWorldHeight() * 1.75f;


        world.createEntity().edit().add(new PositionComponent(CenterMath.centerOnPositionX(size, MainGame.GAME_WIDTH / 2) - Measure.units(10f),
                CenterMath.centerOnPositionY(size, MainGame.GAME_HEIGHT / 2)))
                .add(new DrawableComponent(Layer.BACKGROUND_LAYER_MIDDLE,
                        new TextureDescription.Builder(TextureStrings.WORLD_MAP)
                                .height(size)
                                .width(size)
                                .build()));
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
        } else {
           // characterPane.remove();
           // characterPane.clear();
        }
        float btnW = Measure.units(25f);
        float btnHeight = Measure.units(7.5f);
        CenteringFrame centeringFrame = new CenteringFrame(Measure.units(0), Measure.units(0), Measure.units(30f), Measure.units(12.5f));
        centeringFrame.setWidthPer(btnW);
        centeringFrame.setHeightPer(btnHeight);
        Vector2 position = centeringFrame.calculatePosition(0);

        new ButtonFactory.ButtonBuilder()
                .pos(position.x, position.y)
                .width(btnW)
                .height(btnHeight)
                .text("Start Expedition")
                .buttonAction(new WorldAction() {
                    @Override
                    public void performAction(World world, Entity entity) {
                        game.getScreen().dispose();

                        PartyDetails partyDetails = new PartyDetails();

                        for (int i = 0; i < PARTY_SIZE; i++) {
                            try {
                                partyDetails.addPartyMember(partyMembers.get(i), i);
                            } catch (IndexOutOfBoundsException e) {
                                partyDetails.addPartyMember(null, i);
                            }
                        }

                        game.setScreen(new MapScreen(game, partyDetails));
                    }
                })
                .build(world);


    }


    private void createCurrentPartyFrame(){

        Rectangle frame = new Rectangle(Measure.units(0), Measure.units(0f), gameport.getWorldWidth(), Measure.units(12.5f));

        CenteringFrame centeringFrame = new CenteringFrame(frame);
        centeringFrame.setWidthPer(Measure.units(7.5f));
        centeringFrame.setHeightPer(Measure.units(7.5f));
        centeringFrame.setColumns(4);
        centeringFrame.setRows(1);
        centeringFrame.setxGap(Measure.units(2.5f));

        Entity backdrop = world.createEntity();
        backdrop.edit().add(new PositionComponent(frame.x, frame.y))
                .add(new PartyUiComponent())
                .add(new DrawableComponent(Layer.BACKGROUND_LAYER_NEAR,
                        new TextureDescription.Builder(TextureStrings.BLOCK)
                                .width(frame.width)
                                .height(frame.height)
                                .color(new Color(0,0,0,0.8f))
                                .build()));

        for (int i = 0; i < PARTY_SIZE; i++) {

            Vector2 position = centeringFrame.calculatePosition(i);

            if (partyMembers.get(i) != null) {
                Entity e = createPartyIcon(position.x, position.y, partyMembers.get(i));
                e.edit().add(new ActionOnTapComponent(new WorldAction() {
                    @Override
                    public void performAction(World world, Entity entity) {

                        int i = partyMembers.indexOf(entity.getComponent(UnitComponent.class).getUnitData(), false);

                        if (i != -1) {
                            partyMembers.set(i, null);
                            updateUi();
                        }

                    }
                }));
            } else {
                float width = Measure.units(7.5f);

                Entity e = world.createEntity().edit()
                        .add(new PositionComponent(position.x, position.y))
                        .add(new PartyUiComponent())
                        .add(new HitBoxComponent(width, width))
                        .add(new DrawableComponent(Layer.ENEMY_LAYER_MIDDLE,
                                new TextureDescription.Builder(TextureStrings.BLOCK)
                                        .width(width)
                                        .height(width)
                                        .color(new Color(1,1,1, 0.7f))
                                        .build())).getEntity();
            }

        }

    }


    private void createAvailablePartyFrame(){

        Stage stage = stageUIRenderingSystem.stage;


        container = new Table();
        container.setDebug(true);
        container.setWidth(stage.getWidth());
        container.setHeight(stage.getHeight());
        container.align(Align.right);

        Table partyMemberContainer = new Table();
        partyMemberContainer.setDebug(true);

        ScrollPane scrollPane = new ScrollPane(partyMemberContainer, uiSkin);
        container.add(scrollPane).width(Measure.units(30f)).height(stage.getHeight());


        stage.addActor(container);

        //Right hand column
        for (int i = 0; i < availableMembers.size; i++) {


            TextureRegionDrawable drawable = new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(availableMembers.get(i).icon));

            final UnitData unitData = availableMembers.get(i);

            Button btn = new Button(drawable);

            if (partyMembers.contains(availableMembers.get(i), true)){
                btn = new Button(drawable.tint(new Color(0.1f, 0.1f, 0.1f, 0.7f)));

            } else {
                btn.addListener(new ClickListener(){

                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        addToParty(unitData);
                    }
                });
            }

            partyMemberContainer.add(btn).width(Measure.units(7.5f)).height(Measure.units(7.5f));
            partyMemberContainer.add(new Label(unitData.name, uiSkin));

            drawable = new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(TextureStrings.BLOCK));
            Button characterInfo = new Button(uiSkin);
            characterInfo.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    createCharacterInformationWindow(unitData);
                }
            });
            partyMemberContainer.add(characterInfo).prefSize(Measure.units(5f), Measure.units(5f));

            partyMemberContainer.row();
        }



    }


    private void addToParty(UnitData unitData) {

        if (partyMembers.size > PARTY_SIZE) return;

        for (int i = 0; i < PARTY_SIZE; i++) {
            try {
                if (partyMembers.get(i) == null) {//Gaps in the party are set to null
                    partyMembers.set(i, unitData);
                    updateUi();
                    return;
                }
            } catch (IndexOutOfBoundsException e) {
                partyMembers.insert(i, unitData);
                updateUi();
                return;
            }
        }

    }

    private void createCharacterInformationWindow(UnitData unitData){

        Stage stage = stageUIRenderingSystem.stage;

        characterPane.remove();
        characterPane.clear();
        characterPane.setBackground(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(TextureStrings.BLOCK)).tint(new Color(0,0,0,0.8f)));
        characterPane.setSkin(uiSkin);
        characterPane.setX(Measure.units(0f));
        characterPane.setY(Measure.units(10f));
        characterPane.setWidth(stage.getWidth() - Measure.units(30f));
        characterPane.setHeight(stage.getHeight() - Measure.units(10f));
        characterPane.align(Align.center);

        stage.addActor(characterPane);

        Table characterInfo = new Table(uiSkin);
        characterPane.add(characterInfo).width(Measure.units(10f));

        Image image = new Image(renderingSystem.getAtlas().findRegion(unitData.icon));
        image.setFillParent(true);

        characterInfo.add(new Image(renderingSystem.getAtlas().findRegion(unitData.icon))).width(Measure.units(10f)).height(Measure.units(10f))
                .padRight(Measure.units(5f));


        Table middleInformation = new Table(uiSkin);
        middleInformation.setFillParent(true);
        middleInformation.align(Align.left);

        middleInformation.add(new Label(unitData.name, uiSkin)).height(Measure.units(5f));
        middleInformation.row();
        int hp = unitData.getStatComponent().maxHealth;
        middleInformation.add(new Label("Health: " + hp +  "/" +  hp, uiSkin)).height(Measure.units(5f));

        characterInfo.add(middleInformation);


        Button closeInfo = new Button(uiSkin);
        characterInfo.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                characterPane.remove();
                characterPane.clear();
            }
        });

        characterInfo.add(closeInfo).width(Measure.units(5f)).height(Measure.units(5f)).padLeft(Measure.units(2.5f));

    }


    private Entity createPartyIcon(float x, float y, UnitData unitData) {

        float width = Measure.units(7.5f);

        Entity e = world.createEntity().edit()
                .add(new PositionComponent(x, y))
                .add(new UnitComponent(unitData))
                .add(new PartyUiComponent())
                .add(new HitBoxComponent(width, width))
                .add(new DrawableComponent(Layer.ENEMY_LAYER_MIDDLE,
                        new TextureDescription.Builder(unitData.icon)
                                .width(width)
                                .height(width)
                                .build())).getEntity();

        return e;
    }


    private void updateUi() {

        IntBag unitEntities = world.getAspectSubscriptionManager().get(Aspect.all(PartyUiComponent.class)).getEntities();

        for (int i = 0; i < unitEntities.size(); i++)
            world.delete(unitEntities.get(i));

        container.remove();
        initialize();

    }


    @Override
    protected void processSystem() {

    }
}
