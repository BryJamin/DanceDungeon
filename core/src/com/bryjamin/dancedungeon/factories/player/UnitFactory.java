package com.bryjamin.dancedungeon.factories.player;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.CenteringBoundComponent;
import com.bryjamin.dancedungeon.ecs.components.HitBoxComponent;
import com.bryjamin.dancedungeon.ecs.components.PositionComponent;
import com.bryjamin.dancedungeon.ecs.components.VelocityComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.BuffComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.CoordinateComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.DeploymentComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.HealthComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.MoveToComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.UnPushableComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.ai.TargetComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.player.SkillsComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.BlinkOnHitComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.FadeComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.AffectMoraleComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.EnemyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.FriendlyComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.PlayerControlledComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.SolidComponent;
import com.bryjamin.dancedungeon.ecs.components.identifiers.UnitComponent;
import com.bryjamin.dancedungeon.ecs.systems.battle.TileSystem;
import com.bryjamin.dancedungeon.utils.HitBox;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 23/12/2017.
 */

public class UnitFactory {



    private static final float UNIT_BOUNDS_SIZE = Measure.units(4.5f);

    public Entity baseTileBag(World world, Coordinates c){

        Entity e = world.createEntity();

        e.edit().add(new PositionComponent())
                .add(new SolidComponent())
                .add(new UnPushableComponent())
                .add(new CoordinateComponent(c))
                .add(new MoveToComponent())
                .add(new VelocityComponent())
                .add(new DrawableComponent(Layer.ENEMY_LAYER_MIDDLE, new TextureDescription.Builder(TextureStrings.WALL)
                        //.offsetX()
                        .size(Measure.units(4f)).build()))
                .add(new CenteringBoundComponent(new Rectangle(0, 0, Measure.units(4f), Measure.units(4f))));

        return e;

    }


    public Entity baseAlliedTileBag(World world, Coordinates c){
        Entity e = baseTileBag(world, c);
       // e.edit().remove(UnPushableComponent.class);
        e.edit().add(new HealthComponent(2));
        e.edit().add(new AffectMoraleComponent());
        e.edit().add(new FriendlyComponent());
        e.edit().add(new BlinkOnHitComponent());


        UnitData unitData = new UnitData();
        unitData.setHealth(2);
        unitData.setHealth(2);

        e.edit().add(new UnitComponent(unitData));
        e.getComponent(DrawableComponent.class).drawables.getColor().set(new Color(Color.ORANGE));
        return e;
    }



    public Entity baseDeploymentZone(World world, Rectangle r, Coordinates c){

        Entity e = world.createEntity();

        e.edit().add(new PositionComponent(r.x, r.y))
                .add(new CoordinateComponent(c))
                .add(new HitBoxComponent(r))
                .add(new DeploymentComponent())
                .add(new DrawableComponent(Layer.ENEMY_LAYER_MIDDLE, new TextureDescription.Builder(TextureStrings.BLOCK)
                        .color(new Color(Color.BLUE))
                        .alpha(0.2f)
                        .width(r.width)
                        .height(r.height)
                        .build()))
/*                .add(new FadeComponent(new FadeComponent.FadeBuilder()
                        .fadeIn(true)
                        .alpha(0.17f)
                        .minAlpha(0.15f)
                        .maxAlpha(0.55f)
                        .maximumTime(1.5f)));*/
                .add(new FadeComponent(
                        new FadeComponent.FadeBuilder()
                                .fadeIn(true)
                                .minAlpha(0.15f)
                                .maxAlpha(0.85f)
                               // .endless(true)
                                .maximumTime(1.5f)));
        return e;
    }

    public Entity baseUnitBag(World world, UnitData unitData){

        float size = TileSystem.CELL_SIZE * unitData.getDrawScale();

        Entity e = world.createEntity();

        e.edit().add(new PositionComponent());
        e.edit().add(new UnitComponent(unitData));
        e.edit().add(new SolidComponent());

        e.edit().add(new HealthComponent(unitData.getHealth(), unitData.getMaxHealth()));
        e.edit().add(new CoordinateComponent());
        e.edit().add(new MoveToComponent(Measure.units(unitData.getMapMovementSpeed())));
        e.edit().add(new VelocityComponent());
        e.edit().add(new TargetComponent());
        e.edit().add(new BuffComponent());

        //Graphical
        e.edit().add(new BlinkOnHitComponent());
        e.edit().add(new SkillsComponent(unitData.getSkills()));
        e.edit().add(new TurnComponent());


        e.edit().add(new CenteringBoundComponent(size, size));
        e.edit().add(new HitBoxComponent(new HitBox(size, size)));

        e.edit().add(new DrawableComponent(Layer.PLAYER_LAYER_MIDDLE,
                new TextureDescription.Builder(unitData.icon)
                        .size(size)
                        .build()));


        return e;

    }


}
