package com.bryjamin.dancedungeon.ecs.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.World;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ConditionalActionsComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.SkillButtonComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.HighLightTextComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.UITargetingComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.BattleMessageSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.SelectedTargetSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.factories.TextFactory;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.texture.HighlightedText;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

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

    public SkillUISystem() {
        super(Aspect.all(SkillButtonComponent.class, CenteringBoundaryComponent.class));
    }


    @Override
    protected void processSystem() {

    }


    public void createSkillUi(Entity e) {

        SkillsComponent skillsComponent = e.getComponent(SkillsComponent.class);

        for (int i = 0; i < skillsComponent.skills.size; i++) {
           // if (i == 0) createCreateSkillText(e, skillsComponent.skills.get(i));
            createSkillButton(world.createEntity(), e, Measure.units(25f) * (i + 1), 0, skillsComponent.skills.get(i));
        }

    }


    private void createCreateSkillText(Entity e, Skill skill) {

        TextFactory.createCenteredText(world.createEntity(), Fonts.MEDIUM, skill.getName(),
                tileSystem.getOriginX(),
                tileSystem.getOriginY() - Measure.units(5f),
                tileSystem.getWidth(),
                Measure.units(5f))
                .edit().add(new UITargetingComponent());


        HighlightedText ht = skill.getHighlight(world, e);

        if (ht != null) {
            Entity description = TextFactory.createCenteredText(world.createEntity(), Fonts.SMALL, ht.getText(),
                    tileSystem.getOriginX(),
                    tileSystem.getOriginY() - Measure.units(7.5f),
                    tileSystem.getWidth(),
                    Measure.units(3.5f));

            description.edit().add(new HighLightTextComponent(ht.getHighlightArray()));
            description.edit().add(new UITargetingComponent());
        }

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
        world.getSystem(SelectedTargetSystem.class).clearTargetingTiles();
        Array<Entity> entityArray = skill.createTargeting(world, unit);

        if (entityArray.size <= 0) {
            world.getSystem(BattleMessageSystem.class).createWarningMessage();
        }

    }


    public void clearButtons() {
        for (Entity e : this.getEntities()) {
            e.deleteFromWorld();
        }
    }


    private void createSkillButton(Entity button, final Entity player, float x, float y, final Skill skill) {


        System.out.println(skill.getName());
        System.out.println(skill.getIcon());

        button.edit().add(new PositionComponent(x, y))
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
                            entity.getComponent(DrawableComponent.class).drawables.first().getColor().a = 1f;
                            entity.getComponent(SkillButtonComponent.class).enabled = true;
                        } else {
                            entity.getComponent(DrawableComponent.class).drawables.first().getColor().a = 0.1f;
                            entity.getComponent(SkillButtonComponent.class).enabled = false;
                        }
                    }
                }));

    }


}
