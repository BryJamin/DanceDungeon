package com.bryjamin.dancedungeon.ecs.systems.battle;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.Color;
import com.bryjamin.dancedungeon.assets.Fonts;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundComponent;
import com.bryjamin.dancedungeon.ecs.components.ExpireComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.VelocityComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.BlinkOnHitComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.FadeComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.AffectMoraleComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeadComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.ecs.systems.PlayerPartyManagementSystem;
import com.bryjamin.dancedungeon.factories.unit.UnitData;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextDescription;

/**
 * Created by BB on 15/10/2017.
 *
 * Health System, tracks any damage or healing that has been accumulated during a game frame and applies it to an
 * entities actual health
 *
 */

public class HealthSystem extends EntityProcessingSystem {

    PlayerPartyManagementSystem playerPartyManagementSystem;

    ComponentMapper<UnitComponent> unitM;
    ComponentMapper<VelocityComponent> vm;
    ComponentMapper<PlayerControlledComponent> pm;
    ComponentMapper<BlinkOnHitComponent> blinkOnHitMapper;
    ComponentMapper<AffectMoraleComponent> affectMapper;
    ComponentMapper<PositionComponent> posM;
    ComponentMapper<CenteringBoundComponent> centeringM;

    @SuppressWarnings("unchecked")
    public HealthSystem() {
        super(Aspect.all(UnitComponent.class));
    }


    @Override
    protected void process(Entity e) {

        UnitData ud = unitM.get(e).getUnitData();

        boolean healthChangedflag = false;


        if(ud.getAccumulatedDamage() > 0) {

            if(blinkOnHitMapper.has(e)) {
                blinkOnHitMapper.get(e).isHit = true;
            }

            if(affectMapper.has(e)){//Damage taken by morale affected entities damage the party's morale as well
                playerPartyManagementSystem.editMorale(ud.getAccumulatedDamage());
            }

            createFloatingDamageText(world, Integer.toString(ud.getAccumulatedDamage()), new Color(Color.RED), e);
            healthChangedflag = true;
            ud.applyAccumulatedDamage();

        }

        if(ud.getAccumulatedHealing() > 0 && blinkOnHitMapper.has(e)) {
            createFloatingDamageText(world, Integer.toString(ud.getAccumulatedHealing()), new Color(Color.GREEN), e);
            ud.applyAccumulatedHealing();
            healthChangedflag = true;

        }

        if(healthChangedflag) {
            playerPartyManagementSystem.notifyObservers();

        }


        if(ud.getHealth() <= 0) e.edit().add(new DeadComponent());

    }


    /**
     * Creates the damage text that appears after an entity is damaged or healed
     */
    public void createFloatingDamageText(World world, String text, Color color, Entity entity){

        if(posM.has(entity) && centeringM.has(entity)) {

            Entity floatingTextEntity = world.createEntity();

            floatingTextEntity.edit().add(new PositionComponent(entity.getComponent(PositionComponent.class)));
            floatingTextEntity.edit().add(new CenteringBoundComponent(entity.getComponent(CenteringBoundComponent.class)));
            floatingTextEntity.edit().add(new FadeComponent.FadeBuilder()
                    .maximumDuration(0.75f)
                    .endless(false)
                    .fadeIn(false)
                    .build());
            floatingTextEntity.edit().add(new VelocityComponent(0, Measure.units(20f)));
            floatingTextEntity.edit().add(new ExpireComponent(2.0f));
            floatingTextEntity.edit().add(new DrawableComponent(Layer.BACKGROUND_LAYER_FAR,
                    new TextDescription.Builder(Fonts.SMALL)
                            .text(text)
                            .color(color)
                            .build()));

        }



    }




}

