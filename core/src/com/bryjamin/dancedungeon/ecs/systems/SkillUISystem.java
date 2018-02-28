package com.bryjamin.dancedungeon.ecs.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
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
import com.bryjamin.dancedungeon.ecs.systems.battle.BattleMessageSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.utils.Measure;

/**
 * Created by BB on 23/01/2018.
 * <p>
 * Used to create the Skill UI, when an entity is selected.
 * <p>
 * It can also be called to update and remove certain parts of the skill UI.
 */

public class SkillUISystem extends EntitySystem {

    private static final float SIZE = Measure.units(10f);
    private TileSystem tileSystem;
    private Table container = new Table();
    private Table skillsTable = new Table();
    private Table infoTable = new Table();

    private Label title;
    private Label description;
    private ImageButton[] buttons;

    private Stage stage;


    private MainGame game;
    private TextureAtlas atlas;
    private Skin uiSkin;

    public SkillUISystem(Stage stage, MainGame game) {
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
        container.setHeight(Measure.units(17.5f));
        container.align(Align.bottom);
        container.setTransform(false);

        infoTable = new Table(uiSkin);
        Window window = new Window("Skills", uiSkin);
        skillsTable = new Table(uiSkin);
        skillsTable.setDebug(true);

        window.add(skillsTable);
        container.add(window).width(Measure.units(50f));
        container.add(infoTable).width(Measure.units(30f)).height(Measure.units(17.5f));

        title = new Label("", uiSkin);
        description = new Label("", uiSkin);
        description.setWrap(true);
        description.setAlignment(Align.center);

        infoTable.align(Align.top);
        infoTable.add(title);
        infoTable.row();
        infoTable.add(description).width(infoTable.getWidth());

        stage.addActor(container);


    }

    @Override
    protected void processSystem() {
    }



    public void createSkillUi(Entity e) {
        container.setVisible(true);
        skillsTable.clearChildren();
        SkillsComponent skillsComponent = e.getComponent(SkillsComponent.class);
        for (int i = 0; i < skillsComponent.skills.size; i++) {
            createSkillButton(world.createEntity(), e, Measure.units(15f) * (i + 1), 0, skillsComponent.skills.get(i));
        }
    }




    public void refreshSkillUi(Entity e) {
        container.setVisible(false);
        //createSkillUi(e);
    }


    private void createCreateSkillText(Entity player, Skill skill) {
        title.setText(skill.getName());
        description.remove();

        infoTable.clear();
        infoTable.add(title);
        infoTable.row();

       // description = new Label(skill.getDescription(world, player), uiSkin);
        description.setWrap(true);
        description.setText(skill.getDescription(world, player));
        description.setAlignment(Align.center);
        infoTable.add(description).width(infoTable.getWidth());
    }


    private void createSkillTarget(Entity unit, Skill skill) {
        this.clearTargetingTiles(); //TODO maybe just move to a new layer?
        Array<Entity> entityArray = skill.createTargeting(world, unit);

        if (entityArray.size <= 0) {
            world.getSystem(BattleMessageSystem.class).createWarningMessage();
        }

    }


    private void createSkillButton(Entity button, final Entity player, float x, float y, final Skill skill) {

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
        container.setVisible(false);
    }


}
