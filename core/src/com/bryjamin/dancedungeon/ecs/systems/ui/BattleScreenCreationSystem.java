package com.bryjamin.dancedungeon.ecs.systems.ui;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.FileStrings;
import com.bryjamin.dancedungeon.assets.Skins;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.SkillButtonComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.UITargetingComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionCameraSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BattleMessageSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TurnSystem;
import com.bryjamin.dancedungeon.factories.player.UnitData;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.utils.Measure;

/**
 * Created by BB on 23/01/2018.
 * <p>
 * Used to create the Skill UI, when an entity is selected.
 * <p>
 * It can also be called to update and remove certain parts of the skill UI.
 */

public class BattleScreenCreationSystem extends EntitySystem {

    private TurnSystem turnSystem;
    private ActionCameraSystem actionCameraSystem;

    private static final float SIZE = Measure.units(10f);
    private TileSystem tileSystem;
    private Table container = new Table();
    private Table skillsTable = new Table();
    private Table infoTable = new Table();

    private Table profileTable = new Table();

    private Label title;
    private Label description;
    private ImageButton[] buttons;

    private TextButton endTurn;

    private Stage stage;


    private MainGame game;
    private TextureAtlas atlas;
    private Skin uiSkin;

    public BattleScreenCreationSystem(Stage stage, MainGame game) {
        super(Aspect.all(SkillButtonComponent.class, CenteringBoundaryComponent.class));
        this.game = game;
        this.stage = stage;
        this.atlas = game.assetManager.get(FileStrings.SPRITE_ATLAS_FILE, TextureAtlas.class);
        this.uiSkin = Skins.DEFAULT_SKIN(game.assetManager);
    }


    @Override
    protected void initialize() {

        container = new Table(uiSkin);
        container.setDebug(true);
        container.setWidth(stage.getWidth());
        container.setHeight(Measure.units(20f));
        container.align(Align.bottomLeft);
        container.setTransform(false);

        profileTable = new Table(uiSkin);
        container.add(profileTable);




        infoTable = new Table(uiSkin);
        skillsTable = new Table(uiSkin);
        skillsTable.setDebug(true);
        container.add(skillsTable).width(Measure.units(50f));
        container.add(infoTable).width(Measure.units(30f)).height(Measure.units(17.5f));

        title = new Label("", uiSkin);
        description = new Label("", uiSkin);
        description.setWrap(true);
        description.setAlignment(Align.center);


        endTurn = new TextButton("End", uiSkin);


        endTurn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                world.getSystem(TurnSystem.class).endAllyTurn();
                world.getSystem(BattleScreenCreationSystem.class).reset();
            }
        });

        endTurn.setPosition(0, Measure.units(50f));
        endTurn.setWidth(Measure.units(12.5f));
        endTurn.setHeight(Measure.units(10f));


        stage.addActor(endTurn);

        stage.addActor(container);


    }

    @Override
    protected void processSystem() {

        System.out.println(endTurn.isDisabled());

        if(actionCameraSystem.isProcessing() || !turnSystem.isTurn(TurnSystem.TURN.ALLY)){
            //endTurn.setDisabled(true);
            endTurn.setVisible(false);
        } else {
            endTurn.setVisible(true);
            //endTurn.setDisabled(false);
        }

    }



    public void createSkillUi(Entity e) {

        container.clearChildren();
        container.clear();
        container.setVisible(true);


        UnitData unitData = e.getComponent(UnitComponent.class).getUnitData();

        Label name = new Label(unitData.name, uiSkin);
        profileTable = new Table(uiSkin);
        profileTable.add(name).height(Measure.units(5f));
        profileTable.row();

        Table profileAndHealth = new Table();
        profileAndHealth.add(new Image(new TextureRegionDrawable(atlas.findRegion(unitData.icon)))).size(Measure.units(7.5f), Measure.units(7.5f)).expandY();
        profileAndHealth.row();

        int max = unitData.getStatComponent().maxHealth;

        profileAndHealth.add(new Label(String.format("HP: %s/%s", max, max), uiSkin)).expandY();

        profileTable.add(profileAndHealth).width(Measure.units(10f)).height(Measure.units(10f));
        container.add(profileTable).width(Measure.units(20f));
        container.add(skillsTable).width(Measure.units(22.5f));

        skillsTable.clearChildren();

        skillsTable.add(new Label("Skills", uiSkin)).height(Measure.units(5f));
        skillsTable.row();

        SkillsComponent skillsComponent = e.getComponent(SkillsComponent.class);
        for (int i = 0; i < skillsComponent.skills.size; i++) {
            createSkillButton(e, Measure.units(15f) * (i + 1), 0, skillsComponent.skills.get(i));
        }
    }

    private void createCreateSkillText(Entity player, Skill skill) {

        container.clearChildren();
        createSkillUi(player);

        infoTable.remove();
        infoTable.reset();

        infoTable = new Table();
        infoTable.reset();
        infoTable.clear();

        container.add(infoTable).width(Measure.units(40f));

        infoTable.add(new Label(skill.getName(), uiSkin));
        infoTable.align(Align.top);
        infoTable.row();


        description = new Label(skill.getDescription(world, player), uiSkin);
        description.setWrap(true);
        description.setText(skill.getDescription(world, player));
        description.setAlignment(Align.center);

        infoTable.add(description).width(Measure.units(40f));

    }


    private void createSkillTarget(Entity unit, Skill skill) {
        this.clearTargetingTiles(); //TODO maybe just move to a new layer?
        Array<Entity> entityArray = skill.createTargeting(world, unit);

        if (entityArray.size <= 0) {
            world.getSystem(BattleMessageSystem.class).createWarningMessage();
        }

    }


    private void createSkillButton(final Entity player, float x, float y, final Skill skill) {

        float size = Measure.units(7.5f);

        Drawable drawable = new TextureRegionDrawable(atlas.findRegion(skill.getIcon()));
        Button btn = new Button(drawable);
        skillsTable.add(btn).width(size).height(size).pad(Measure.units(1.5f));
        btn.addListener(new ClickListener(){

            @Override
            public void clicked(InputEvent event, float x, float y) {
                createCreateSkillText(player,
                        skill);

                createSkillTarget(player,
                        skill);
            }
        });

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
        container.clearChildren();
        container.setVisible(false);
    }


}
