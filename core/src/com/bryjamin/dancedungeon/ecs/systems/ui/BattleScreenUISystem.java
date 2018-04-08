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
import com.bryjamin.dancedungeon.Observer;
import com.bryjamin.dancedungeon.assets.FileStrings;
import com.bryjamin.dancedungeon.assets.Padding;
import com.bryjamin.dancedungeon.assets.Skins;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.SkillButtonComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.UITargetingComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionCameraSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.BattleDeploymentSystem;
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

public class BattleScreenUISystem extends EntitySystem {

    private TurnSystem turnSystem;
    private ActionCameraSystem actionCameraSystem;

    private static final float SIZE = Measure.units(10f);
    private TileSystem tileSystem;
    private Table container = new Table();


    private Table skillButtonsTable = new Table();
    private Table skillInformationTable = new Table();
    private Table characterProfileAndHealthTable = new Table();

    private static final float BOTTOM_TABLE_HEIGHT = Measure.units(20f);


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


    @Override
    protected void initialize() {

        container = new Table(uiSkin);
        container.setDebug(true);
        container.setWidth(stage.getWidth());
        container.setHeight(Measure.units(15f));
        container.align(Align.bottomLeft);
        container.setTransform(false);



        container.add(characterProfileAndHealthTable);

        skillInformationTable = new Table(uiSkin);
        skillButtonsTable = new Table(uiSkin);
        skillButtonsTable.setDebug(true);

        endTurn = new TextButton("End Turn", uiSkin);


        endTurn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                world.getSystem(TurnSystem.class).endAllyTurn();
                world.getSystem(BattleScreenUISystem.class).reset();
            }
        });

        endTurn.setPosition(stage.getWidth() - Measure.units(15f), Measure.units(40f));
        endTurn.setWidth(Measure.units(15f));
        endTurn.setHeight(Measure.units(10f));


        stage.addActor(endTurn);

        stage.addActor(container);


    }

    @Override
    protected void processSystem() {

        if (actionCameraSystem.isProcessing() || !turnSystem.isTurn(TurnSystem.TURN.ALLY)) {
            endTurn.setVisible(false);
        } else {
            endTurn.setVisible(true);
        }


    }


    /**
     * Populates the bottom of the scene with a HUD that displays the selected character's name and skills.
     * You can interact with the skills to view what they do and show where they will target on the battle map.
     * @param e
     */
    public void createChaacterSkillHUD(Entity e) {

        container.clearChildren();
        container.clear();
        container.setVisible(true);


        UnitData unitData = e.getComponent(UnitComponent.class).getUnitData();

        Label name = new Label(unitData.name, uiSkin);
        characterProfileAndHealthTable = new Table(uiSkin);
        characterProfileAndHealthTable.add(name).height(Measure.units(5f)).expandY();
        characterProfileAndHealthTable.row();
        characterProfileAndHealthTable.add(new Image(new TextureRegionDrawable(atlas.findRegion(unitData.icon)))).size(Measure.units(7.5f), Measure.units(7.5f)).expandY();



        container.add(characterProfileAndHealthTable).width(Measure.units(20f));
        container.add(skillButtonsTable).width(Measure.units(22.5f));

        skillButtonsTable.clearChildren();

        skillButtonsTable.add(new Label("Skills", uiSkin)).height(Measure.units(5f));
        skillButtonsTable.row();

        SkillsComponent skillsComponent = e.getComponent(SkillsComponent.class);
        for (int i = 0; i < skillsComponent.skills.size; i++) {
            createSkillButton(e, Measure.units(15f) * (i + 1), 0, skillsComponent.skills.get(i));
        }
    }

    private void createCreateSkillText(Entity player, Skill skill) {

        container.clearChildren();
        createChaacterSkillHUD(player);

        skillInformationTable.remove();
        skillInformationTable.reset();

        skillInformationTable = new Table();
        skillInformationTable.reset();
        skillInformationTable.clear();

        container.add(skillInformationTable).width(Measure.units(60f)).expandX().fill();

        skillInformationTable.add(new Label(skill.getName(), uiSkin));
        skillInformationTable.align(Align.top);
        skillInformationTable.row();


        description = new Label(skill.getDescription(world, player), uiSkin);
        description.setWrap(true);
        description.setText(skill.getDescription(world, player));
        description.setAlignment(Align.center);

        skillInformationTable.add(description).width(Measure.units(60f));

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
        skillButtonsTable.add(btn).width(size).height(size).pad(Measure.units(1.5f));
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
