package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.FileStrings;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.assets.Padding;
import com.bryjamin.dancedungeon.assets.Skins;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.SkillButtonComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.UITargetingComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.ecs.systems.action.BattleWorldInputHandlerSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionCameraSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BattleMessageSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.factories.map.event.BattleEvent;
import com.bryjamin.dancedungeon.factories.map.event.objectives.AbstractObjective;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.TargetingFactory;
import com.bryjamin.dancedungeon.screens.battle.BattleScreen;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.screens.strategy.MapScreen;
import com.bryjamin.dancedungeon.utils.Measure;

/**
 * Created by BB on 23/01/2018.
 * <p>
 * Used to create the Skill UI, when an entity is selected.
 * <p>
 * It can also be called to update and remove certain parts of the skill UI.
 */

public class BattleScreenUISystem extends EntitySystem {

    private TurnSystem turnSystem;
    private ActionCameraSystem actionCameraSystem;
    private BattleWorldInputHandlerSystem battleWorldInputHandlerSystem;
    private StageUIRenderingSystem stageUIRenderingSystem;
    private RenderingSystem renderingSystem;

    private static final float SIZE = Measure.units(10f);

    private static final float PROFILE_PICTURE_SIZE = Measure.units(5.5f);

    private TileSystem tileSystem;
    private Table container = new Table();
    private Table areYouSureContainer = new Table();


    private Table tableForSkillButtons;
    private Table skillInformationTable;
    private Table characterProfileTable;

    private Table objectivesTable;

    private ButtonGroup<Button> skillButtonButtonGroup = new ButtonGroup<>();

    private static final float BOTTOM_TABLE_HEIGHT = Measure.units(13.5f) - Padding.SMALL;


    private Label title;
    private Label description;
    private ImageButton[] buttons;

    private TextButton endTurn;

    private Stage stage;


    private MainGame game;
    private TextureAtlas atlas;
    private Skin uiSkin;

    public BattleScreenUISystem(Stage stage, MainGame game) {
        super(Aspect.all(SkillButtonComponent.class, CenteringBoundaryComponent.class));
        this.game = game;
        this.stage = stage;
        this.atlas = game.assetManager.get(FileStrings.SPRITE_ATLAS_FILE, TextureAtlas.class);
        this.uiSkin = Skins.DEFAULT_SKIN(game.assetManager);
    }


    public void populateAreYouSureContainer(){

        if(areYouSureContainer.hasChildren()){
            areYouSureContainer.reset();
        }

        areYouSureContainer.setVisible(true);
        areYouSureContainer.addAction(new Action() { //Ensures, units can not be interacted with when this Container is visible
            @Override
            public boolean act(float delta) {
                if(areYouSureContainer.isVisible()){
                    battleWorldInputHandlerSystem.setState(BattleWorldInputHandlerSystem.State.ONLY_STAGE);
                } else {
                    battleWorldInputHandlerSystem.setState(BattleWorldInputHandlerSystem.State.DEPLOYMENT);
                    return true;
                }
                return false;
            }
        });

        areYouSureContainer.add(new Label("Some Units Still Have Actions Remaining", uiSkin)).colspan(2).padBottom(Padding.MEDIUM);
        areYouSureContainer.row();
        areYouSureContainer.add(new Label("Are You Sure You Want To End Your Turn?", uiSkin)).colspan(2).padBottom(Padding.MEDIUM);;
        areYouSureContainer.row();

        TextButton yes = new TextButton("Yes", uiSkin);
        yes.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                areYouSureContainer.setVisible(false);
                turnSystem.endAllyTurn();
                reset();
            }
        });



        TextButton no = new TextButton("No", uiSkin);
        no.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                areYouSureContainer.setVisible(false);
            }
        });


        areYouSureContainer.add(yes).width(Measure.units(20f)).height(Measure.units(5f)).expandX();
        areYouSureContainer.add(no).width(Measure.units(20f)).height(Measure.units(5f)).expandX();

    }


    @Override
    protected void initialize() {


        areYouSureContainer = new Table(uiSkin);
        areYouSureContainer.setDebug(StageUIRenderingSystem.DEBUG);
        areYouSureContainer.setWidth(stage.getWidth());
        areYouSureContainer.setHeight(stage.getHeight());
        areYouSureContainer.align(Align.center);
        areYouSureContainer.setVisible(false);
        areYouSureContainer.setTouchable(Touchable.enabled);
        areYouSureContainer.setBackground(new TextureRegionDrawable(atlas.findRegion(TextureStrings.BLOCK)).tint(new Color(0, 0, 0, 0.8f)));


        container = new Table(uiSkin);
        container.setDebug(StageUIRenderingSystem.DEBUG);
        container.setWidth(stage.getWidth());
        container.setHeight(BOTTOM_TABLE_HEIGHT);
        container.align(Align.bottomLeft);
        container.setTransform(false);
        container.setVisible(false);


        container.add(characterProfileTable).padBottom(Padding.SMALL);

        characterProfileTable = new Table(uiSkin);
        applyNinePathToTable(characterProfileTable);
        container.add(characterProfileTable).width(Measure.units(20f)).height(BOTTOM_TABLE_HEIGHT).padRight(Padding.MEDIUM).expandX().fillX();

        tableForSkillButtons = new Table(uiSkin);
        applyNinePathToTable(tableForSkillButtons);
        tableForSkillButtons.setDebug(StageUIRenderingSystem.DEBUG);
        container.add(tableForSkillButtons).width(Measure.units(22.5f)).height(BOTTOM_TABLE_HEIGHT).padRight(Padding.MEDIUM).expandX();

        skillInformationTable = new Table(uiSkin);
        applyNinePathToTable(skillInformationTable);
        skillInformationTable.setDebug(StageUIRenderingSystem.DEBUG);
        skillInformationTable.setVisible(false);
        container.add(skillInformationTable).width(Measure.units(45f)).height(BOTTOM_TABLE_HEIGHT).expandY();


        objectivesTable = new Table(uiSkin);
        objectivesTable.setWidth(Measure.units(30f));
        objectivesTable.setHeight(Measure.units(27.5f));
        objectivesTable.setPosition(stage.getWidth() - Measure.units(30f), Measure.units(25f));
        objectivesTable.setDebug(StageUIRenderingSystem.DEBUG);
        stage.addActor(objectivesTable);


        skillButtonButtonGroup.setMinCheckCount(0);
        skillButtonButtonGroup.setMaxCheckCount(1);
        skillButtonButtonGroup.setUncheckLast(true);

        endTurn = new TextButton("End Turn", uiSkin);


        endTurn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                if(turnSystem.isAllActionsComplete()){
                    turnSystem.endAllyTurn();
                    reset();
                } else {
                    populateAreYouSureContainer();
                }
            }
        });

        endTurn.setPosition(stage.getWidth() - Measure.units(20f), Measure.units(15f));
        endTurn.setWidth(Measure.units(20f));
        endTurn.setHeight(Measure.units(7.5f));


        stage.addActor(endTurn);

        stage.addActor(container);

        stage.addActor(areYouSureContainer);


    }


    public void applyNinePathToTable(Table table){
        NinePatch patch = new NinePatch(renderingSystem.getAtlas().findRegion(TextureStrings.BORDER), 4, 4, 4, 4);
        table.setBackground(new NinePatchDrawable(patch));
    }



    @Override
    protected void processSystem() {

        if (actionCameraSystem.isProcessing() || !turnSystem.isTurn(TurnSystem.TURN.ALLY)) {
            endTurn.setVisible(false);
        } else {
            endTurn.setVisible(true);
        }


    }


    public void updateObjectiveTable(BattleEvent battleEvent){

        if(objectivesTable.hasChildren()){
            objectivesTable.clear();
        }

        objectivesTable.add(new Label("Objectives", uiSkin)).expandX().padBottom(Padding.SMALL);
        objectivesTable.row();
        objectivesTable.add(new Label(battleEvent.getPrimaryObjective().getDescription(), uiSkin)).padBottom(Padding.SMALL);;
        objectivesTable.row();
        objectivesTable.add(new Label("Bonus", uiSkin)).padBottom(Padding.SMALL);;

        for(AbstractObjective o : battleEvent.getBonusObjective()){
            objectivesTable.row();
            objectivesTable.add(new Label(o.getDescription(), uiSkin, Fonts.SMALL_FONT_STYLE_NAME,
                    o.isFailed(world) ? new Color(Color.GRAY) : new Color(Color.WHITE)
            )).padBottom(Padding.SMALL);;
        }
    }



    /**
     * Populates the bottom of the scene with a HUD that displays the selected character's name and skills.
     * You can interact with the skills to view what they do and show where they will target on the battle map.
     * @param e
     */
    public void createCharacterSkillHUD(Entity e) {

        container.setVisible(true);

        UnitData unitData = e.getComponent(UnitComponent.class).getUnitData();

        Label name = new Label(unitData.name, uiSkin);

        characterProfileTable.clear();
        characterProfileTable.add(name).height(Measure.units(5f)).center().expandX();
        characterProfileTable.row();
        characterProfileTable.add(new Image(new TextureRegionDrawable(atlas.findRegion(unitData.icon)))).size(PROFILE_PICTURE_SIZE).expandY();


        tableForSkillButtons.clear();
        tableForSkillButtons.setTouchable(Touchable.enabled);
        tableForSkillButtons.addListener(new ClickListener(){}); //Empty click listener to avoid menu closing, if a player misses tapping a skill
        tableForSkillButtons.add(new Label("Skills", uiSkin)).height(Measure.units(5f)).center().expandX().colspan(3);
        tableForSkillButtons.row();

        skillButtonButtonGroup.clear();
        SkillsComponent skillsComponent = e.getComponent(SkillsComponent.class);
        for (int i = 0; i < skillsComponent.skills.size; i++) {
            createSkillButton(e, skillsComponent.skills.get(i));
        }


        createMovementTiles(e);
        //skillButtonButtonGroup.uncheckAll();
    }

    private void updateSkillText(Entity player, Skill skill) {

        skillInformationTable.setVisible(true);
        skillInformationTable.clear();

        Label skillName = new Label(skill.getName(), uiSkin);
        skillInformationTable.align(Align.top);
        skillInformationTable.add(skillName).height(Measure.units(5f)).center().expandX().colspan(3);;
        skillInformationTable.row();

        description = new Label(skill.getDescription(world, player), uiSkin, Fonts.LABEL_STYLE_SMALL_FONT);
        description.setWrap(true);
        description.setText(skill.getDescription(world, player));
        description.setAlignment(Align.center);

        skillInformationTable.add(description).width(skillInformationTable.getWidth()).expandY();

    }


    private void createSkillTarget(Entity unit, Skill skill) {
        this.clearTargetingTiles(); //TODO maybe just move to a new layer?
        Array<Entity> entityArray = skill.createTargeting(world, unit);

        if (entityArray.size <= 0) {
            world.getSystem(BattleMessageSystem.class).createWarningMessage();
        }

    }

    /**
     * Upon being selected creates UsesMoveAction and Attacking tiles for the player, based on
     * the avaliablity of an entites attack and movement actions
     *
     * @param e
     */
    private void createMovementTiles(Entity e) {
        TurnComponent turnComponent = e.getComponent(TurnComponent.class);
        if (turnComponent.movementActionAvailable) {
            new TargetingFactory().createMovementTiles(world, e, e.getComponent(StatComponent.class).movementRange);
        }
    }


    /**
     * Creates a 'Skill' button within the table for skill buttons
     *
     * This buttons when tapped will show which areas of the map is can be casted on, as well as
     * let the 'description' table become visible and detail what the skill does.
     *
     * @param player - The entity
     * @param skill - The skill in use.
     */
    private void createSkillButton(final Entity player, final Skill skill) {

        float size = Measure.units(6.25f);

        Stack stack = new Stack();

        Drawable drawable = new TextureRegionDrawable(atlas.findRegion(skill.getIcon()));
        final Button btn = new Button(uiSkin, "inventory");

        btn.addListener(new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y) {

                if(btn.isChecked()) {
                    updateSkillText(player,
                            skill);

                    createSkillTarget(player,
                            skill);
                } else {
                    skillInformationTable.clear();
                    skillInformationTable.setVisible(false);
                    clearTargetingTiles();
                    createMovementTiles(player);
                }
         //       btn.setChecked(true);
            }
        });

        Table table = new Table();
        Image image = new Image(drawable);
        image.setTouchable(Touchable.disabled);
        table.add(image).size(size - Measure.units(1f));


        stack.add(btn);
        skillButtonButtonGroup.add(btn);
        stack.add(table);
        tableForSkillButtons.add(stack).width(size).height(size).pad(Measure.units(1.5f)).center().expandX().expandY();

    }


    public void createVictoryRewards(BattleEvent battleEvent, PartyDetails partyDetails){
        Stage stage = stageUIRenderingSystem.stage;

        //TODO maybe clear and disable other parts of the system? Handling it here is quite hidden
        endTurn.setDisabled(true); //Disabled button functionality

        Table container = stageUIRenderingSystem.createContainerTable();
        container.setBackground(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(TextureStrings.BLOCK)).tint(new Color(0,0,0,0.6f)));

        stage.addActor(container);
        Table victoryContainer = new Table(uiSkin);
        victoryContainer.align(Align.top);

        container.add(victoryContainer).width(Measure.units(40f)).height(Measure.units(40f));


        //TITLE
        Label victory = new Label("Victory", uiSkin);
        victoryContainer.add(victory).height(Measure.units(5f)).padTop(Padding.SMALL);
        victoryContainer.row();




        //PRIMARY OBJECTIVE
        Table rewardTable = new Table(uiSkin);
        victoryContainer.add(rewardTable).height(Measure.units(5f));
        populateRewardTable(rewardTable, battleEvent.getPrimaryObjective(), partyDetails);
        victoryContainer.row();

        //SECONDARY OBEJECTIVE
        Label bonus = new Label("Bonus", uiSkin);
        victoryContainer.add(bonus);

        for(AbstractObjective bonusObjective : battleEvent.getBonusObjective()) {
            victoryContainer.row();

            Table bonusObjectiveTable = new Table(uiSkin);
            populateRewardTable(bonusObjectiveTable, bonusObjective, partyDetails);
            victoryContainer.add(bonusObjectiveTable);
        }


        victoryContainer.row();

        //victoryContainer.align(Align.bottom);

        TextButton textButton = new TextButton("Continue", uiSkin);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Screen menu = ((BattleScreen) game.getScreen()).getPreviousScreen();
                game.getScreen().dispose();
                game.setScreen(menu);
                ((MapScreen) menu).battleVictory();
            }
        });

        victoryContainer.add(textButton).height(Measure.units(5f)).bottom().expandX().expandY().fillX();
    }


    private void populateRewardTable(Table rewardTable, AbstractObjective abstractObjective, PartyDetails partyDetails){

        rewardTable.align(Align.top);

        AbstractObjective.Reward reward = abstractObjective.getReward();

        switch (reward){
            case MONEY:

                Label goldLabel = new Label("Gold", uiSkin);
                rewardTable.add(goldLabel);

                Label goldIncrease = new Label(" +" + reward.getValue(), uiSkin);
                rewardTable.add(goldIncrease);

                partyDetails.money += reward.getValue();
                break;
            case MORALE:

                Label gold = new Label("Morale", uiSkin);
                rewardTable.add(gold);

                Label reputationIncrease = new Label(" +" + reward.getValue(), uiSkin);
                rewardTable.add(reputationIncrease);

                partyDetails.morale += reward.getValue();
                break;
            case SKILL_POINT:

                Label skill_point = new Label("Skill Point", uiSkin);
                rewardTable.add(skill_point);

                Label skillPointIncrease = new Label(" +" + reward.getValue(), uiSkin);
                rewardTable.add(skillPointIncrease);

                partyDetails.skillPoints += reward.getValue();
        }

        rewardTable.row();

    }



    /**
     * Clears the button entities and selected entity from the system
     */
    public void clearTargetingTiles() {
        IntBag bag = world.getAspectSubscriptionManager().get(Aspect.all(UITargetingComponent.class)).getEntities();
        for (int i = 0; i < bag.size(); i++) {
            world.getEntity(bag.get(i)).deleteFromWorld();
        }
    }

    public void reset() {
        this.clearTargetingTiles();
        container.setVisible(false);
        skillInformationTable.setVisible(false);
        skillInformationTable.clear();
    }

}
