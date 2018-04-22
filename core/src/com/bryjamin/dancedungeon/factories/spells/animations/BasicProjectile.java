package com.bryjamin.dancedungeon.factories.spells.animations;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bryjamin.dancedungeon.assets.Colors;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.VelocityComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ConditionalActionsComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.OnDeathActionsComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeadComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.ActionQueueSystem;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.factories.spells.Skill;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 24/12/2017.
 *
 * Class for building a projectile that travels in a straight line towards it target
 *
 * What it does upon reaching the target (death) is defined outside this class
 *
 */

public class BasicProjectile implements SpellAnimation {

    private enum Drawable {
        DEFAULT, MISSLE, TRAIN
    }

    private float width = Measure.units(5f);
    private float height = Measure.units(5f);
    private float speed = Measure.units(80f);

    private Drawable drawable = Drawable.DEFAULT;

    public BasicProjectile(){}

   public void cast(World world, final Entity caster, final Skill skill, final Coordinates casterCoordinates, final Coordinates target){

       Vector2 casterCenter = world.getSystem(TileSystem.class).createRectangleUsingCoordinates(casterCoordinates).getCenter(new Vector2());

       float x = CenterMath.centerOnPositionX(width, casterCenter.x);
       float y = CenterMath.centerOnPositionY(height, casterCenter.y);

       Vector2 targetCenter = world.getSystem(TileSystem.class).createRectangleUsingCoordinates(target).getCenter(new Vector2());

       Entity projectile = world.createEntity();
       projectile.edit().add(new PositionComponent(x, y));

       //TODO move this into the new Action Camera System
       world.getSystem(ActionQueueSystem.class).createDeathWaitAction(projectile); //Wait for the projectile to die. To remove the action

       projectile.edit().add(new MoveToComponent(speed, new Vector3(
               CenterMath.centerOnPositionX(width, targetCenter.x),
               CenterMath.centerOnPositionY(height, targetCenter.y),
               0)));

       projectile.edit().add(createDrawableComponent());


       projectile.edit().add(new VelocityComponent());
       projectile.edit().add(new CenteringBoundComponent(width, height));

       projectile.edit().add(new ConditionalActionsComponent(new WorldConditionalAction() {
           @Override
           public boolean condition(World world, Entity entity) {
               return entity.getComponent(MoveToComponent.class).isEmpty();
           }

           @Override
           public void performAction(World world, Entity entity) {
               entity.edit().remove(ConditionalActionsComponent.class);
               entity.edit().add(new DeadComponent());
           }
       }));


       projectile.edit().add(new OnDeathActionsComponent(new WorldAction() {
           @Override
           public void performAction(World world, Entity entity) {
               skill.castSpellOnTargetLocation(skill.getSkillId(), world, caster, casterCoordinates, target);
           }
       }));


   }


   private DrawableComponent createDrawableComponent(){

        switch (drawable){
            default:
            case DEFAULT:
                return new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE,
                        new TextureDescription.Builder(TextureStrings.BLOCK)
                                .color(new Color(Colors.BOMB_ORANGE))
                                .width(width)
                                .height(height)
                                .build());
        }


   }












}
