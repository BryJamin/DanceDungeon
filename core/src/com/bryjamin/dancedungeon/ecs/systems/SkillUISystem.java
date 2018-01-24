package com.bryjamin.dancedungeon.ecs.systems;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.SkillButtonComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.HighLightTextComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.UITargetingComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.factories.TextFactory;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.factories.spells.SpellFactory;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.bag.BagToEntity;
import com.bryjamin.dancedungeon.utils.texture.HighlightedText;

/**
 * Created by BB on 23/01/2018.
 *
 * Used to create the Skill UI, when an entity is selected.
 *
 * It can also be called to update and remove certain parts of the skill UI.
 *
 */

public class SkillUISystem extends EntitySystem {

    private TileSystem tileSystem;

    public SkillUISystem() {
        super(Aspect.all(SkillButtonComponent.class, CenteringBoundaryComponent.class));
    }


    @Override
    protected void processSystem() {

    }


    public void createSkillUi(Entity e){

        SkillsComponent skillsComponent = e.getComponent(SkillsComponent.class);

        for (int i = 0; i < skillsComponent.skills.size; i++) {

            if(i == 0) createCreateSkillText(e, skillsComponent.skills.get(i));

            BagToEntity.bagToEntity(world.createEntity(), new SpellFactory().skillButton(Measure.units(25f) * (i + 1), 0,
                    skillsComponent.skills.get(i), e));
        }

    }



    private void createCreateSkillText(Entity e, Skill skill){

        TextFactory.createCenteredText(world.createEntity(), Fonts.MEDIUM, skill.getName(),
                tileSystem.getOriginX(),
                tileSystem.getOriginY() - Measure.units(5f),
                tileSystem.getWidth(),
                Measure.units(5f))
                .edit().add(new UITargetingComponent());


        HighlightedText ht = skill.getHighlight(world, e);

        if(ht != null) {
            Entity description = TextFactory.createCenteredText(world.createEntity(), Fonts.SMALL, ht.getText(),
                    tileSystem.getOriginX(),
                    tileSystem.getOriginY() - Measure.units(7.5f),
                    tileSystem.getWidth(),
                    Measure.units(3.5f));

            description.edit().add(new HighLightTextComponent(ht.getHighlightArray()));
            description.edit().add(new UITargetingComponent());
        }

    }


    public boolean touch(float x, float y){

        for(Entity e : this.getEntities()) {
            if (e.getComponent(HitBoxComponent.class).contains(x, y)) {
                createCreateSkillText(e.getComponent(SkillButtonComponent.class).getEntity(),
                        e.getComponent(SkillButtonComponent.class).getSkill());
                return true;
            }
        }
        return false;
    }









}
