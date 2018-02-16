package com.bryjamin.dancedungeon.ecs.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.MainGame;
import com.bryjamin.dancedungeon.assets.Skins;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.SkillButtonComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.UITargetingComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.BattleMessageSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.ecs.systems.graphical.RenderingSystem;
import com.bryjamin.dancedungeon.ecs.systems.ui.StageUIRenderingSystem;
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
    private RenderingSystem renderingSystem;
    private StageUIRenderingSystem stageUIRenderingSystem;
    private Table container = new Table();
    private Table skillsTable = new Table();
    private Table infoTable = new Table();
    private MainGame game;
    private Skin uiSkin;

    public SkillUISystem(MainGame game) {
        super(Aspect.all(SkillButtonComponent.class, CenteringBoundaryComponent.class));
        this.game = game;
        this.uiSkin = Skins.DEFAULT_SKIN(game.assetManager);
    }


    @Override
    protected void processSystem() {

    }


    public void createSkillUi(Entity e) {

        Stage stage = stageUIRenderingSystem.stage;

        container.remove();

        container = new Table(uiSkin);
        container.setDebug(true);
        container.setWidth(stage.getWidth());
        container.setHeight(Measure.units(17.5f));
        container.align(Align.bottom);

        skillsTable.remove();
        skillsTable = new Table(uiSkin);

        infoTable.remove();
        infoTable = new Table(uiSkin);

        container.add(skillsTable).width(Measure.units(50f));
        container.add(infoTable).width(Measure.units(30f)).height(Measure.units(17.5f));

        stageUIRenderingSystem.stage.addActor(container);

        SkillsComponent skillsComponent = e.getComponent(SkillsComponent.class);
        for (int i = 0; i < skillsComponent.skills.size; i++) {
            createSkillButton(world.createEntity(), e, Measure.units(15f) * (i + 1), 0, skillsComponent.skills.get(i));
        }
    }

    public void refreshSkillUi(Entity e) {
        container.remove();
        //createSkillUi(e);
    }


    private void createCreateSkillText(Entity player, Skill skill) {

/*        Table skillDescription = new Table(uiSkin);

        container.add(skillDescription).width(Measure.units(20f));*/

        Label title = new Label(skill.getName(), uiSkin);
        Label descrption = new Label(skill.getDescription(world, player), uiSkin);
        descrption.setWrap(true);
        descrption.setAlignment(Align.center);

        infoTable.clear();
        infoTable.align(Align.top);
        infoTable.add(title);
        infoTable.row();
        infoTable.add(descrption).width(infoTable.getWidth());

/*
        TextFactory.createCenteredText(world.createEntity(), Fonts.MEDIUM, skill.getName(),
                tileSystem.getOriginX(),
                tileSystem.getOriginY() - Measure.units(5f),
                tileSystem.getWidth(),
                Measure.units(5f))
                .edit().add(new UITargetingComponent());


        HighlightedText ht = skill.getHighlight(world, player);

        if (ht != null) {
            Entity description = TextFactory.createCenteredText(world.createEntity(), Fonts.SMALL, ht.getText(),
                    tileSystem.getOriginX(),
                    tileSystem.getOriginY() - Measure.units(7.5f),
                    tileSystem.getWidth(),
                    Measure.units(3.5f));

            description.edit().add(new HighLightTextComponent(ht.getHighlightArray()));
            description.edit().add(new UITargetingComponent());
        } else {
            Entity description = TextFactory.createCenteredText(world.createEntity(), Fonts.MEDIUM, skill.getDescription(world, player),
                    tileSystem.getOriginX(),
                    tileSystem.getOriginY() - Measure.units(7.5f),
                    tileSystem.getWidth(),
                    Measure.units(3.5f));
            description.edit().add(new UITargetingComponent());
        }
*/

    }


    public boolean touch(float x, float y) {

        for (Entity e : this.getEntities()) {

            SkillButtonComponent sbc = e.getComponent(SkillButtonComponent.class);

            if (e.getComponent(HitBoxComponent.class).contains(x, y) && sbc.enabled) {
                createCreateSkillText(sbc.getEntity(),
                        sbc.getSkill());

                createSkillTarget(sbc.getEntity(),
                        sbc.getSkill());

                return true;
            }
        }
        return false;
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

        Drawable drawable = new TextureRegionDrawable(renderingSystem.getAtlas().findRegion(skill.getIcon()));
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

/*        button.edit().add(new PositionComponent(x, y))
                .add(new SkillButtonComponent(player, skill))
                .add(new CenteringBoundaryComponent(SIZE, SIZE))
                .add(new HitBoxComponent(new HitBox(new Rectangle(x, y, SIZE, SIZE))))
                .add(new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE, new TextureDescription.Builder(skill.getIcon())
                        .size(SIZE)
                        .build()))
                .add(new ConditionalActionsComponent(new WorldConditionalAction() {

                    boolean isCanCastCondition = false;

                    @Override
                    public boolean condition(World world, Entity entity) {

                        if (isCanCastCondition) {
                            isCanCastCondition = false;
                            return skill.canCast(world, player);
                        } else {
                            isCanCastCondition = true;
                            return !skill.canCast(world, player);
                        }

                    }

                    @Override
                    public void performAction(World world, Entity entity) {

                        if (!isCanCastCondition) {
                            entity.getComponent(DrawableComponent.class).drawables.getColor().a = 1f;
                            entity.getComponent(SkillButtonComponent.class).enabled = true;
                        } else {
                            entity.getComponent(DrawableComponent.class).drawables.getColor().a = 0.1f;
                            entity.getComponent(SkillButtonComponent.class).enabled = false;
                        }
                    }
                }));


        if (skill.getSpellCoolDown() == Skill.SpellCoolDown.OverTime && skill.getCoolDownTracker() > 0) {

            Entity coolDownText = world.createEntity();
            coolDownText.edit()
                    .add(new PositionComponent(x, y))
                    .add(new SkillButtonComponent())
                    .add(new UITargetingComponent())
                    .add(new DrawableComponent(Layer.ENEMY_LAYER_FAR,
                            new TextDescription.Builder(Fonts.MEDIUM)
                                    .text(Integer.toString(skill.getCoolDownTracker()))
                                    .width(SIZE)
                                    .height(SIZE)
                                    .build()));

        }*/


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
        container.remove();
    }


}
