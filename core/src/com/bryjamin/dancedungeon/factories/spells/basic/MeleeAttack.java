package com.bryjamin.dancedungeon.factories.spells.basic;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ConditionalActionsComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.WaitActionComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationMapComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.AnimationStateComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeadComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 20/12/2017.
 *
 * This Melee Attack
 *
 * Used as a basic attack for enemies that do not have a ranged weapon.
 *
 * This attack deals damage based on the power of the entity using it
 *
 */

public class MeleeAttack extends Skill {


    public MeleeAttack() {
        super(new Builder()
                .name("Attack")
                .icon(TextureStrings.SKILLS_SLASH)
                .targeting(Targeting.Enemy)
                .attack(Attack.Melee));
    }


    @Override
    public void cast(World world, Entity entity, Coordinates target) {

        for(Entity e : world.getSystem(TileSystem.class).getCoordinateMap().get(target)){
            if(world.getMapper(HealthComponent.class).has(e)){
                e.getComponent(HealthComponent.class).applyDamage(entity.getComponent(StatComponent.class).power);
            }
        };

        entity.getComponent(TurnComponent.class).attackActionAvailable = false;
        entity.getComponent(TurnComponent.class).movementActionAvailable = false;

        final int SLASH_DRAWABLE_ID = 25;
        final int SLASH_ANIMATION = 0;

        Entity slash = world.createEntity();

        Rectangle rectangle = world.getSystem(TileSystem.class).createRectangleUsingCoordinates(target);

        slash.edit().add(new PositionComponent(rectangle.x, rectangle.y));
        slash.edit().add(new DrawableComponent(Layer.FOREGROUND_LAYER_FAR, new TextureDescription.Builder(TextureStrings.SKILLS_SLASH)
                .identifier(SLASH_DRAWABLE_ID)
                .width(rectangle.getWidth())
                //.color(new Color(Colors.AMOEBA_FAST_PURPLE))
                .height(rectangle.getHeight())
                //.scaleX(-1)
                .build()));
        slash.edit().add(new AnimationStateComponent().put(SLASH_DRAWABLE_ID, SLASH_ANIMATION));
        slash.edit().add(new AnimationMapComponent().put(SLASH_ANIMATION, TextureStrings.SKILLS_SLASH, 0.3f, Animation.PlayMode.NORMAL));


/*
        bag.add(new AnimationStateComponent().put(BODY_DRAWABLE, STANDING_ANIMATION));
        bag.add(new AnimationMapComponent()
                .put(STANDING_ANIMATION, TextureStrings.PLAYER, 0.4f, Animation.PlayMode.LOOP));*/


        slash.edit().add(new ConditionalActionsComponent(new WorldConditionalAction() {
            @Override
            public boolean condition(World world, Entity entity) {
                return entity.getComponent(AnimationMapComponent.class).animations.get(SLASH_ANIMATION).getAnimation().isAnimationFinished(
                        entity.getComponent(AnimationStateComponent.class).drawableIdAnimationStateMap.get(SLASH_DRAWABLE_ID).stateTime
                );
            }

            @Override
            public void performAction(World world, Entity entity) {
                entity.edit().add(new DeadComponent());
            }
        }));

        slash.edit().add(new WaitActionComponent());



    }

    @Override
    public void endTurnUpdate() {

    }

    @Override
    public String getIcon() {
        return null;
    }

    @Override
    public boolean canCast(World world, Entity entity) {
        return entity.getComponent(TurnComponent.class).attackActionAvailable;
    }
}
