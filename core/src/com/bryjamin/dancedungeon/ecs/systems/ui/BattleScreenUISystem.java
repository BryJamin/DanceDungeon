package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.Colors;
import com.bryjamin.dancedungeon.assets.FileStrings;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.assets.NinePatches;
import com.bryjamin.dancedungeon.assets.Padding;
import com.bryjamin.dancedungeon.assets.Skins;
import com.bryjamin.dancedungeon.assets.Styles;
import com.bryjamin.dancedungeon.assets.TextResource;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundComponent;
import com.bryjamin.dancedungeon.ecs.components.FollowPositionComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.ecs.components.battle.AvailableActionsCompnent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.StoredSkillComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.GrowAndShrinkTransformationComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UITargetingComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.ecs.systems.PlayerPartyManagementSystem;
import com.bryjamin.dancedungeon.ecs.systems.action.BattleScreenInputSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionQueueSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BattleDeploymentSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.UtilityAiSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.factories.map.event.BattleEvent;
import com.bryjamin.dancedungeon.factories.map.event.objectives.AbstractObjective;
import com.bryjamin.dancedungeon.factories.unit.UnitData;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.TargetingFactory;
import com.bryjamin.dancedungeon.screens.battle.BattleScreen;
import com.bryjamin.dancedungeon.screens.battle.PartyDetails;
import com.bryjamin.dancedungeon.screens.menu.CharacterSelectionScreen;
import com.bryjamin.dancedungeon.screens.menu.MenuScreen;
import com.bryjamin.dancedungeon.screens.strategy.MapScreen;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.AngleMath;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.observer.Observer;
import com.bryjamin.dancedungeon.utils.options.PlayerSave;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;
import com.bryjamin.dancedungeon.utils.ui.AreYouSureFrame;

import java.util.Locale;


/**
 * Created by BB on 23/01/2018.
 * <p>
 * Controls all Aspects Of UI on the BattleScreen.
 * <p>
 * It can also be called to update and remove certain parts of the skill UI.
 */

public class BattleScreenUISystem extends BaseSystem implements Observer {

    private static final int REP_REWARD = 100;
    private static final int REP_BONUS_REWARD = 200;


    private TurnSystem turnSystem;
    private ActionQueueSystem actionQueueSystem;
    private BattleDeploymentSystem battleDeploymentSystem;
    private BattleScreenInputSystem battleScreenInputSystem;
    private StageUIRenderingSystem stageUIRenderingSystem;
    private PlayerPartyManagementSystem playerPartyManagementSystem;
    private RenderingSystem renderingSystem;
    private TileSystem tileSystem;


    private ComponentMapper<PlayerControlledComponent> pcm;
    private ComponentMapper<EnemyComponent> em;
    private ComponentMapper<HealthComponent> hm;
    private ComponentMapper<StoredSkillComponent> storedm;

    private static final float SKILL_BUTTON_SIZE = Measure.units(7.5f);
    private static final float PROFILE_PICTURE_SIZE = Measure.units(5.5f);


    private static final float RECTICLE_SCALE = 1.5f;

    private enum State {
        BATTLE, DEPLOYING_UNITS
    }

    private State state = State.DEPLOYING_UNITS;


    private enum BottomTableState {
        PLAYER_SELECT, ENEMY_SELECT, ALLY_SELECT
    }

    private BottomTableState botTabState;

    //Deployment State
    private final Table deploymentTable = new Table();

    private final Table bottomContainer = new Table();
    private AreYouSureFrame areYouSureContainer;

    private Table tutorialInformationWindow;
    private Table tutorialArrowTable;



    private Entity actionQueueEntity;

    //Battle State
    private Table tableForSkillButtons;
    private Table skillDescriptionTable;
    private Table characterProfileTable;
    private Table objectivesTable;
    private Table rightSideContainer;

    private Table moraleTable;

    private Table nextTurnBanner;


    private Table attackIndicatorTable;

    private ButtonGroup<Button> skillButtonButtonGroup = new ButtonGroup<>();

    private static final float BOTTOM_TABLE_HEIGHT = Measure.units(13.5f) - Padding.SMALL;

    private Label description;

    private TextButton endTurn;
    private Stage stage;

    private MainGame game;
    private TextureAtlas atlas;
    private Skin uiSkin;

    public BattleScreenUISystem(Stage stage, MainGame game) {
        this.game = game;
        this.stage = stage;
        this.atlas = game.assetManager.get(FileStrings.SPRITE_ATLAS_FILE, TextureAtlas.class);
        this.uiSkin = Skins.DEFAULT_SKIN(game.assetManager);
    }



    @Override
    public void update(Object o) {

        if(o instanceof BattleDeploymentSystem){
            populateDeploymentTable();
            if(!((BattleDeploymentSystem) o).isProcessing()){
                state = State.BATTLE;
                populateBottomContainer();
            }
        } else if(o instanceof PlayerPartyManagementSystem){
            populateMoraleTable();
        } else if(o instanceof TurnSystem){
            createNextTurnBanner((turnSystem.getTurn()));
        }

    }


    @Override
    protected void initialize() {

        actionQueueEntity = world.createEntity();

        battleDeploymentSystem.getObservers().addObserver(this);
        playerPartyManagementSystem.addObserver(this);
        turnSystem.addEnemyTurnObserver(this);
        turnSystem.addPlayerTurnObserver(this);

        areYouSureContainer = new AreYouSureFrame(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        areYouSureContainer.setVisible(false);
                        turnSystem.endAllyTurn();
                        resetBottomContainer();
                    }
        }, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                areYouSureContainer.setVisible(false);
            }
        }, uiSkin, TextResource.BATTLE_ACTIONS_REMAINING_TEXT_1,
                TextResource.BATTLE_ACTIONS_REMAINING_TEXT_2
        );

        areYouSureContainer.setDebug(StageUIRenderingSystem.DEBUG);
        areYouSureContainer.setWidth(stage.getWidth());
        areYouSureContainer.setHeight(stage.getHeight());
        areYouSureContainer.align(Align.center);
        areYouSureContainer.setVisible(false);
        areYouSureContainer.setTouchable(Touchable.enabled);
        areYouSureContainer.setBackground(new TextureRegionDrawable(atlas.findRegion(TextureStrings.BLOCK)).tint(new Color(0, 0, 0, 0.85f)));

        nextTurnBanner = new Table(uiSkin);


        skillDescriptionTable = new Table(uiSkin);
        skillDescriptionTable.setVisible(false);

        attackIndicatorTable = new Table(uiSkin);
        attackIndicatorTable.setBackground(NinePatches.getBorderNinePatchDrawable(renderingSystem.getAtlas(),
                new Color(Colors.ENEMY_BULLET_COLOR),
                0.35f
        ));
        attackIndicatorTable.setVisible(false);
        attackIndicatorTable.addAction(new Action() {
            @Override
            public boolean act(float delta) {

                if(!skillDescriptionTable.isVisible()){
                    attackIndicatorTable.setVisible(false);
                }

                return false;
            }
        });

        bottomContainer.setSkin(uiSkin);
        bottomContainer.setWidth(stage.getWidth());
        bottomContainer.setHeight(BOTTOM_TABLE_HEIGHT);
        bottomContainer.align(Align.bottomLeft);
        bottomContainer.setTransform(false);
        bottomContainer.setVisible(false);
        applyNinePatchToTable(bottomContainer);
        bottomContainer.add(characterProfileTable).padBottom(Padding.SMALL);


        tableForSkillButtons = new Table(uiSkin);
        applyNinePatchToTable(tableForSkillButtons);
        tableForSkillButtons.setDebug(StageUIRenderingSystem.DEBUG);

        populateBottomContainer();


        rightSideContainer = new Table();
        rightSideContainer.setWidth(Measure.units(35f));
        rightSideContainer.setHeight(Measure.units(27.5f));
        rightSideContainer.setPosition(stage.getWidth() - Measure.units(37.5f), Measure.units(22.5f));

        moraleTable = new Table(uiSkin);
        moraleTable.setBackground(NinePatches.getBorderNinePatchDrawable(atlas, Colors.AMOEBA_FAST_PURPLE));
        moraleTable.setDebug(StageUIRenderingSystem.DEBUG);

        populateRightSideContainer();



        skillButtonButtonGroup.setMinCheckCount(0);
        skillButtonButtonGroup.setMaxCheckCount(1);
        skillButtonButtonGroup.setUncheckLast(true);


        stage.addActor(rightSideContainer);
        stage.addActor(bottomContainer);
        stage.addActor(attackIndicatorTable);
        stage.addActor(areYouSureContainer);

        tutorialInformationWindow = new Table(uiSkin);
        tutorialInformationWindow.setVisible(false);
        //tutorialInformationWindow.setDebug(true);
        tutorialInformationWindow.setBackground(NinePatches.getBorderNinePatchDrawable(renderingSystem.getAtlas(), Colors.TUTORIAL_TABLE_OUTLINE));
        tutorialInformationWindow.setTouchable(Touchable.enabled);
        tutorialInformationWindow.addListener(new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y) {
                tutorialInformationWindow.setVisible(false);
            }
        });

        tutorialArrowTable = new Table(uiSkin);
        tutorialArrowTable.setVisible(false);

        stage.addActor(nextTurnBanner);

        //Tutorial TABLES
        stage.addActor(tutorialArrowTable);
        stage.addActor(tutorialInformationWindow);


    }


    private void populateAreYouSureContainer(){

        if(areYouSureContainer.hasChildren()){
            areYouSureContainer.reset();
        }

        areYouSureContainer.setVisible(true);
        areYouSureContainer.addAction(new Action() { //Ensures, units can not be interacted with when this Container is visible
            @Override
            public boolean act(float delta) {
                if(areYouSureContainer.isVisible()){
                    battleScreenInputSystem.restrictInputToStage();
                } else {
                    battleScreenInputSystem.unRestrictInput();
                    return true;
                }
                return false;
            }
        });

        areYouSureContainer.update();
    }


    ///RIGHT SIDE OF THE BATTLE SCREEN ///

    /// MORALE, OBJECTIVES AND END TURN ////

    private void populateRightSideContainer(){

        rightSideContainer.clear();

        rightSideContainer.setDebug(StageUIRenderingSystem.DEBUG);

        //MORALE
        rightSideContainer.add(moraleTable).padBottom(Padding.MEDIUM).fill();
        rightSideContainer.row();
        populateMoraleTable();


        objectivesTable = new Table(uiSkin);
        //objectivesTable.setHeight(Measure.units(27.5f));
        objectivesTable.setBackground(NinePatches.getDefaultNinePatch(renderingSystem.getAtlas()));
        objectivesTable.setDebug(StageUIRenderingSystem.DEBUG);

        rightSideContainer.add(objectivesTable).width(Measure.units(35f)).padBottom(Padding.MEDIUM);
        rightSideContainer.row();

        endTurn = new TextButton(TextResource.BATTLE_END_TURN, uiSkin);
        endTurn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(turnSystem.isAllActionsComplete()){
                    turnSystem.endAllyTurn();
                    resetBottomContainer();
                } else {
                    populateAreYouSureContainer();
                }
            }
        });

        endTurn.addAction(endTurnButtonAction());

        rightSideContainer.add(endTurn).width(Measure.units(20f)).height(Measure.units(7.5f)).expandX();
    }


    /**
     * Action for The End Turn Button. It hides itself when actions are Queued or when it is not the player's turn.
     */
    private Action endTurnButtonAction(){
        return new Action() {
            @Override
            public boolean act(float delta) {
                if (actionQueueSystem.isProcessing() || !turnSystem.isTurn(TurnSystem.TURN.PLAYER)) {
                    endTurn.setVisible(false);
                } else {
                    endTurn.setVisible(true);
                }
                return false;
            }
        };
    }



    private void populateMoraleTable(){

        moraleTable.clear();
        Label morale = new Label(String.format(Locale.ENGLISH,TextResource.PARTY_MORALE + ": %d/%d", playerPartyManagementSystem.getPartyDetails().getMorale(), PartyDetails.MAX_MORALE), uiSkin);
        morale.setAlignment(Align.center);
        moraleTable.add(morale).fill().align(Align.center);

    }



    public void updateObjectiveTable(BattleEvent battleEvent){

        if(objectivesTable.hasChildren()){
            objectivesTable.clear();
        }

        objectivesTable.add(new Label(TextResource.BATTLE_OBJECTIVES, uiSkin)).expandX().padBottom(Padding.SMALL);
        objectivesTable.row();
        objectivesTable.add(new Label(battleEvent.getPrimaryObjective().getDescription(), uiSkin)).padBottom(Padding.SMALL);;
        objectivesTable.row();

        if(battleEvent.getBonusObjectives().length > 0) {
            objectivesTable.add(new Label(TextResource.BATTLE_BONUS, uiSkin)).padBottom(Padding.SMALL);
            ;

            for (AbstractObjective o : battleEvent.getBonusObjectives()) {
                objectivesTable.row();
                objectivesTable.add(new Label(o.getDescription(), uiSkin, Fonts.SMALL_FONT_STYLE_NAME,
                        o.isFailed(world) ? new Color(Color.GRAY) : new Color(Color.WHITE)
                )).padBottom(Padding.SMALL);
                ;
            }

        }
    }



    /// BOTTOM OF BATTLE SCREEN ///
    /// CHARACTER PORTRAITS AND SKILLS ///

    private void populateBottomContainer(){

        bottomContainer.clear();

        switch (state){

            case DEPLOYING_UNITS:

                bottomContainer.setVisible(true);
                deploymentTable.setSkin(uiSkin);
                bottomContainer.add(deploymentTable).width(stage.getWidth() - Measure.units(5f)).height(BOTTOM_TABLE_HEIGHT);
                populateDeploymentTable();

                break;

            case BATTLE:

                bottomContainer.setVisible(false);
                bottomContainer.setBackground((Drawable) null);
                //Table for displaying the characters name and profiles
                characterProfileTable = new Table(uiSkin);
                applyNinePatchToTable(characterProfileTable);
                bottomContainer.add(characterProfileTable).width(Measure.units(20f)).height(BOTTOM_TABLE_HEIGHT).padRight(Padding.MEDIUM).expandX().fillX();

                //Table for Skill buttons.

                bottomContainer.add(tableForSkillButtons).width(Measure.units(22.5f)).height(BOTTOM_TABLE_HEIGHT).padRight(Padding.MEDIUM).expandX();


                //Table used to show Skill Information. Is hidden until a skill is pressed.
                skillDescriptionTable.clear();
                skillDescriptionTable.setDebug(true);
                applyNinePatchToTable(skillDescriptionTable);
                skillDescriptionTable.setDebug(StageUIRenderingSystem.DEBUG);
                skillDescriptionTable.setVisible(false);
                bottomContainer.add(skillDescriptionTable).width(Measure.units(45f)).height(BOTTOM_TABLE_HEIGHT).expandY().expandX();

                break;


        }

    }


    /**
     * Used within the Deployment Phase To display which hero will be deployed when a play presses
     * a highlighted square
     */
    private void populateDeploymentTable(){

        if (deploymentTable.hasChildren()) {
            deploymentTable.clear();
        }

        UnitData unitData = battleDeploymentSystem.getDeployingUnit();
        deploymentTable.setWidth(stageUIRenderingSystem.stage.getWidth());

        Label deployingLabel = new Label(TextResource.BATTLE_DEPLOYMENT + ": ", uiSkin);

        deploymentTable.add(deployingLabel).pad(Padding.SMALL);

        deploymentTable.add(new Image(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(unitData.icon))))
                .size(Measure.units(7.5f), Measure.units(7.5f));

    }



    private void applyNinePatchToTable(Table table){
        table.setBackground(NinePatches.getDefaultNinePatch(renderingSystem.getAtlas()));
    }



    @Override
    protected void processSystem() {

    }



    private void createNextTurnBanner(TurnSystem.TURN turn){

        nextTurnBanner.reset();
        //nextTurnBanner.getActions().clear();
        nextTurnBanner.getColor().set(new Color(Color.WHITE));
        String text;

        switch (turn){

            default:
            case ENEMY:
            case INTENT:

                text = TextResource.BATTLE_ENEMY_TURN;
                nextTurnBanner.setBackground(NinePatches.getBorderNinePatchDrawable(renderingSystem.getAtlas(), new Color(Color.RED)));
                break;

            case PLAYER:
                text = TextResource.BATTLE_ALLY_TURN;
                nextTurnBanner.setBackground(NinePatches.getBorderNinePatchDrawable(renderingSystem.getAtlas(), new Color(Colors.TABLE_BORDER_COLOR)));
                break;
        }

        float height = Measure.units(7.5f);
        float width = stage.getWidth() + Measure.units(20f);

        nextTurnBanner.setVisible(true);
        nextTurnBanner.setWidth(width);
        nextTurnBanner.setHeight(height);
        nextTurnBanner.setPosition(CenterMath.centerOnPositionX(width, stage.getWidth() / 2), CenterMath.centerOnPositionY(height, stage.getHeight() / 2 + Measure.units(5f)));
        nextTurnBanner.align(Align.center);
        nextTurnBanner.add(new Label(text, uiSkin));

        final Action a = Actions.fadeOut(1.25f, Interpolation.smoother);

        nextTurnBanner.addAction(a);

        actionQueueSystem.pushLastAction(actionQueueEntity, new WorldConditionalAction() {
            @Override
            public boolean condition(World world, Entity entity) {
                return nextTurnBanner.getColor().a <= 0.1f;
            }

            @Override
            public void performAction(World world, Entity entity) {

            }
        });

    }



    /**
     * Populates the bottom of the scene with a HUD that displays the selected character's name and skills.
     * You can interact with the skills to view what they do and show where they will target on the battle map.
     */
    public void setUpSelectedCharacterHUD(Entity e) {

        if(pcm.has(e)){
            botTabState = BottomTableState.PLAYER_SELECT;
        } else if(em.has(e)){
            botTabState = BottomTableState.ENEMY_SELECT;
            world.getSystem(UtilityAiSystem.class).createDebugScoreTools(e);
        }

        createTargetReticle(world, e);

        UnitData unitData = e.getComponent(UnitComponent.class).getUnitData();
        bottomContainer.setVisible(true);
        tableForSkillButtons.setVisible(true);

        Label name = new Label(unitData.name, uiSkin);


        switch (botTabState){

            case PLAYER_SELECT:
                createMovementTiles(e);
                characterProfileTable.clear();
                characterProfileTable.add(name).height(Measure.units(5f)).center().expandX();
                characterProfileTable.row();
                characterProfileTable.add(new Image(new TextureRegionDrawable(atlas.findRegion(unitData.icon)))).size(PROFILE_PICTURE_SIZE).expandY();


                tableForSkillButtons.clear();
                tableForSkillButtons.setTouchable(Touchable.enabled);
                tableForSkillButtons.addListener(new ClickListener(){}); //Empty click listener to avoid menu closing, if a player misses tapping a skill


                characterProfileTable.setBackground(NinePatches.getDefaultNinePatch(atlas));
                skillDescriptionTable.setBackground(NinePatches.getDefaultNinePatch(atlas));

                skillButtonButtonGroup.clear();
                SkillsComponent skillsComponent = e.getComponent(SkillsComponent.class);
                for (int i = 0; i < skillsComponent.skills.size; i++) {
                    createSkillButton(e, skillsComponent.skills.get(i));
                }

                break;


            case ENEMY_SELECT:

                characterProfileTable.clear();
                characterProfileTable.add(name).height(Measure.units(5f)).center().expandX();
                characterProfileTable.row();
                characterProfileTable.add(new Image(new TextureRegionDrawable(atlas.findRegion(unitData.icon)))).size(PROFILE_PICTURE_SIZE).expandY();

                characterProfileTable.setBackground(NinePatches.getBorderNinePatchDrawable(atlas, new Color(Colors.ENEMY_BULLET_COLOR)));
                skillDescriptionTable.setBackground(NinePatches.getBorderNinePatchDrawable(atlas, new Color(Colors.ENEMY_BULLET_COLOR)));

                populateSkillInformationTableForEnemies(e);
                break;





        }


        //skillButtonButtonGroup.uncheckAll();
    }


    /**
     * When An enemy is clicked The Skill Button And Description TABLE Are Automatically Shown.
     * @param enemy
     */
    private void populateSkillInformationTableForEnemies(Entity enemy){

        tableForSkillButtons.clear();

        if(!storedm.has(enemy)){
            skillDescriptionTable.clear();
            skillDescriptionTable.setVisible(false);
            //skillDescriptionTable.add(new Label(TextResource.BATTLE_NO_ENEMY_ATTACK, uiSkin, Fonts.LABEL_STYLE_SMALL_FONT));
            tableForSkillButtons.setVisible(false);
        } else {

            skillDescriptionTable.clear();
            skillDescriptionTable.setVisible(true);
            skillDescriptionTable.add(new Label(TextResource.BATTLE_ENEMY_ATTACK, uiSkin, Fonts.LABEL_STYLE_SMALL_FONT)).expandY();
            tableForSkillButtons.setVisible(false);

            StoredSkillComponent storedSkillComponent = storedm.get(enemy);

            attackIndicatorTable.setWidth(tileSystem.getMinimumCellSize() * 0.4f);
            attackIndicatorTable.setHeight(tileSystem.getMinimumCellSize() * 0.4f);

            //Checks whether to display the damage a unit will deal to an area.
            if(tileSystem.getCoordinateMap().containsKey(storedSkillComponent.storedTargetCoordinates)){

                for(Entity e : tileSystem.getCoordinateMap().get(storedSkillComponent.storedTargetCoordinates)){


                    if(hm.has(e)){

                        attackIndicatorTable.setVisible(true);

                        attackIndicatorTable.clearChildren();
                        attackIndicatorTable.add(new Label("" + storedSkillComponent.skill.getBaseDamage(), uiSkin, Fonts.LABEL_STYLE_SMALL_FONT));

                        Vector2 center = tileSystem.getRectangleUsingCoordinates(storedSkillComponent.storedTargetCoordinates).getCenter(new Vector2());

                        attackIndicatorTable.setPosition(CenterMath.centerOnPositionX(attackIndicatorTable.getWidth(), center.x),
                                CenterMath.centerOnPositionY(attackIndicatorTable.getHeight(), center.y)
                                );

                        break;
                    }

                }

            }

        }




    }


    /**
     * This is called when a player presses A Skill In The Skill Button Table.
     *
     * It sets this Table To Visible and displays The Skill's name and description
     *
     * @param player - Player Entity
     * @param skill - Skill of Skill Button Pressed
     */
    private void updateSkillText(Entity player, Skill skill) {

        skillDescriptionTable.clear();
        skillDescriptionTable.setVisible(true);
        skillDescriptionTable.setDebug(StageUIRenderingSystem.DEBUG);

        Label skillName = new Label(skill.getName(), uiSkin);
        skillDescriptionTable.align(Align.top);
        skillDescriptionTable.add(skillName).height(Measure.units(5f)).center().expandX().colspan(3);;
        skillDescriptionTable.row();

        description = new Label(skill.getDescription(world, player), uiSkin, Fonts.LABEL_STYLE_SMALL_FONT);
        description.setWrap(true);
        description.setText(skill.getDescription(world, player));
        description.setAlignment(Align.center);

        skillDescriptionTable.add(description).width(skillDescriptionTable.getWidth()).expandY().expandX();

    }


    /**
     * Creates the targeting of a give skill and displays it to the player.
     * @param unit
     * @param skill
     */
    private void createSkillTargeting(Entity unit, Skill skill) {
        this.clearTargetingUI();
        skill.createTargeting(world, unit);
    }

    /**
     * Upon being selected creates UsesMoveAction and Attacking tiles for the player, based on
     * the avaliablity of an entites attack and movement actions
     *
     * @param e
     */
    private void createMovementTiles(Entity e) {
        AvailableActionsCompnent availableActionsCompnent = e.getComponent(AvailableActionsCompnent.class);
        if (availableActionsCompnent.movementActionAvailable) {
            new TargetingFactory().createMovementTiles(world, e, e.getComponent(UnitComponent.class).getUnitData().getMovementRange());
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

        Stack stack = new Stack();

        Drawable drawable = new TextureRegionDrawable(atlas.findRegion(skill.getIcon()));
        final Button btn = new Button(uiSkin, Styles.BUTTON_STYLE_TOGGLE);

        btn.addListener(new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y) {

                if(btn.isChecked()) {
                    updateSkillText(player,
                            skill);

                    createSkillTargeting(player,
                            skill);
                } else { //Remove skill targets and return to showing movement targets
                    skillDescriptionTable.clear();
                    skillDescriptionTable.setVisible(false);
                    clearTargetingUI();
                    createMovementTiles(player);
                }
            }
        });

        Table table = new Table();
        Image image = new Image(drawable);
        image.setTouchable(Touchable.disabled);
        table.add(image).size(SKILL_BUTTON_SIZE - Padding.SMALL);


        stack.add(btn);
        skillButtonButtonGroup.add(btn);
        stack.add(table);
        tableForSkillButtons.add(stack).width(SKILL_BUTTON_SIZE).height(SKILL_BUTTON_SIZE).pad(Measure.units(1.5f)).center().expandX().expandY();

    }


    public void createVictoryRewards(BattleEvent battleEvent, PartyDetails partyDetails){

        endTurn.setDisabled(true);

        Table victoryTable = stageUIRenderingSystem.createContainerTable();
        stageUIRenderingSystem.stage.addActor(victoryTable);

        victoryTable.setTouchable(Touchable.enabled);
        victoryTable.addListener(new ClickListener());


        Table t = new Table(uiSkin);
        t.setBackground(new NinePatchDrawable(NinePatches.getBorderPatch(renderingSystem.getAtlas(),
                new Color(Colors.TUTORIAL_TABLE_OUTLINE.r, Colors.TUTORIAL_TABLE_OUTLINE.g, Colors.TUTORIAL_TABLE_OUTLINE.b, 0.75f))));
        t.align(Align.center);

        victoryTable.add(t).width(Measure.units(85f)).height(Measure.units(45f));

        Label title = new Label(TextResource.BATTLE_VICTORY, uiSkin);
        t.add(title).padTop(Padding.SMALL).colspan(12);
        t.row();

        int money = 0;
        int morale = 0;
        int reputation = REP_REWARD;

        switch (battleEvent.getPrimaryObjective().getReward()){
            case MONEY: money++;
            break;
            case MORALE: morale++;
            break;
        }

        createRewardLine(t, TextResource.BATTLE_VICTORY_REWARD, money, morale, reputation);

        money = 0;
        morale = 0;
        reputation = 0;

        for(AbstractObjective bonusObjective : battleEvent.getBonusObjectives()) {

            switch (bonusObjective.getReward()) {
                case MONEY:
                    money++;
                    break;
                case MORALE:
                    morale++;
                    break;
            }

            reputation += REP_BONUS_REWARD;

        }

        if(battleEvent.getBonusObjectives().length > 0){
            createRewardLine(t, TextResource.BATTLE_VICTORY_BONUS, money, morale, reputation);
        }

        t.row();

        TextButton textButton = new TextButton(TextResource.BATTLE_VICTORY_CONTINUE, uiSkin);
        textButton.setBackground(new NinePatchDrawable(NinePatches.getBorderPatch(renderingSystem.getAtlas(), new Color(Colors.ENEMY_BULLET_COLOR))));
        t.add(textButton).height(Measure.units(7.5f)).width(Measure.units(20f)).padBottom(Padding.SMALL).colspan(12);

        textButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {

                Screen menu = ((BattleScreen) game.getScreen()).getPreviousScreen();

                if(menu instanceof CharacterSelectionScreen){
                    game.getScreen().dispose();
                    game.setScreen(menu);
                }else {
                    game.getScreen().dispose();
                    game.setScreen(menu);
                    ((MapScreen) menu).battleVictory();
                }

            }
        });




    }


    private void createRewardLine(Table t, String title, int money, int morale, int reputation){


        Label reward = new Label(title + ":", uiSkin);
        t.add(reward).pad(Padding.MEDIUM);

        if(money > 0) {

            Table iconTextRight = new Table();
            iconTextRight.add(new Image(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(TextureStrings.ICON_MONEY)))).size(Measure.units(5f));
            iconTextRight.add(new Label("" + money, uiSkin)).padRight(Padding.MEDIUM);
            t.add(iconTextRight);
        }

        if(morale > 0)
            t.add(new Label(TextResource.PARTY_MORALE + " +" + morale, uiSkin)).padRight(Padding.MEDIUM);

        t.add(new Label(TextResource.PARTY_REPUTATION + " +" + reputation, uiSkin)).padRight(Padding.MEDIUM);

        t.row();

        playerPartyManagementSystem.editMoney(money);
        playerPartyManagementSystem.editMorale(morale);
        playerPartyManagementSystem.editReputation(reputation);

    }


    public void createDefeatScreen() {

        Table gameCompleteTable = stageUIRenderingSystem.createContainerTable();
        stageUIRenderingSystem.stage.addActor(gameCompleteTable);

        gameCompleteTable.setTouchable(Touchable.enabled);
        gameCompleteTable.addListener(new ClickListener());

        Table t = new Table(uiSkin);
        t.setBackground(new NinePatchDrawable(NinePatches.getBorderPatch(renderingSystem.getAtlas(),
                new Color(Colors.ENEMY_BULLET_COLOR.r, Colors.ENEMY_BULLET_COLOR.g, Colors.ENEMY_BULLET_COLOR.b, 0.75f))));
        t.align(Align.center);

        gameCompleteTable.add(t).width(Measure.units(85f)).height(Measure.units(45f));

        String[] strings = {
                TextResource.DEFEAT_TEXT,
                TextResource.DEFEAT_TEXT_2,
                TextResource.DEFEAT_TEXT_3
        };

        Label title = new Label(TextResource.DEFEAT, uiSkin);
        t.add(title).padTop(Padding.SMALL);
        t.row();

        for(String s : strings){
            Label text = new Label(s, uiSkin);
            t.add(text).padBottom(Padding.SMALL).expandY();
            t.row();
        }

        TextButton textButton = new TextButton(TextResource.DEFEAT_BACK_TO_MAIN_MENU, uiSkin);
        textButton.setBackground(new NinePatchDrawable(NinePatches.getBorderPatch(renderingSystem.getAtlas(), new Color(Colors.ENEMY_BULLET_COLOR))));
        t.add(textButton).height(Measure.units(7.5f)).padBottom(Padding.SMALL);

        textButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.getScreen().dispose();
                game.setScreen(new MenuScreen(game));
            }
        });

    }


    /**
     * Clears the button entities and selected entity from the system
     */
    public void clearTargetingUI() {
        IntBag bag = world.getAspectSubscriptionManager().get(Aspect.all(UITargetingComponent.class)).getEntities();
        for (int i = 0; i < bag.size(); i++) {
            world.getEntity(bag.get(i)).deleteFromWorld();
        }
    }

    public void resetBottomContainer() {
        this.clearTargetingUI();
        bottomContainer.setVisible(false);
        populateBottomContainer();
    }




    /// BATTLE MAP OPERATIONS //

    private void createTargetReticle(World world, Entity entity) {

        float width = tileSystem.getMinimumCellSize() * RECTICLE_SCALE;
        float height = tileSystem.getMinimumCellSize() * RECTICLE_SCALE;

        CenteringBoundComponent centeringBoundComponent = entity.getComponent(CenteringBoundComponent.class);


        Entity recticle = world.createEntity();
        recticle.edit().add(new PositionComponent())
                .add(new GrowAndShrinkTransformationComponent(1.05f))
                .add(new UITargetingComponent())
                .add(new FollowPositionComponent(entity.getComponent(PositionComponent.class).position,
                        CenterMath.offsetX(centeringBoundComponent.bound.width, width),
                        CenterMath.offsetY(centeringBoundComponent.bound.height, height)))
                .add(new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE, new TextureDescription.Builder(TextureStrings.TARGETING)
                        .width(width)
                        .height(height)
                        .color(pcm.has(entity) ? new Color(Color.WHITE) : new Color(Color.RED))
                        .build()));

    }






    //// TUTORIAL OPERATIONS ///


    public Table createTutorialWindow(Rectangle rectangleToCenterOn, TutorialSystem.TutorialState tutorialState) {

        Rectangle centerRect;
        Vector2 center;
        float width;
        float height;

        Label title;
        Label text1;
        Label text2;
        Label text3;


        tutorialInformationWindow.clearChildren();
        tutorialInformationWindow.align(Align.center);

        switch (tutorialState){


            case BANNER:

                endTurn.setVisible(false); //Hide End Turn Button Until The appropriate part of the tutorials
                endTurn.getActions().clear();

                Vector2 pos = moraleTable.localToStageCoordinates(new Vector2());
                centerRect = new Rectangle(pos.x, pos.y, moraleTable.getWidth(), moraleTable.getHeight());

                width = Measure.units(35f);
                height = Measure.units(32.5f);

                buildDefaultTutorialWindow(tutorialInformationWindow,
                        centerRect,
                        width,
                        height,
                        0,
                        -Measure.units(20f),
                        TextResource.TUTORIAL_MORALE_TITLE,
                        TextResource.TUTORIAL_MORALE_TEXT_1,
                        TextResource.TUTORIAL_MORALE_TEXT_2
                );

                buildTutorialArrow(tutorialArrowTable, tutorialInformationWindow, centerRect);

                break;


            case ALLIED_STRUCTURE:

                width = Measure.units(55f);
                height = Measure.units(21f);

                buildDefaultTutorialWindow(tutorialInformationWindow,
                        rectangleToCenterOn,
                        width,
                        height,
                        0,
                        -Measure.units(17.5f),
                        TextResource.TUTORIAL_ALLIED_STRUCTURE_TITLE,
                        TextResource.TUTORIAL_ALLIED_STRUCTURE_TEXT_1,
                        TextResource.TUTORIAL_ALLIED_STRUCTURE_TEXT_2
                );


                buildTutorialArrow(tutorialArrowTable, tutorialInformationWindow, rectangleToCenterOn);

                break;



            case ENEMY_FIRST_MOVE:

                width = Measure.units(45f);
                height = Measure.units(30f);

                buildDefaultTutorialWindow(tutorialInformationWindow,
                        rectangleToCenterOn,
                        width,
                        height,
                        Measure.units(27.5f),
                        0,
                        TextResource.TUTORIAL_ENEMIES_TITLE,
                        TextResource.TUTORIAL_ENEMIES_TEXT_1,
                        TextResource.TUTORIAL_ENEMIES_TEXT_2,
                        TextResource.TUTORIAL_ENEMIES_TEXT_3
                );

                buildTutorialArrow(tutorialArrowTable, tutorialInformationWindow, rectangleToCenterOn);

                break;

            case HERO_EXPLANATION_AND_PLAYER_FIRST_MOVE:


                width = Measure.units(42.5f);
                height = Measure.units(25f);

                buildDefaultTutorialWindow(tutorialInformationWindow,
                        rectangleToCenterOn,
                        width,
                        height,
                        Measure.units(27.5f),
                        0,
                        TextResource.TUTORIAL_HEROES_TITLE,
                        TextResource.TUTORIAL_HEROES_TEXT_1,
                        TextResource.TUTORIAL_HEROES_TEXT_2
                );

                buildTutorialArrow(tutorialArrowTable, tutorialInformationWindow, rectangleToCenterOn);

                break;


            case MOVE_HERE_PROMPT:

                center = rectangleToCenterOn.getCenter(new Vector2());

                width = Measure.units(15);
                height = Measure.units(5f);

                tutorialInformationWindow.setVisible(true);
                //tutorialInformationWindow.setHeight(Measure.units(30f));
                tutorialInformationWindow.setWidth(width);
                tutorialInformationWindow.setHeight(height);
                tutorialInformationWindow.setPosition(CenterMath.centerOnPositionX(width, center.x), CenterMath.centerOnPositionY(height, center.y + Measure.units(7.5f)));

                width = Measure.units(8.5f);

                text1 = new Label(TextResource.TUTORIAL_MOVE_HERE, uiSkin, Fonts.LABEL_STYLE_SMALL_FONT);
                tutorialInformationWindow.add(text1).expandX().align(Align.center);



                break;


            case SKILLS:

                tableForSkillButtons.validate();
                tableForSkillButtons.setVisible(true);
                pos = tableForSkillButtons.localToStageCoordinates(new Vector2(0, 0));

                width = Measure.units(45f);
                height = Measure.units(27.5f);

                Rectangle skillsTableRectangle =  new Rectangle(pos.x, pos.y, tableForSkillButtons.getWidth(), tableForSkillButtons.getHeight());

                buildDefaultTutorialWindow(tutorialInformationWindow,
                        skillsTableRectangle,
                        width,
                        height,
                        0,
                        Measure.units(25),
                        TextResource.TUTORIAL_SKILLS_TITLE,
                        TextResource.TUTORIAL_SKILLS_TEXT_1,
                        TextResource.TUTORIAL_SKILLS_TEXT_2,
                        TextResource.TUTORIAL_SKILLS_TEXT_3);

                buildTutorialArrow(tutorialArrowTable, tutorialInformationWindow, skillsTableRectangle);

                break;

            case PUSHING:

                endTurn.addAction(endTurnButtonAction());

                width = Measure.units(45f);
                height = Measure.units(27.5f);

                buildDefaultTutorialWindow(tutorialInformationWindow,
                        rectangleToCenterOn,
                        width,
                        height,
                        Measure.units(27.5f),
                        0,
                        TextResource.TUTORIAL_PUSHING_TITLE,
                        TextResource.TUTORIAL_PUSHING_TEXT_1,
                        TextResource.TUTORIAL_PUSHING_TEXT_2
                );


                buildTutorialArrow(tutorialArrowTable, tutorialInformationWindow, rectangleToCenterOn);

                break;

            case RANGED_PLAYER_ARRIVES:

                width = Measure.units(45f);
                height = Measure.units(25f);

                buildDefaultTutorialWindow(tutorialInformationWindow,
                        rectangleToCenterOn,
                        width,
                        height,
                        Measure.units(32.5f),
                        0,
                        TextResource.TUTORIAL_RANGED_ATTACK_TITLE,
                        TextResource.TUTORIAL_RANGED_ATTACK_TEXT_1,
                        TextResource.TUTORIAL_RANGED_ATTACK_TEXT_2
                );

                buildTutorialArrow(tutorialArrowTable, tutorialInformationWindow, rectangleToCenterOn);

                break;


            case THROWN_PLAYER_ARRIVES:

                width = Measure.units(50f);
                height = Measure.units(22.5f);

                buildDefaultTutorialWindow(tutorialInformationWindow,
                        rectangleToCenterOn,
                        width,
                        height,
                        Measure.units(32.5f),
                        0,
                        TextResource.TUTORIAL_AERIAL_ATTACK,
                        TextResource.TUTORIAL_AERIAL_ATTACK_TEXT_1,
                        TextResource.TUTORIAL_AERIAL_ATTACK_TEXT_2
                );

                buildTutorialArrow(tutorialArrowTable, tutorialInformationWindow, rectangleToCenterOn);

                break;


            case OBJECTIVES:

                objectivesTable.validate();
                pos = objectivesTable.localToStageCoordinates(new Vector2(0, 0));

                width = Measure.units(55f);
                height = Measure.units(22.5f);

                Rectangle objTableRect = new Rectangle(pos.x, pos.y, objectivesTable.getWidth(), objectivesTable.getHeight());

                buildDefaultTutorialWindow(tutorialInformationWindow,
                        objTableRect,
                        width,
                        height,
                        -Measure.units(47.5f),
                        0,
                        TextResource.TUTORIAL_OBJECTIVES_TITLE,
                        TextResource.TUTORIAL_OBJECTIVES_TEXT_1,
                        TextResource.TUTORIAL_OBJECTIVES_TEXT_2
                        );

                buildTutorialArrow(tutorialArrowTable, tutorialInformationWindow, objTableRect);

                break;


            case END:

                tutorialInformationWindow.reset();

                width = Measure.units(55f);
                height = Measure.units(25f);


                buildDefaultTutorialWindow(tutorialInformationWindow,
                        new Rectangle(0, 0, stage.getWidth(), stage.getHeight()),
                        width,
                        height,
                        0,
                        0,
                        TextResource.TUTORIAL_END_TITLE,
                        TextResource.TUTORIAL_END_TEXT_1,
                        TextResource.TUTORIAL_END_TEXT_2
                );

                tutorialInformationWindow.addListener(new ClickListener(){

                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.getScreen().dispose();
                        game.setScreen(new CharacterSelectionScreen(game));
                        PlayerSave.turnOffFirstTimePlayer();
                    }

                });

        }

        return tutorialInformationWindow;



    }

    private void buildDefaultTutorialWindow(Table table, Rectangle toCenterOn, float width, float height, float offsetX, float offsetY, String title, String... text){

        Vector2 center = toCenterOn.getCenter(new Vector2());

        table.setVisible(true);
        table.setWidth(width);
        table.setHeight(height);
        table.align(Align.top);
        table.setHeight(height);
        table.setPosition(CenterMath.centerOnPositionX(width, center.x) + offsetX, CenterMath.centerOnPositionY(height, center.y + offsetY));


        Label labelTitle = new Label(title, uiSkin);

        width = width - Measure.units(2.5f);

        table.add(labelTitle).width(width).padBottom(Padding.SMALL).padTop(Padding.SMALL);
        table.row();

        for(String s : text){
            Label textLabel = new Label(s, uiSkin, Fonts.LABEL_STYLE_SMALL_FONT);
            textLabel.setWrap(true);
            table.add(textLabel).width(width).padBottom(Padding.SMALL);
            table.row();
        }


        Label clickToDismiss = new Label(TextResource.TUTORIAL_CLICK_TO_DISMISS, uiSkin, Fonts.LABEL_STYLE_SMALL_FONT);
        clickToDismiss.setWrap(true);
        clickToDismiss.setAlignment(Align.center);
        table.add(clickToDismiss).width(width);
    }



    private void buildTutorialArrow(Table arrowTable, Table fromTable, Rectangle to){
        buildTutorialArrow(arrowTable, fromTable, to, 0);
    }


    private void buildTutorialArrow(final Table arrowTable, final Table fromTable, Rectangle to, float sizeOffset){

        arrowTable.clear();
        arrowTable.getActions().clear();

        Vector2 fromCenter = new Rectangle(fromTable.getX(), fromTable.getY(), fromTable.getWidth(), fromTable.getHeight()).getCenter(new Vector2()); ;

        Vector2 toCenter = to.getCenter(new Vector2());

        float size = fromCenter.dst(toCenter);
        float barWidth = Measure.units(1f);


        int rotation = (int) AngleMath.angleOfTravelInDegrees(fromCenter.x, fromCenter.y, toCenter.x, toCenter.y) - 90;

        float minus;

        switch (rotation){
            default:
            case 0:
            case 180:
                minus = to.height / 2;
                break;
            case 90:
            case -90:
                minus = to.width / 2;
        }


        arrowTable.setHeight(size - minus + sizeOffset);
        arrowTable.setWidth(barWidth);
        arrowTable.setPosition(CenterMath.centerOnPositionX(arrowTable.getWidth(), fromCenter.x), fromCenter.y);
        arrowTable.add(new Image(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(TextureStrings.BLOCK))
                .tint(new Color(Colors.TUTORIAL_TABLE_OUTLINE)))).width(barWidth).height(arrowTable.getHeight());



        arrowTable.setTransform(true);
        arrowTable.setOrigin(arrowTable.getWidth() / 2, 0);
        arrowTable.setRotation((float) rotation);

        arrowTable.setVisible(true);


        arrowTable.addAction(new Action() {
            @Override
            public boolean act(float delta) {

                if(!fromTable.isVisible()){
                    getActor().setVisible(false);
                    return true;
                }

                return false;
            }
        });
        //tutorialInformationWindow.setVisible(false);


    }



























}
