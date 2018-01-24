package com.bryjamin.dancedungeon.factories.spells.restorative;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
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
import com.bryjamin.dancedungeon.factories.spells.CooldownSpellDescription;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.HighlightedText;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 04/01/2018.
 */

public class Heal extends CooldownSpellDescription {


    @Override
    public Array<Entity> createTargeting(World world, Entity player) {
        Array<Entity> entityArray = new com.bryjamin.dancedungeon.factories.spells.TargetingFactory().createAllyTargetTiles(world, player, this, 3);
        return entityArray;
    }


    private int getHealValue(Entity e){
        return e.getComponent(StatComponent.class).magic;
    }

    @Override
    public void cast(World world, Entity entity, Coordinates target) {

        ready = false;

        for (Entity e : world.getSystem(TileSystem.class).getCoordinateMap().get(target)) {
            if (world.getMapper(HealthComponent.class).has(e)) {
                e.getComponent(HealthComponent.class).applyHealing(getHealValue(e));
            }
        }
        ;

        entity.getComponent(TurnComponent.class).attackActionAvailable = false;
        entity.getComponent(TurnComponent.class).movementActionAvailable = false;

        final int SLASH_DRAWABLE_ID = 25;
        final int SLASH_ANIMATION_ID = 0;

        Entity heal = world.createEntity();

        Rectangle rectangle = world.getSystem(TileSystem.class).createRectangleUsingCoordinates(target);

        heal.edit().add(new PositionComponent(rectangle.x, rectangle.y))
                .add(new DrawableComponent(Layer.FOREGROUND_LAYER_FAR, new TextureDescription.Builder(TextureStrings.SKILLS_HEAL)
                        .identifier(SLASH_DRAWABLE_ID)
                        .color(Color.GREEN)
                        .width(rectangle.getWidth())
                        .height(rectangle.getHeight())
                        .build()))
                .add(new AnimationStateComponent().put(SLASH_DRAWABLE_ID, SLASH_ANIMATION_ID))
                .add(new AnimationMapComponent().put(SLASH_ANIMATION_ID, TextureStrings.SKILLS_HEAL, 0.2f, Animation.PlayMode.NORMAL))
                .add(new ConditionalActionsComponent(new WorldConditionalAction() {
                    @Override
                    public boolean condition(World world, Entity entity) {
                        return entity.getComponent(AnimationMapComponent.class).animations.get(SLASH_ANIMATION_ID).getAnimation().isAnimationFinished(
                                entity.getComponent(AnimationStateComponent.class).drawableIdAnimationStateMap.get(SLASH_DRAWABLE_ID).stateTime
                        );
                    }

                    @Override
                    public void performAction(World world, Entity entity) {
                        entity.edit().add(new DeadComponent());
                    }
                }))
                .add(new WaitActionComponent());


    }


    @Override
    public boolean canCast(World world, Entity entity) {
        return ready;
    }

    @Override
    public String getIcon() {
        return "skills/Medicine";
    }

    @Override
    public String getName() {
        return "Recover";
    }

    @Override
    public String getDescription(World world, Entity entity) {
        return "Test Heals Allies for " + getHealValue(entity) + " damage";
    }

    @Override
    public HighlightedText getHighlight(World world, Entity entity) {
        return new HighlightedText()
                .add("Heals", new Color(Color.GREEN))
                .add(" Allies for ")
                .add(Integer.toString(getHealValue(entity)), new Color(Color.GREEN))
                .add(" damage");
    }


    /*
    @Override
    public void endTurnUpdate() {

    }*/


/*

    @Override
    public boolean canCast(World world, Entity entity) {
        return entity.getComponent(TurnComponent.class).attackActionAvailable = false;
    }
*/

}
