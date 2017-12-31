package com.bryjamin.dancedungeon.factories.spells.basic;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.bryjamin.dancedungeon.assets.Colors;
import com.bryjamin.dancedungeon.assets.TextureStrings;
import com.bryjamin.dancedungeon.ecs.components.battle.StatComponent;
import com.bryjamin.dancedungeon.ecs.components.battle.TurnComponent;
import com.bryjamin.dancedungeon.ecs.components.graphics.DrawableComponent;
import com.bryjamin.dancedungeon.factories.spells.SkillDescription;
import com.bryjamin.dancedungeon.factories.spells.animations.BasicProjectile;
import com.bryjamin.dancedungeon.utils.Measure;
import com.bryjamin.dancedungeon.utils.math.Coordinates;
import com.bryjamin.dancedungeon.utils.texture.Layer;
import com.bryjamin.dancedungeon.utils.texture.TextureDescription;

/**
 * Created by BB on 24/12/2017.
 */

public class MageAttack extends SkillDescription {


    @Override
    public Array<Entity> createTargeting(World world, Entity player) {
        return new Array<Entity>();
    }

    @Override
    public void cast(World world, Entity entity, Coordinates target) {

        float size = Measure.units(2.5f);

        new BasicProjectile.BasicProjectileBuilder()
                .drawableComponent(new DrawableComponent(Layer.FOREGROUND_LAYER_MIDDLE,
                        new TextureDescription.Builder(TextureStrings.BLOCK)
                                .color(new Color(Colors.PLAYER_BULLET_COLOR))
                                .width(size)
                                .height(size)
                                .build()))
                .width(size)
                .speed(Measure.units(150f))
                //.speed(Measure.units(0f))
                .height(size)
                .damage(entity.getComponent(StatComponent.class).magic)
                .build()
                .cast(world, entity, target);

        entity.getComponent(TurnComponent.class).attackActionAvailable = false;
        entity.getComponent(TurnComponent.class).movementActionAvailable = false;

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
        return entity.getComponent(TurnComponent.class).attackActionAvailable = false;
    }



}
