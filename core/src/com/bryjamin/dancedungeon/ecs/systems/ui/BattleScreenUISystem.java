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
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
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
import com.bryjamin.dancedungeon.assets.FileStrings;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.assets.NinePatches;
import com.bryjamin.dancedungeon.assets.Padding;
import com.bryjamin.dancedungeon.assets.Skins;
import com.bryjamin.dancedungeon.assets.TextResource;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundComponent;
import com.bryjamin.dancedungeon.ecs.components.FollowPositionComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.StoredSkillComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.ScaleTransformationComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UITargetingComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.ecs.systems.action.BattleWorldInputHandlerSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionQueueSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BattleDeploymentSystem;
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
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.observer.Observer;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;


/**
 * Created by BB on 23/01/2018.
 * <p>
 * Used to create the Skill UI, when an entity is selected.
 * <p>
 * It can also be called to update and remove certain parts of the skill UI.
 */

public class BattleScreenUISystem extends BaseSystem implements Observer {

    private TurnSystem turnSystem;
    private ActionQueueSystem actionQueueSystem;
    private BattleDeploymentSystem battleDeploymentSystem;
    private BattleWorldInputHandlerSystem battleWorldInputHandlerSystem;
    private StageUIRenderingSystem stageUIRenderingSystem;
    private RenderingSystem renderingSystem;
    private TileSystem tileSystem;


    private ComponentMapper<PlayerControlledComponent> pcm;
    private ComponentMapper<EnemyComponent> em;
    private ComponentMapper<StoredSkillComponent> storedm;

    private static final float SKILL_BUTTON_SIZE = Measure.units(7.5f);
    private static final float PROFILE_PICTURE_SIZE = Measure.units(5.5f);


    private static final float RECTICLE_SCALE = 1.5f;

    private enum State {
        BATTLE, DEPLOYING_UNITS
    }

    private State state = State.DEPLOYING_UNITS;


    private enum BottomTableState {
        PLAYER_SELECT, ENEMY_SELECT, ALLY_SELECT //TODO maybe.
    }

    private BottomTableState botTabState;

    //Deployment State
    private final Table deploymentTable = new Table();

    private final Table bottomContainer = new Table();
    private Table areYouSureContainer = new Table();





    //Battle State
    private Table tableForSkillButtons;
    private Table skillInformationTable;
    private Table characterProfileTable;
    private Table objectivesTable;
    private Table objectivesAndButtonContainer;


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

        if(o.getClass().equals(BattleDeploymentSystem.class)){
            populateDeploymentTable();

            System.out.println("Here");

            if(!((BattleDeploymentSystem) o).isProcessing()){
                state = State.BATTLE;

                System.out.println("Not here");
                populateBottomContainer();
            }
        }

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
                resetBottomContainer();
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

        battleDeploymentSystem.getObservers().addObserver(this);

        areYouSureContainer = new Table(uiSkin);
        areYouSureContainer.setDebug(StageUIRenderingSystem.DEBUG);
        areYouSureContainer.setWidth(stage.getWidth());
        areYouSureContainer.setHeight(stage.getHeight());
        areYouSureContainer.align(Align.center);
        areYouSureContainer.setVisible(false);
        areYouSureContainer.setTouchable(Touchable.enabled);
        areYouSureContainer.setBackground(new TextureRegionDrawable(atlas.findRegion(TextureStrings.BLOCK)).tint(new Color(0, 0, 0, 0.95f)));


        bottomContainer.setSkin(uiSkin);
        bottomContainer.setWidth(stage.getWidth());
        bottomContainer.setHeight(BOTTOM_TABLE_HEIGHT);
        bottomContainer.align(Align.bottomLeft);
        bottomContainer.setTransform(false);
        bottomContainer.setVisible(false);
        applyNinePathToTable(bottomContainer);
        bottomContainer.add(characterProfileTable).padBottom(Padding.SMALL);
        populateBottomContainer();


        objectivesAndButtonContainer = new Table();
        objectivesAndButtonContainer.setWidth(Measure.units(35f));
        objectivesAndButtonContainer.setHeight(Measure.units(27.5f));
        objectivesAndButtonContainer.setPosition(stage.getWidth() - Measure.units(37.5f), Measure.units(22.5f));
        populateObjectivesAndButtons();

        skillButtonButtonGroup.setMinCheckCount(0);
        skillButtonButtonGroup.setMaxCheckCount(1);
        skillButtonButtonGroup.setUncheckLast(true);


        stage.addActor(objectivesAndButtonContainer);
        stage.addActor(bottomContainer);
        stage.addActor(areYouSureContainer);


    }


    private void populateObjectivesAndButtons(){

        objectivesAndButtonContainer.clear();

        objectivesTable = new Table(uiSkin);
        //objectivesTable.setHeight(Measure.units(27.5f));
        objectivesTable.setBackground(new NinePatchDrawable(NinePatches.getBorderPatch(renderingSystem.getAtlas())));
        objectivesTable.setDebug(StageUIRenderingSystem.DEBUG);

        objectivesAndButtonContainer.add(objectivesTable).width(Measure.units(35f)).padBottom(Padding.MEDIUM);
        objectivesAndButtonContainer.row();

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

        endTurn.addAction(new Action() {
            @Override
            public boolean act(float delta) {
                if (actionQueueSystem.isProcessing() || !turnSystem.isTurn(TurnSystem.TURN.ALLY)) {
                    endTurn.setVisible(false);
                } else {
                    endTurn.setVisible(true);
                }
                return false;
            }
        });

        objectivesAndButtonContainer.add(endTurn).width(Measure.units(20f)).height(Measure.units(7.5f)).expandX();
    }

    private void populateBottomContainer(){

        bottomContainer.clear();

        switch (state){

            case DEPLOYING_UNITS:

                bottomContainer.setVisible(true);
                deploymentTable.setSkin(uiSkin);
                bottomContainer.add(deploymentTable).width(stage.getWidth()).height(BOTTOM_TABLE_HEIGHT);
                populateDeploymentTable();

                break;

            case BATTLE:

                bottomContainer.setVisible(false);
                bottomContainer.setBackground((Drawable) null);
                //Table for displaying the characters name and profiles
                characterProfileTable = new Table(uiSkin);
                applyNinePathToTable(characterProfileTable);
                bottomContainer.add(characterProfileTable).width(Measure.units(20f)).height(BOTTOM_TABLE_HEIGHT).padRight(Padding.MEDIUM).expandX().fillX();

                //Table for Skill buttons.
                tableForSkillButtons = new Table(uiSkin);
                applyNinePathToTable(tableForSkillButtons);
                tableForSkillButtons.setDebug(StageUIRenderingSystem.DEBUG);
                bottomContainer.add(tableForSkillButtons).width(Measure.units(22.5f)).height(BOTTOM_TABLE_HEIGHT).padRight(Padding.MEDIUM).expandX();


                //Table used to show Skill Information. Is hidden until a skill is pressed.
                skillInformationTable = new Table(uiSkin);
                applyNinePathToTable(skillInformationTable);
                skillInformationTable.setDebug(StageUIRenderingSystem.DEBUG);
                skillInformationTable.setVisible(false);
                bottomContainer.add(skillInformationTable).width(Measure.units(45f)).height(BOTTOM_TABLE_HEIGHT).expandY();


                break;


        }

    }


    private void populateDeploymentTable(){

        if (deploymentTable.hasChildren()) {
            deploymentTable.clear();
        }

        UnitData unitData = battleDeploymentSystem.getDeployingUnit();

        // deploymentTable.setDebug(true);
        deploymentTable.setWidth(stageUIRenderingSystem.stage.getWidth());

        //NinePatch patch = new NinePatch(renderingSystem.getAtlas().findRegion(TextureStrings.BORDER), 4, 4, 4, 4);
        //deploymentTable.setBackground(new NinePatchDrawable(NinePatches.getBorderPatch(renderingSystem.getAtlas())));

        Label deployingLabel = new Label("Please Select Where To Deploy: ", uiSkin);

        deploymentTable.add(deployingLabel).pad(Padding.SMALL);

        deploymentTable.add(new Image(new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(unitData.icon))))
                .size(Measure.units(7.5f), Measure.units(7.5f));

    }



    private void applyNinePathToTable(Table table){
        table.setBackground(new NinePatchDrawable(NinePatches.getBorderPatch(renderingSystem.getAtlas())));
    }



    @Override
    protected void processSystem() {

    }


    public void updateObjectiveTable(BattleEvent battleEvent){

        if(objectivesTable.hasChildren()){
            objectivesTable.clear();
        }

        objectivesTable.add(new Label(TextResource.BATTLE_OBJECTIVES, uiSkin)).expandX().padBottom(Padding.SMALL);
        objectivesTable.row();
        objectivesTable.add(new Label(battleEvent.getPrimaryObjective().getDescription(), uiSkin)).padBottom(Padding.SMALL);;
        objectivesTable.row();
        objectivesTable.add(new Label(TextResource.BATTLE_BONUS, uiSkin)).padBottom(Padding.SMALL);;

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
    public void setUpSelectedCharacterHUD(Entity e) {

        if(pcm.has(e)){
            botTabState = BottomTableState.PLAYER_SELECT;
        } else if(em.has(e)){
            botTabState = BottomTableState.ENEMY_SELECT;
        }

        createMovementTiles(e);
        createTargetReticle(world, e);

        UnitData unitData = e.getComponent(UnitComponent.class).getUnitData();
        bottomContainer.setVisible(true);

        Label name = new Label(unitData.name, uiSkin);


        switch (botTabState){

            case PLAYER_SELECT:

                characterProfileTable.clear();
                characterProfileTable.add(name).height(Measure.units(5f)).center().expandX();
                characterProfileTable.row();
                characterProfileTable.add(new Image(new TextureRegionDrawable(atlas.findRegion(unitData.icon)))).size(PROFILE_PICTURE_SIZE).expandY();


                tableForSkillButtons.clear();
                tableForSkillButtons.setTouchable(Touchable.enabled);
                tableForSkillButtons.addListener(new ClickListener(){}); //Empty click listener to avoid menu closing, if a player misses tapping a skill

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
                skillInformationTable.setVisible(true);
                populateSkillInformationTableForEnemies(e);
                break;





        }


        //skillButtonButtonGroup.uncheckAll();
    }


    private void populateSkillInformationTableForEnemies(Entity enemy){

        if(!storedm.has(enemy)){
            skillInformationTable.clear();
            skillInformationTable.add(new Label(TextResource.BATTLE_NO_ENEMY_ATTACK, uiSkin, Fonts.LABEL_STYLE_SMALL_FONT));
            tableForSkillButtons.setVisible(false);
        } else {

            Skill skill = storedm.get(enemy).skill;

            Stack stack = new Stack();
            Drawable drawable = new TextureRegionDrawable(atlas.findRegion(skill.getIcon()));
            final Button btn = new Button(uiSkin, "inventory");
            btn.setChecked(true);
            btn.setTouchable(Touchable.disabled);

            Table table = new Table();
            Image image = new Image(drawable);
            image.setTouchable(Touchable.disabled);
            table.add(image).size(SKILL_BUTTON_SIZE - Padding.SMALL);


            stack.add(btn);
            stack.add(table);
            tableForSkillButtons.add(stack).width(SKILL_BUTTON_SIZE).height(SKILL_BUTTON_SIZE).pad(Measure.units(1.5f)).center().expandX().expandY();

            updateSkillText(enemy, skill);


        }




    }


    private void updateSkillText(Entity player, Skill skill) {

        skillInformationTable.setVisible(true);
        skillInformationTable.clear();
        skillInformationTable.setDebug(true);

        Label skillName = new Label(skill.getName(), uiSkin);
        skillInformationTable.align(Align.top);
        skillInformationTable.add(skillName).height(Measure.units(5f)).center().expandX().colspan(3);;
        skillInformationTable.row();

        description = new Label(skill.getDescription(world, player), uiSkin, Fonts.LABEL_STYLE_SMALL_FONT);
        description.setWrap(true);
        description.setText(skill.getDescription(world, player));
        description.setAlignment(Align.center);

        skillInformationTable.add(description).width(skillInformationTable.getWidth()).expandY().expandX();

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

        Stack stack = new Stack();

        Drawable drawable = new TextureRegionDrawable(atlas.findRegion(skill.getIcon()));
        final Button btn = new Button(uiSkin, "inventory");

        btn.addListener(new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y) {

                if(btn.isChecked()) {
                    updateSkillText(player,
                            skill);

                    createSkillTargeting(player,
                            skill);
                } else { //Remove skill targets and return to showing movement targets
                    skillInformationTable.clear();
                    skillInformationTable.setVisible(false);
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

                partyDetails.changeMoney(reward.getValue());
                break;
            case MORALE:
                Label gold = new Label("Morale", uiSkin);
                rewardTable.add(gold);
                Label reputationIncrease = new Label(" +" + reward.getValue(), uiSkin);
                rewardTable.add(reputationIncrease);
                partyDetails.changeMorale(reward.getValue());
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
                .add(new ScaleTransformationComponent(1.05f))
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



















}
