package com.bryjamin.dancedungeon.factories.player.spells.animations;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundaryComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.VelocityComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.ConditionalActionsComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.OnDeathActionsComponent;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldAction;
import com.bryjamin.dancedungeon.ecs.components.actions.interfaces.WorldConditionalAction;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.DeadComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.CenterMath;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 13/11/2017.
 */

public class Fireball implements Skill {


    private static final int AP = 1;

    private float damage = 3;

    public Fireball(){};

    public Fireball(float damage){
        this.damage = damage;
    }


    @Override
    public void cast(World world, Entity entity, Coordinates target) {

        final TileSystem tileSystem = world.getSystem(TileSystem.class);
        PositionComponent positionComponent = entity.getComponent(PositionComponent.class);

        float size = Measure.units(5f);

        float x = CenterMath.centerPositionX(size, positionComponent.getX() + Measure.units(2.5f));
        float y = CenterMath.centerPositionY(size, positionComponent.getY() + Measure.units(2.5f));


        Rectangle r = tileSystem.getRectangleUsingCoordinates(target);

        Entity fireBall = world.createEntity();
        fireBall.edit().add(new PositionComponent(x, y));
        fireBall.edit().add(new CoordinateComponent(new Coordinates(), true));
        fireBall.edit().add(new MoveToComponent(Measure.units(60f), new Vector3(
                CenterMath.centerPositionX(size, r.getCenter(new Vector2()).x),
                CenterMath.centerPositionY(size, r.getCenter(new Vector2()).y),
                0)));

        fireBall.edit().add((new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE,
                new TextureDescription.Builder(TextureStrings.BLOCK)
                        .color(new Color(Color.ORANGE))
                        .width(size)
                        .height(size)
                        .build())));

        fireBall.edit().add(new VelocityComponent());
        fireBall.edit().add(new CenteringBoundaryComponent(new Rectangle(x, y, size, size)));

        fireBall.edit().add(new ConditionalActionsComponent(new WorldConditionalAction() {
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


        fireBall.edit().add(new OnDeathActionsComponent(new WorldAction() {
            @Override
            public void performAction(World world, Entity entity) {

                TileSystem tileSystem = world.getSystem(TileSystem.class);

                CoordinateComponent coordinateComponent = entity.getComponent(CoordinateComponent.class);

                for(Entity e : tileSystem.getCoordinateMap().get(coordinateComponent.coordinates)){
                    if(world.getMapper(HealthComponent.class).has(e)){
                        e.getComponent(HealthComponent.class).applyDamage(damage);
                    }
                };

            }
        }));



    }

}
